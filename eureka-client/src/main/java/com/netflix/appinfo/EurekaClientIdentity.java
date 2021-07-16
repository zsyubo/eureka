package com.netflix.appinfo;

/**
 * todo  不知道有撒用
 * 该类持有与eureka服务器上的eureka客户端认证有关的元数据信息。
 *
 * This class holds metadata information related to eureka client auth with the eureka server
 */
public class EurekaClientIdentity extends AbstractEurekaIdentity {
    public static final String DEFAULT_CLIENT_NAME = "DefaultClient";

    private final String clientVersion = "1.4";
    private final String id;
    private final String clientName;

    /**
     * id 默认是本机ip
     * @param id
     */
    public EurekaClientIdentity(String id) {
        this(id, DEFAULT_CLIENT_NAME);
    }
    
    public EurekaClientIdentity(String id, String clientName) {
        this.id = id;
        this.clientName = clientName;
    }

    @Override
    public String getName() {
        return clientName;
    }

    @Override
    public String getVersion() {
        return clientVersion;
    }

    @Override
    public String getId() {
        return id;
    }
}
