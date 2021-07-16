package com.netflix.discovery.shared.transport.jersey;

import com.sun.jersey.client.apache4.ApacheHttpClient4;

/**
 * 默认实现是EurekaJerseyClientImpl
 * @author David Liu
 */
public interface EurekaJerseyClient {

    ApacheHttpClient4 getClient();

    /**
     * Clean up resources.
     */
    void destroyResources();
}
