package com.game.foundation.gateway.api;

import com.nexus.maven.netty.socket.router.RpcApi;
import com.nexus.maven.netty.socket.router.RpcMethod;

import javax.annotation.PostConstruct;

/**
 * Created by @panyao on 2017/8/9.
 */
@RpcApi
public class AkkaRouter {

    @PostConstruct
    public void init() {

    }

    @RpcMethod(value = "routing")
    public void routing() {

    }

}
