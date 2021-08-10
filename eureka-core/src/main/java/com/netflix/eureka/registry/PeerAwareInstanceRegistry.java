/*
 * Copyright 2015 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.netflix.eureka.registry;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.resources.ASGResource;

import java.util.List;

/**
 * @author Tomasz Bak
 */
public interface PeerAwareInstanceRegistry extends InstanceRegistry {

    void init(PeerEurekaNodes peerEurekaNodes) throws Exception;

    /**
     * 从一个对等的eureka节点填充注册表信息。如果通信失败，这个操作会失败到其他节点，直到列表用完。
     *
     * Populates the registry information from a peer eureka node. This
     * operation fails over to other nodes until the list is exhausted if the
     * communication fails.
     */
    int syncUp();

    /**
     * 检查注册表访问是否被允许，或者服务器是否处于无法获取所有注册表信息的情况。
     * 如果服务器在启动时无法从对等的eureka节点获取注册表信息，则它不会在com.Netflix.eureka.eurekaserverconfig.getwaittimeinwshensyncempty()中指定的时间段内返回注册表信息。
     *
     * Checks to see if the registry access is allowed or the server is in a
     * situation where it does not all getting registry information. The server
     * does not return registry information for a period specified in
     * {@link com.netflix.eureka.EurekaServerConfig#getWaitTimeInMsWhenSyncEmpty()}, if it cannot
     * get the registry information from the peer eureka nodes at start up.
     *
     * @return false - if the instances count from a replica transfer returned
     *         zero and if the wait time has not elapsed, otherwise returns true
     */
     boolean shouldAllowAccess(boolean remoteRegionRequired);

     void register(InstanceInfo info, boolean isReplication);

     void statusUpdate(final String asgName, final ASGResource.ASGStatus newStatus, final boolean isReplication);
}
