package com.framework.akka_cluste_router;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.protobuf.Internal;

/**
 * Created by @panyao on 2017/9/25.
 */
public class RouterLocalFrontendActor extends AbstractActor {

    private BiMap<Internal.EnumLite, ActorRef> localRouters = HashBiMap.create(16);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LocalRouterJoinEntity.class, j -> {
                    ActorRef target = localRouters.get(j.getRouterId());
                    if (target == null) {
                        sender().tell(new RouterNotFoundEntity(j.getRouterId()), getSelf());
                    } else {
                        target.forward(j.getApiRequest(), getContext());
                    }
                })
                .match(LocalRouterRegistrationEntity.class, r -> {
                    getContext().watch(sender());
                    localRouters.put(r.getRouterId(), sender());
                })
                .match(Terminated.class, terminated -> {
                    localRouters.inverse().remove(terminated.getActor());
                })
                .build();
    }

}
