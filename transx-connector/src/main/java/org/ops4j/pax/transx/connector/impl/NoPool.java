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

import java.time.Duration;

/**
 *
 *
 * */
public class NoPool implements PoolingSupport {

    public static final PoolingSupport INSTANCE = new NoPool();

    public ConnectionInterceptor addPoolingInterceptors(ConnectionInterceptor tail) {
        return tail;
    }

    public int getPartitionCount() {
        return 0;
    }

    public int getIdleConnectionCount() {
        return 0;
    }

    public int getConnectionCount() {
        return 0;
    }

    public int getPartitionMaxSize() {
        return 0;
    }

    public void setPartitionMaxSize(int maxSize) {
    }

    public int getPartitionMinSize() {
        return 0;
    }

    public void setPartitionMinSize(int minSize) {
    }

    public Duration getBlockingTimeout() {
        return Duration.ZERO;
    }

    public void setBlockingTimeout(Duration blockingTimeout) {
    }

    public Duration getIdleTimeout() {
        return Duration.ZERO;
    }

    public void setIdleTimeout(Duration idleTimeout) {
    }

    @Override
    public Duration getValidatingPeriod() {
        return Duration.ZERO;
    }

    @Override
    public void setValidatingPeriod(Duration validatingPeriod) {

    }
}
