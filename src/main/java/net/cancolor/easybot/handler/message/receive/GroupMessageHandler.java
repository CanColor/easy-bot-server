package net.cancolor.easybot.handler.message.receive;

import io.netty.channel.Channel;
import net.cancolor.easybot.server.OnlineChannelContainer;
import net.cancolor.easybotapi.wrap.BotWrap;
import net.cancolor.easybotapi.wrap.FriendWrap;
import net.cancolor.easybotapi.wrap.GroupWrap;
import net.cancolor.easybotapi.wrap.MessageWrap;
import net.cancolor.easybotapi.constant.MessageConstant;
import net.cancolor.easybotapi.model.message.dto.SendClientMessageDTO;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
 * @author Soar
 * @date 2021-12-10
 * @description:群组消息实现
 */
@Service
public class GroupMessageHandler {
     static Integer isUseMiraiCode = 1;

    Logger logger = LoggerFactory.getLogger(GroupMessageHandler.class);
    private final Map<Integer, Channel> onlineChannelMap = OnlineChannelContainer.newInstance().getChannelMap();

    //接受消息
    public void receiveMessages(GroupMessageEvent event) {
        SendClientMessageDTO sendClientMessageDTO=new SendClientMessageDTO();
        //成员信息
        Member member = event.getSender();
        sendClientMessageDTO.setFriend(FriendWrap.wrap(member)).setBot(BotWrap.wrap(member));
        sendClientMessageDTO.setGroup(GroupWrap.wrap(member)).setLevel(event.getPermission().getLevel());
        if (isUseMiraiCode==1) {
            MessageChain chain= event.getMessage();
            String miraiCode=chain.serializeToMiraiCode().trim().equals("")?chain.toString():chain.serializeToMiraiCode();
            sendClientMessageDTO.setMiraiCode(miraiCode);
            logger.info("监听群消息: {}", sendClientMessageDTO);
        } else {
            isUseMiraiCode=0;
            //2自己 0是匿名
            sendClientMessageDTO.setMessageList(MessageWrap.wrap(event.getMessage()));
            logger.info("监听群消息: {}", sendClientMessageDTO);
        }
        OnlineChannelContainer.sendAllChannel(MessageConstant.CHAT,isUseMiraiCode,sendClientMessageDTO);
    }


}
