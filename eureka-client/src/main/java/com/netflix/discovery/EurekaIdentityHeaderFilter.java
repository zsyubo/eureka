package com.netflix.discovery;

import com.netflix.appinfo.AbstractEurekaIdentity;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * 从代码中看 猜测就是在 http 头加入一些 通用信息
 * @author 远山(胡元富)
 * @date  2021/7/16
 **/
public class EurekaIdentityHeaderFilter extends ClientFilter {

    private final AbstractEurekaIdentity authInfo;

    public EurekaIdentityHeaderFilter(AbstractEurekaIdentity authInfo) {
        this.authInfo = authInfo;
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        if (authInfo != null) {
            cr.getHeaders().putSingle(AbstractEurekaIdentity.AUTH_NAME_HEADER_KEY, authInfo.getName());
            cr.getHeaders().putSingle(AbstractEurekaIdentity.AUTH_VERSION_HEADER_KEY, authInfo.getVersion());

            if (authInfo.getId() != null) {
                cr.getHeaders().putSingle(AbstractEurekaIdentity.AUTH_ID_HEADER_KEY, authInfo.getId());
            }
        }
        return getNext().handle(cr);
    }
}
