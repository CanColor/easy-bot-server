package net.cancolor.easymirai.handler.message.receive;

import net.cancolor.easymirai.server.ChannelContainer;
import net.cancolor.easymirai.server.OnlineChannelContainer;
import net.cancolor.easymirai.wrap.BotWrap;
import net.cancolor.easymirai.wrap.FriendWrap;
import net.cancolor.easymirai.wrap.GroupWrap;
import net.cancolor.easymirai.wrap.MessageWrap;
import io.netty.channel.Channel;
import net.cancolor.easymiraiapi.model.message.client.receive.GroupMessage;
import net.cancolor.easymiraiapi.model.message.client.receive.MiraiMessage;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

/*
 * @author Soar
 * @date 2021-12-10
 * @description:群组消息实现
 */
@Service
public class GroupMessageHandler {
    final static boolean isUseMiraiCode = true;

    Logger logger = LoggerFactory.getLogger(GroupMessageHandler.class);
    private Map<Integer, Channel> onlineChannelMap = OnlineChannelContainer.newInstance().getChannelMap();

    //接受消息
    public void receiveMessages(GroupMessageEvent event) {
        //成员信息
        Member member = event.getSender();
        if (isUseMiraiCode) {
            MiraiMessage miraiMessage = new MiraiMessage();
            miraiMessage.setFriend(FriendWrap.wrap(member)).setBot(BotWrap.wrap(member));
            miraiMessage.setMiraiCode(event.getMessage().serializeToMiraiCode()).setLevel(event.getPermission().getLevel()).setGroup(GroupWrap.wrap(member));
            logger.info("监听群消息: {}", miraiMessage);
            Iterator<Map.Entry<Integer, Channel>> it = onlineChannelMap.entrySet().iterator();
            OnlineChannelContainer.sendAllChannel("group",1,miraiMessage);
        } else {
            //2自己 0是匿名
            GroupMessage groupMessage= new GroupMessage();
            groupMessage.setFriend(FriendWrap.wrap(member)).setBot(BotWrap.wrap(member));
            groupMessage.setGroup(GroupWrap.wrap(member));
            groupMessage.setMessage(MessageWrap.wrap(event));
            logger.info("监听群消息: {}", groupMessage);
            OnlineChannelContainer.sendAllChannel("group",0,groupMessage);
        }
    }


}
