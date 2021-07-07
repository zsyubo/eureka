package com.netflix.discovery;

import com.netflix.appinfo.ApplicationInfoManager;

/**
 * 一个可以在创建时与EurekaClient注册的处理程序，以执行预注册逻辑。预注册逻辑必须是同步的，以保证在注册前执行。
 *
 * A handler that can be registered with an {@link EurekaClient} at creation time to execute
 * pre registration logic. The pre registration logic need to be synchronous to be guaranteed
 * to execute before registration.
 */
public interface PreRegistrationHandler {
    void beforeRegistration();
}
