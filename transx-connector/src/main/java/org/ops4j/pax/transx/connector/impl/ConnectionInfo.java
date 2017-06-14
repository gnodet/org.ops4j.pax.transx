/**
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

import java.util.Objects;

public class ConnectionInfo {

    private ManagedConnectionInfo mci;
    private Object connection;
    private Object connectionProxy;
    private boolean unshareable;
    private boolean applicationManagedSecurity;
    private Exception trace;

    public ConnectionInfo() {
    } // ConnectionInfo constructor

    public ConnectionInfo(ManagedConnectionInfo mci) {
        this.mci = mci;
    }

    /**
     * Get the Mci value.
     * @return the Mci value.
     */
    public ManagedConnectionInfo getManagedConnectionInfo() {
        return mci;
    }

    /**
     * Set the Mci value.
     * @param mci The new Mci value.
     */
    public void setManagedConnectionInfo(ManagedConnectionInfo mci) {
        this.mci = mci;
    }

    /**
     * Get the Connection value.
     * @return the Connection value.
     */
    public Object getConnectionHandle() {
        return connection;
    }

    /**
     * Set the Connection value.
     * @param connection The new Connection value.
     */
    public void setConnectionHandle(Object connection) {
        assert this.connection == null;
        this.connection = connection;
    }

    public Object getConnectionProxy() {
        return connectionProxy;
    }

    public void setConnectionProxy(Object connectionProxy) {
        this.connectionProxy = connectionProxy;
    }

    public boolean isUnshareable() {
        return unshareable;
    }

    public void setUnshareable(boolean unshareable) {
        this.unshareable = unshareable;
    }

    public boolean isApplicationManagedSecurity() {
        return applicationManagedSecurity;
    }

    public void setApplicationManagedSecurity(boolean applicationManagedSecurity) {
        this.applicationManagedSecurity = applicationManagedSecurity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionInfo that = (ConnectionInfo) o;
        return Objects.equals(mci, that.mci) &&
               Objects.equals(connection, that.connection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mci, connection);
    }

    public void setTrace() {
        this.trace = new Exception("Stack Trace");
    }

    public Exception getTrace() {
        return trace;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("handle: ").append(connection);
        b.append(mci);
        return b.toString();
    }
} // ConnectionInfo