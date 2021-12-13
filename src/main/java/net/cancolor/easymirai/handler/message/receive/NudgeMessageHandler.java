package net.cancolor.easymirai.handler.message.receive;

import net.cancolor.easymirai.server.ChannelContainer;
import net.cancolor.easymirai.server.OnlineChannelContainer;
import net.cancolor.easymirai.wrap.BotWrap;
import net.cancolor.easymirai.wrap.FriendWrap;
import net.cancolor.easymirai.wrap.GroupWrap;
import net.cancolor.easymiraiapi.model.message.client.receive.NudgeMessage;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.UserOrBot;
import net.mamoe.mirai.event.events.NudgeEvent;
import net.mamoe.mirai.internal.contact.GroupImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        NudgeMessage nudgeMessage = new NudgeMessage();
        UserOrBot from = event.getFrom();
        if (event.getSubject() instanceof GroupImpl) {
            Group group = (GroupImpl) event.getSubject();
            nudgeMessage.setGroup(GroupWrap.wrap(group));
        }
        UserOrBot target = event.getTarget();
        nudgeMessage.setTarget(FriendWrap.wrap(target)).setSuffix(event.getSuffix()).setAction(event.getAction()).setBot(BotWrap.wrap(event.getBot())).setFriend(FriendWrap.wrap(from));
        logger.info("监听戳一戳: {}", nudgeMessage);
        OnlineChannelContainer.sendAllChannel("nudge", 0, nudgeMessage);

    }


}
