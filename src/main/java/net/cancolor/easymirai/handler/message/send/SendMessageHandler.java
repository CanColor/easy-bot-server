package net.cancolor.easymirai.handler.message.send;




import io.netty.channel.Channel;
import net.cancolor.easymirai.utils.SendClientMessageUtil;
import net.cancolor.easymirai.utils.SendTencentMessageUtils;
import net.cancolor.easymiraiapi.model.message.dto.SendServerMessageDTO;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author Soar
 * @title: SendMessageHandler
 * @projectName easy-mirai
 * @description: 发送信息到腾讯
 * @date 2021/12/11 23:06
 */
public class SendMessageHandler implements Serializable {


    static Logger logger = LoggerFactory.getLogger(SendMessageHandler.class);

    public static void sendGroupMessage(Channel channel, Bot bot, Group group, NormalMember normalMember, SendServerMessageDTO sendServerMessage) {
        MessageChain messageChain = null;
        try {
            messageChain = SendTencentMessageUtils.wrapGroupMessage(group, sendServerMessage);
            logger.info("客户端 发送消息:channel:{},bot:{},group:{},normalMember:{}", channel, bot, group, normalMember, messageChain.serializeToMiraiCode());
        } catch (Exception e) {
            SendClientMessageUtil.sendClient(channel, e.getLocalizedMessage());
            return;
        }
        if (sendServerMessage.getSendServerMessageList().size() > 0 && sendServerMessage.getSendServerMessageList().get(0).getContactsMessage() == null) {
            sendMessage(channel, group, messageChain);
            logger.info("客户端 发送消息:channel:{},bot:{},group:{},normalMember:{},miraiCode:{}", channel, bot, group, normalMember, messageChain.toString());
        }
    }


    public static void sendFriendMessage(Channel channel, Bot bot, Friend friend, SendServerMessageDTO sendServerMessage) {
        MessageChain messageChain = null;
        try {
            messageChain = SendTencentMessageUtils.wrapFriendMessage(friend, sendServerMessage);
        } catch (Exception e) {
            SendClientMessageUtil.sendClient(channel, e.getLocalizedMessage());
            return;
        }
        if (sendServerMessage.getSendServerMessageList().size() > 0 && sendServerMessage.getSendServerMessageList().get(0).getContactsMessage() == null) {
            sendMessage(channel, friend, messageChain);
            logger.info("客户端 发送消息: channel:{},bot:{},friend:{},miraiCode:{}", channel, bot, friend, messageChain.toString());
        }
    }


    public static void sendGroupMessage(Channel channel, Bot bot, Group group, NormalMember normalMember, String miraiCode) {
        MessageChain messageChain = MiraiCode.deserializeMiraiCode(miraiCode);
        sendMessage(channel, group, messageChain);
        logger.info("客户端 发送消息:channel:{},bot:{},group:{},normalMember:{},miraiCode:{}", channel, bot, group, normalMember, messageChain.toString());
    }


    public static void sendFriendMessage(Channel channel, Bot bot, Friend friend, String miraiCode) {
        MessageChain messageChain = MiraiCode.deserializeMiraiCode(miraiCode);
        sendMessage(channel, friend, messageChain);
        logger.info("客户端 发送消息: channel:{},bot:{},friend:{},miraiCode:{}", channel, bot, friend, messageChain.toString());
    }


    public static void sendMessage(Channel channel, Contact contact, MessageChain messageChain) {
        if (messageChain != null && messageChain.size() > 0) {
            contact.sendMessage(messageChain);
        } else {
            SendClientMessageUtil.sendClient(channel, "请勿传递不支持的消息！");
        }
    }


}
