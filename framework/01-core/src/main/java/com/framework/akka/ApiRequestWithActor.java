package com.framework.akka;

import com.framework.core.AppVersion;
import com.framework.message.RequestArg;
import com.google.protobuf.Internal;

/**
 * Created by @panyao on 2017/8/25.
 */
public final class ApiRequestWithActor implements AppVersion {

    private final long userId;

    private final Internal.EnumLite apiOpcode;
    private final String version;
    private final RequestArg[] args;

    private final boolean isServer;

    private ApiRequestWithActor(long userId, Internal.EnumLite apiOpcode, String version, RequestArg[] args, boolean isServer) {
        this.userId = userId;
        this.apiOpcode = apiOpcode;
        this.version = version;
        this.args = args;
        this.isServer = isServer;
    }

    public static ApiRequestWithActor newClientApiRequestWithActor(long userId, Internal.EnumLite apiOpcode, String version, RequestArg[] args) {
        return new ApiRequestWithActor(userId, apiOpcode, version, args, false);
    }

    public static ApiRequestWithActor newServerApiRequestWithActor(long userId, Internal.EnumLite apiOpcode, String version, RequestArg[] args) {
        return new ApiRequestWithActor(userId, apiOpcode, version, args, true);
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public RequestArg[] getArgs() {
        return args;
    }

    public RequestArg getArg(int argName) {
        if (args == null || args.length == 0) {
            return null;
        }

        try {
            return args[argName];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * 要截取的数组数量 [1,2,3]
     * subArg(1) -> [2,3]
     * subArg(2) -> [3]
     * subArg(3) -> []
     * subArg(4) -> error
     *
     * @param subSize
     * @return
     */
    public RequestArg[] subArg(int subSize) {
        if (args == null || args.length < 2) {
            return null;
        }
        RequestArg[] args = new RequestArg[this.getArgs().length - subSize];
        for (int i = subSize; i < this.getArgs().length; i++) {
            args[i - subSize] = this.getArgs()[i];
        }
        return args;
    }

    public Internal.EnumLite getApiOpcode() {
        return apiOpcode;
    }

    public boolean isServer() {
        return isServer;
    }
}
