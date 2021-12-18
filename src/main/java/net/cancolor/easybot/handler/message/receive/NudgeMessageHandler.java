package net.cancolor.easybot.handler.message.receive;

import net.cancolor.easybot.server.OnlineChannelContainer;
import net.cancolor.easybotapi.wrap.BotWrap;
import net.cancolor.easybotapi.wrap.FriendWrap;
import net.cancolor.easybotapi.wrap.GroupWrap;
import net.cancolor.easybotapi.constant.MessageConstant;
import net.cancolor.easybotapi.model.message.Message;
import net.cancolor.easybotapi.model.message.NudgeMessage;
import net.cancolor.easybotapi.model.message.dto.SendClientMessageDTO;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.UserOrBot;
import net.mamoe.mirai.event.events.NudgeEvent;
import net.mamoe.mirai.internal.contact.GroupImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * @author Soar
 * @date 2021-12-10
 * @description:戳一戳
 */
@Service
public class NudgeMessageHandler {

    Logger logger = LoggerFactory.getLogger(NudgeMessageHandler.class);

    //接受消息
    public void receiveMessages(NudgeEvent event) {
        SendClientMessageDTO sendClientMessageDTO=new SendClientMessageDTO();
        NudgeMessage nudgeMessage = new NudgeMessage();
        UserOrBot from = event.getFrom();
        if (event.getSubject() instanceof GroupImpl) {
            Group group = (GroupImpl) event.getSubject();
            sendClientMessageDTO.setGroup(GroupWrap.wrap(group));
        }
        sendClientMessageDTO.setBot(BotWrap.wrap(event.getBot())).setFriend(FriendWrap.wrap(from));
        UserOrBot target = event.getTarget();
        nudgeMessage.setSuffix(event.getSuffix()).setAction(event.getAction()).setTarget(FriendWrap.wrap(target));
        Message message=new Message();
        message.setNudgeMessage(nudgeMessage);
        List<Message> messageList=new ArrayList<>();
        messageList.add(message);
        sendClientMessageDTO.setMessageList(messageList);
        logger.info("监听戳一戳: {}", nudgeMessage);
        OnlineChannelContainer.sendAllChannel(MessageConstant.CHAT, 0, sendClientMessageDTO);

    }


}
