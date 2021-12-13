package net.cancolor.easymirai.handler.message.send;




import net.cancolor.easymirai.utils.SendTencentMessageUtils;
import net.cancolor.easymiraiapi.model.message.dto.SendServerMessageDTO;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
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

    public static void sendGroupMessage(Bot bot, Group group, SendServerMessageDTO sendServerMessage) {
        MessageChain messageChain = SendTencentMessageUtils.wrapGroupMessage(group, sendServerMessage);
        sendMessage(group, messageChain);
        logger.info("bot:{},group:{},friend:{},发送消息:", bot, group, messageChain.serializeToMiraiCode());
    }


    public static void sendFriendMessage(Bot bot, Friend friend, SendServerMessageDTO sendServerMessage) {
        MessageChain messageChain = SendTencentMessageUtils.wrapFriendMessage(friend, sendServerMessage);
        sendMessage(friend, messageChain);
        logger.info("bot:{},friend:{},发送消息:{}", bot, friend, messageChain.serializeToMiraiCode());
    }


    public static void sendGroupMessage(Bot bot, Group group, String miraiCode) {
        MessageChain messageChain = MiraiCode.deserializeMiraiCode(miraiCode);
        sendMessage(group, messageChain);
        logger.info("bot:{},group:{},发送消息:{}", bot, group, messageChain.serializeToMiraiCode());
    }


    public static void sendFriendMessage(Bot bot, Friend friend, String miraiCode) {
        MessageChain messageChain = MiraiCode.deserializeMiraiCode(miraiCode);
        sendMessage(friend, messageChain);
        logger.info("bot:{},friend:{},发送消息:{}", bot, friend, messageChain.serializeToMiraiCode());
    }


    public static void sendMessage(Contact contact, MessageChain messageChain) {
        contact.sendMessage(messageChain);
    }


}
