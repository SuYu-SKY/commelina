package com.instruction.matching.portal;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.instruction.matching.apis.ApiDef;
import com.instruction.matching.service.Matching;
import com.nexus.maven.core.akka.RouterActor;
import com.nexus.maven.core.message.ApiRequestWithActor;
import com.nexus.maven.core.message.MemberOfflineEvent;

/**
 * Created by @panyao on 2017/8/29.
 */
public class MatchingRouter extends RouterActor {

    private final ActorRef matching = getContext().actorOf(Matching.props(), "matching");

    @Override
    public void onRequest(ApiRequestWithActor request) {
        switch (Integer.valueOf(request.getApiMethod())) {
            case ApiDef.MATCHING_APIS.JOIN_MATCH_QUENE_VALUE:
                matching.tell(new Matching.JOIN_MATCH(request.getUserId()), this.getSelf());
                break;
            case ApiDef.MATCHING_APIS.REMOVE_MATCH_QUENE_VALUE:
                matching.tell(new Matching.REMOVE_MATCH(request.getUserId()), this.getSelf());
                break;
            // FIXME: 2017/8/29 处理 接口不存在的情况
        }
    }

    @Override
    public void onOffline(MemberOfflineEvent offlineEvent) {
        // 把用户从匹配队列里面移除
        matching.tell(new Matching.REMOVE_MATCH(offlineEvent.getUserId()), this.getSelf());
    }

    public static Props props() {
        return Props.create(MatchingRouter.class);
    }

}