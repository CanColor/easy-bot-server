package net.cancolor.easymirai.handler.message.receive;

import net.cancolor.easymirai.server.ChannelContainer;
import net.cancolor.easymirai.server.OnlineChannelContainer;
import net.cancolor.easymirai.wrap.BotWrap;
import net.cancolor.easymirai.wrap.FriendWrap;
import net.cancolor.easymirai.wrap.MessageWrap;
import net.cancolor.easymiraiapi.model.message.client.receive.FriendMessage;
import net.cancolor.easymiraiapi.model.message.client.receive.MiraiMessage;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * @author Soar
 * @date 2021-12-10
 * @description:私聊消息实现
 */
@Service
public class FriendMessageHandler {
    final  static boolean isUseMiraiCode=true;
    Logger logger = LoggerFactory.getLogger(FriendMessageHandler.class);

    //接受消息
    public void receiveMessages(FriendMessageEvent event) {
        //成员信息
        Friend member = event.getSender();
        if(isUseMiraiCode){
            MiraiMessage miraiMessageDTO=new MiraiMessage();
            String miraiCode=event.getMessage().serializeToMiraiCode();
            miraiMessageDTO.setMiraiCode(miraiCode).setFriend(FriendWrap.wrap(member)).setBot(BotWrap.wrap(member));
            logger.info("监听私聊消息: {}",miraiMessageDTO);
            OnlineChannelContainer.sendAllChannel("friend",1,miraiMessageDTO);
        }else{
            FriendMessage friendMessageDTO = new FriendMessage();
            friendMessageDTO.setMessage(MessageWrap.wrap(event)).setBot(BotWrap.wrap(member)).setFriend(FriendWrap.wrap(member));
            logger.info("监听私聊消息: {}",friendMessageDTO);
            OnlineChannelContainer.sendAllChannel("friend",0,friendMessageDTO);
        }

    }




}
