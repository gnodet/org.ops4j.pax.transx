/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ops4j.pax.transx.connector.impl;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;


/**
 * SinglePoolConnectionInterceptor chooses a single connection from the pool.  If selectOneAssumeMatch
 * is true, it simply returns the selected connection.
 * THIS SHOULD BE USED ONLY IF MAXIMUM SPEED IS ESSENTIAL AND YOU HAVE THOROUGLY CHECKED THAT
 * MATCHING WOULD SUCCEED ON THE SELECTED CONNECTION. (i.e., read the docs on your impl
 * to find out how matching works)
 * If selectOneAssumeMatch is false, it checks with the ManagedConnectionFactory that the
 * selected connection does match before returning it: if not it throws an exception.
 */
public class SinglePoolConnectionInterceptor extends AbstractSinglePoolConnectionInterceptor {

    private boolean selectOneAssumeMatch;

    //pool is mutable but only changed when protected by write lock on resizelock in superclass
    private final List<ManagedConnectionInfo> pool;

    public SinglePoolConnectionInterceptor(ConnectionInterceptor next,
                                           int maxSize,
                                           int minSize,
                                           Duration blockingTimeout,
                                           Duration idleTimeout,
                                           boolean backgroundValidation,
                                           Duration validatingPeriod,
                                           boolean validateOnMatch,
                                           boolean selectOneAssumeMatch) {
        super(next, maxSize, minSize, blockingTimeout, idleTimeout, backgroundValidation, validatingPeriod, validateOnMatch);
        pool = new ArrayList<>(maxSize);
        this.selectOneAssumeMatch = selectOneAssumeMatch;
    }

    protected void internalGetConnection(ConnectionInfo connectionInfo) throws ResourceException {
        synchronized (pool) {
            if (destroyed) {
                throw new ResourceException("ManagedConnection pool has been destroyed");
            }

            ManagedConnectionInfo newMCI;
            if (pool.isEmpty()) {
                next.getConnection(connectionInfo);
                connectionCount++;
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.log(Level.FINEST, "Supplying new connection MCI: " + connectionInfo.getManagedConnectionInfo() + " MC: " + connectionInfo.getManagedConnectionInfo().getManagedConnection() + " from pool: " + this);
                }
                return;
            } else {
                newMCI = pool.remove(pool.size() - 1);
            }
            if (connectionCount < minSize) {
                timer.schedule(new FillTask(connectionInfo), 10);
            }
            if (selectOneAssumeMatch) {
                connectionInfo.setManagedConnectionInfo(newMCI);
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.log(Level.FINEST, "Supplying pooled connection without checking matching MCI: " + connectionInfo.getManagedConnectionInfo() + " MC: " + connectionInfo.getManagedConnectionInfo().getManagedConnection() + " from pool: " + this);
                }
                return;
            }
            ManagedConnection matchedMC;
            try {
                ManagedConnectionInfo mci = connectionInfo.getManagedConnectionInfo();
                matchedMC = newMCI.getManagedConnectionFactory().matchManagedConnections(Collections.singleton(newMCI.getManagedConnection()),
                        mci.getSubject(),
                        mci.getConnectionRequestInfo());
            } catch (ResourceException e) {
                //something is wrong: destroy connection, rethrow, release permit
                returnConnection(new ConnectionInfo(newMCI), ConnectionReturnAction.DESTROY);
                throw e;
            }
            if (matchedMC != null) {
                connectionInfo.setManagedConnectionInfo(newMCI);
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.log(Level.FINEST, "Supplying pooled connection  MCI: " + connectionInfo.getManagedConnectionInfo() + " from pool: " + this);
                }
            } else {
                //matching failed.
                returnConnection(new ConnectionInfo(newMCI), ConnectionReturnAction.RETURN_HANDLE);
                throw new ResourceException("The pooling strategy does not match the MatchManagedConnections implementation.  Please investigate and reconfigure this pool");
            }
        }
    }

    protected void internalDestroy() {
        synchronized (pool) {
            while (!pool.isEmpty()) {
                ManagedConnection mc = pool.remove(pool.size() - 1).getManagedConnection();
                if (mc != null) {
                    try {
                        mc.destroy();
                    }
                    catch (ResourceException re) {
                        //ignore
                    }
                }
            }
        }
    }

    protected Object getPool() {
        return pool;
    }

    protected void doAdd(ManagedConnectionInfo mci) {
        pool.add(mci);
    }

    /**
     * @param mci managedConnectionInfo to remove from pool
     * @return true if mci was not in pool already, false if mci was in pool already.
     */
    protected boolean doRemove(ManagedConnectionInfo mci) {
        LOG.log(Level.INFO, "Removing " + mci + " from pool " + this);
        return !pool.remove(mci);
    }

    protected void transferConnections(int maxSize, int shrinkNow) {
        for (int i = 0; i < shrinkNow; i++) {
            ConnectionInfo killInfo = new ConnectionInfo(pool.get(0));
            internalReturn(killInfo, ConnectionReturnAction.DESTROY);
        }
    }

    public int getIdleConnectionCount() {
        return pool.size();
    }


    protected void getExpiredManagedConnectionInfos(Instant threshold, List<ManagedConnectionInfo> killList) {
        synchronized (pool) {
            for (Iterator<ManagedConnectionInfo> mcis = pool.iterator(); mcis.hasNext(); ) {
                ManagedConnectionInfo mci = mcis.next();
                if (mci.getLastUsed().isBefore(threshold)) {
                    mcis.remove();
                    killList.add(mci);
                    connectionCount--;
                }
            }
        }
    }

    protected void getManagedConnectionInfos(List<ManagedConnectionInfo> mciList) {
        synchronized (pool) {
            mciList.addAll(pool);
        }
    }

    public void info(StringBuilder s) {
        s.append(getClass().getName());
        s.append("[minSize=").append(minSize);
        s.append(",maxSize=").append(maxSize);
        s.append(",idleTimeout=").append(idleTimeout);
        s.append(",blockingTimeout=").append(blockingTimeout);
        s.append(".selectOneAssumeMatch=").append(selectOneAssumeMatch).append("]\n");
        next.info(s);
    }

}
