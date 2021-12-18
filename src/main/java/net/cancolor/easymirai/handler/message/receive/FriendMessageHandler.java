package net.cancolor.easymirai.handler.message.receive;

import net.cancolor.easymirai.server.OnlineChannelContainer;
import net.cancolor.easymiraiapi.wrap.BotWrap;
import net.cancolor.easymiraiapi.wrap.FriendWrap;
import net.cancolor.easymiraiapi.wrap.MessageWrap;
import net.cancolor.easymiraiapi.constant.MessageConstant;
import net.cancolor.easymiraiapi.model.message.dto.SendClientMessageDTO;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
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
    static Integer isUseMiraiCode = 1;
    Logger logger = LoggerFactory.getLogger(FriendMessageHandler.class);

    //接受消息
    public void receiveMessages(FriendMessageEvent event) {
        //成员信息
        Friend member = event.getSender();
        SendClientMessageDTO sendClientMessageDTO = new SendClientMessageDTO();
        sendClientMessageDTO.setFriend(FriendWrap.wrap(member)).setBot(BotWrap.wrap(member));
        if (isUseMiraiCode==1) {
            MessageChain chain = event.getMessage();
            String miraiCode = chain.serializeToMiraiCode().trim().equals("") ? chain.toString() : chain.serializeToMiraiCode();
            sendClientMessageDTO.setMiraiCode(miraiCode);
            logger.info("监听私聊消息: {}", sendClientMessageDTO);
        } else {
            isUseMiraiCode=0;
            sendClientMessageDTO.setMessageList(MessageWrap.wrap(event.getMessage()));
            logger.info("监听私聊消息: {}", sendClientMessageDTO);
        }
        OnlineChannelContainer.sendAllChannel(MessageConstant.CHAT, isUseMiraiCode, sendClientMessageDTO);

    }


}
