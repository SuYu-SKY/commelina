package com.commelina.akka;

import com.commelina.akka.dispatching.proto.ApiRequest;
import com.commelina.niosocket.RequestRouterHandler;
import com.commelina.niosocket.proto.SocketASK;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author @panyao
 * @date 2017/9/25
 */
public abstract class DefaultLocalActorRequestHandler implements RequestRouterHandler, Router {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final void onRequest(ChannelHandlerContext ctx, SocketASK ask) {
        final ApiRequest.Builder newRequestBuilder = ApiRequest.newBuilder();
        if (beforeHook(ask, newRequestBuilder, ctx)) {
            newRequestBuilder.setOpcode(ask.getOpcode())
                    .setVersion(ask.getVersion())
                    .addAllArgs(ask.getArgsList());
            afterHook(newRequestBuilder.build(), ctx);
        }
    }

    protected boolean beforeHook(SocketASK ask, ApiRequest.Builder newRequestBuilder, ChannelHandlerContext ctx) {
        return true;
    }

    protected void afterHook(ApiRequest request, ChannelHandlerContext ctx) {
//        Object result = AkkaLocalWorkerSystem.INSTANCE.askLocalRouterNode(request);
//        if (result instanceof MessageBody) {
//            ReplyUtils.reply(ctx, getRouterId(), request.getOpcode(), (MessageBody) result);
//        } else if (result instanceof LoginUserEntity) {
//            ContextAdapter.userLogin(ctx.channel().id(), ((LoginUserEntity) result).getUserId());
//            ReplyUtils.reply(ctx, getRouterId(), request.getOpcode(), ((LoginUserEntity) result).getMessageBody());
//        } else {
//            throw new InvalidParameterException("Undefined type: " + result.getClass().getName());
//        }
    }

    protected Logger getLogger() {
        return logger;
    }
}