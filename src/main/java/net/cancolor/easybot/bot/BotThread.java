package net.cancolor.easybot.bot;

import net.cancolor.easybot.handler.message.receive.FriendMessageHandler;
import net.cancolor.easybot.handler.message.receive.NudgeMessageHandler;
import net.cancolor.easybot.handler.message.receive.GroupMessageHandler;
import net.cancolor.easybot.utils.SpringBeanFactoryUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NudgeEvent;

/**
 * @author Soar
 * @title: BotThread
 * @projectName easy-mirai
 * @description: TODO
 * @date 2021/12/12 12:19
 */
public class BotThread implements Runnable {
    private final InitMirai mirai;
    private final GroupMessageHandler groupMessageHandler;
    private final FriendMessageHandler friendMessageHandler;
    private final NudgeMessageHandler nudgeMessageHandler;

    public BotThread() {
        this.mirai = (InitMirai) SpringBeanFactoryUtil.getBean(InitMirai.class);
        this.groupMessageHandler = (GroupMessageHandler) SpringBeanFactoryUtil.getBean(GroupMessageHandler.class);
        this.friendMessageHandler = (FriendMessageHandler) SpringBeanFactoryUtil.getBean(FriendMessageHandler.class);
        this.nudgeMessageHandler = (NudgeMessageHandler) SpringBeanFactoryUtil.getBean(NudgeMessageHandler.class);
    }

    @Override
    public void run() {
        //初始化
        Bot bot = mirai.start();

        // 创建监听
        //监听群消息
        Listener listenerGroup = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            groupMessageHandler.receiveMessages(event);
        });
        //戳一戳
        Listener nudge = GlobalEventChannel.INSTANCE.subscribeAlways(NudgeEvent.class, event -> {
            nudgeMessageHandler.receiveMessages(event);
        });

        //监听私聊
        //不支持监听私聊发送文件
        Listener listenerFriend = GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
            friendMessageHandler.receiveMessages(event);
        });


    }
}
