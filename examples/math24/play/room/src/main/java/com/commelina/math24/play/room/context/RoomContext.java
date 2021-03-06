package com.commelina.math24.play.room.context;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.commelina.akka.dispatching.nodes.AbstractServiceActor;
import com.commelina.akka.dispatching.proto.ActorBroadcast;
import com.commelina.akka.dispatching.proto.ApiRequest;
import com.commelina.akka.dispatching.proto.MemberOfflineEvent;
import com.commelina.akka.dispatching.proto.MemberOnlineEvent;
import com.commelina.math24.play.room.entity.PlayerEntity;
import com.commelina.math24.play.room.entity.PlayerStatus;
import com.commelina.math24.play.room.proto.NOTIFY_OPCODE;
import com.commelina.math24.play.room.proto.NotifyJoinRoom;
import com.commelina.math24.play.room.proto.Prepare;
import com.commelina.math24.play.room.proto.Prepared;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 房间
 *
 * @author @panyao
 * @date 2017/8/17
 */
public class RoomContext extends AbstractServiceActor {

    private final long roomId;
    private final BiMap<Long, PlayerEntity> players = HashBiMap.create(8);

    /**
     * 棋盘 actorRef
     */
    private ActorRef checkerboard;
    /**
     * 棋盘准备状态
     */
    private boolean checkerboardPrepared;

    public RoomContext(long roomId, List<PlayerEntity> playerEntities) {
        this.roomId = roomId;
        playerEntities.forEach(v -> players.put(v.getUserId(), v));
    }

    @Override
    public void preStart() throws Exception {
        // 设置检查游戏结束的任务
        // 10 分钟之后结束游戏
        getScheduler().scheduleOnce(Duration.create(11, TimeUnit.MINUTES), this::checkOver, getDispatcher());

        // 创建棋盘
        checkerboard = getContext().getSystem().actorOf(Checkerboard.props(100, 100));

        // 发送准备棋盘的通知
        checkerboard.tell(Prepare.getDefaultInstance(), getSelf());

        // 给客户端发送加入房间广播
        sendJoinRoomBroadcast();
    }

    private void sendJoinRoomBroadcast() {
        FiniteDuration finiteDuration = Duration.create(10, TimeUnit.SECONDS);

        // 向客户端广播加入房间的消息
        selectFrontend().tell(ActorBroadcast.newBuilder()
                .setOpcode(NOTIFY_OPCODE.JOIN_ROOM_VALUE)
                .addAllUserIds(players.keySet())
                .setMessage(NotifyJoinRoom.newBuilder()
                        .setRoomId(roomId)
                        .setOverMicrosecond(System.currentTimeMillis() + finiteDuration.toMillis())
                        .build().toByteString())
                .build(), getSelf());

//        ActorSystem system = getContext().getSystem();
//
//        // 十秒之后
//        players.keySet().forEach(u -> system.scheduler().scheduleOnce(finiteDuration, () -> {
//
//        }, system.dispatcher()));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MemberOfflineEvent.class, offlineEvent -> {
                    PlayerEntity playerEntity = players.get(offlineEvent.getLogoutUserId());
                    if (playerEntity != null) {
                        playerEntity.setPlayerStatus(PlayerStatus.Offline);
                    }
                })
                .match(MemberOnlineEvent.class, onlineEvent -> {
                    PlayerEntity playerEntity = players.get(onlineEvent.getLoginUserId());
                    if (playerEntity != null) {
                        playerEntity.setPlayerStatus(PlayerStatus.Online);
                    }
                })
                // 标记棋盘为准备完成状态
                .match(Prepared.class, p -> checkerboardPrepared = true)
                .build();
    }

    private boolean switchBehavior(ApiRequest r) {

        return true;
    }

    private void checkOver() {

    }

    public static Props props(long roomId, List<PlayerEntity> playerEntities) {
        return Props.create(RoomContext.class, roomId, playerEntities);
    }

}
