package com.framework.niosocket;

import akka.actor.Props;

/**
 * Created by @panyao on 2017/8/25.
 */
public interface ActorRequest {

    Props getProps(ChannelOutputHandler outputHandler);

}