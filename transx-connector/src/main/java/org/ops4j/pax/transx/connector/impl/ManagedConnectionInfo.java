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

import org.ops4j.pax.transx.tm.NamedResource;

import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

public class ManagedConnectionInfo {

    private ManagedConnectionFactory managedConnectionFactory;
    private ConnectionRequestInfo connectionRequestInfo;
    private Subject subject;
    private ManagedConnection managedConnection;
    private NamedResource xares;
    private Instant lastUsed;
    private ConnectionInterceptor poolInterceptor;

    private ConnectionEventListenerImpl listener;

    public ManagedConnectionInfo(
            ManagedConnectionFactory managedConnectionFactory,
            ConnectionRequestInfo connectionRequestInfo) {
        this.managedConnectionFactory = managedConnectionFactory;
        this.connectionRequestInfo = connectionRequestInfo;
    }

    public ManagedConnectionFactory getManagedConnectionFactory() {
        return managedConnectionFactory;
    }

    public void setManagedConnectionFactory(ManagedConnectionFactory managedConnectionFactory) {
        this.managedConnectionFactory = managedConnectionFactory;
    }

    public ConnectionRequestInfo getConnectionRequestInfo() {
        return connectionRequestInfo;
    }

    public void setConnectionRequestInfo(ConnectionRequestInfo cri) {
        this.connectionRequestInfo = cri;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public ManagedConnection getManagedConnection() {
        return managedConnection;
    }

    public void setManagedConnection(ManagedConnection managedConnection) {
        assert this.managedConnection == null;
        this.managedConnection = managedConnection;
    }

    public NamedResource getXAResource() {
        return xares;
    }

    public void setXAResource(NamedResource xares) {
        this.xares = xares;
    }

    public Instant getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Instant lastUsed) {
        this.lastUsed = lastUsed;
    }

    public void setPoolInterceptor(ConnectionInterceptor poolInterceptor) {
        this.poolInterceptor = poolInterceptor;
    }

    public ConnectionInterceptor getPoolInterceptor() {
        return poolInterceptor;
    }

    public void setConnectionEventListener(ConnectionEventListenerImpl listener) {
        this.listener = listener;
    }

    public void addConnectionHandle(ConnectionInfo connectionInfo) {
        listener.addConnectionInfo(connectionInfo);
    }

    public void removeConnectionHandle(ConnectionInfo connectionInfo) {
        listener.removeConnectionInfo(connectionInfo);
    }

    public boolean hasConnectionHandles() {
        return listener.hasConnectionInfos();
    }

    public void clearConnectionHandles() {
        listener.clearConnectionInfos();
    }

    public Collection<ConnectionInfo> getConnectionInfos() {
        return listener.getConnectionInfos();
    }

    public boolean securityMatches(ManagedConnectionInfo other) {
        return Objects.equals(subject, other.subject)
                && Objects.equals(connectionRequestInfo, other.connectionRequestInfo);
    }

    public boolean hasConnectionInfo(ConnectionInfo connectionInfo) {
        return listener.hasConnectionInfo(connectionInfo);
    }

    public boolean isFirstConnectionInfo(ConnectionInfo connectionInfo) {
        return listener.isFirstConnectionInfo(connectionInfo);
    }

    @Override
    public String toString() {
        return "ManagedConnectionInfo: " + super.toString() + ". mc: " + managedConnection + "]";
    }
    
}
