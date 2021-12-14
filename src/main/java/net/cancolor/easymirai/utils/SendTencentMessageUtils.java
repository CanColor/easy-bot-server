package net.cancolor.easymirai.utils;


import net.cancolor.easymiraiapi.constent.AtConstant;
import net.cancolor.easymiraiapi.constent.ContactsConstant;
import net.cancolor.easymiraiapi.model.message.AtMessage;
import net.cancolor.easymiraiapi.model.message.ContactsMessage;
import net.cancolor.easymiraiapi.model.message.FaceMessage;
import net.cancolor.easymiraiapi.model.message.VipFaceMessage;
import net.cancolor.easymiraiapi.model.message.client.send.SendServerMessage;
import net.cancolor.easymiraiapi.model.message.dto.SendServerFileMessageDTO;
import net.cancolor.easymiraiapi.model.message.dto.SendServerImageMessageDTO;
import net.cancolor.easymiraiapi.model.message.dto.SendServerMessageDTO;
import net.cancolor.easymiraiapi.okhttp3.OkHttpUtils;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.message.action.Nudge;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * @author Soar
 * @title: sendMessageUtils
 * @projectName easy-mirai
 * @description: 发送消息工具封装类
 * @date 2021/12/11 16:52
 */
public class SendTencentMessageUtils {

    //封装发送信息
    public static MessageChain wrapGroupMessage(Group group, SendServerMessageDTO sendServerMessageDTO) throws Exception {
        List<SendServerMessage> sendServerMessageList = sendServerMessageDTO.getSendServerMessageList();
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        for (SendServerMessage message : sendServerMessageList) {
            AtMessage atMessage = message.getAtMessage();
            ContactsMessage contactsMessage = message.getContactsMessage();
            SendServerFileMessageDTO sendFileMessage = message.getSendFileMessage();
            //at
            if (atMessage != null) {
                if (atMessage.getType().equalsIgnoreCase(AtConstant.AT)) {
                    messageChainBuilder.append(new At(sendServerMessageDTO.getFriendId()));
                }
                if (atMessage.getType().equalsIgnoreCase(AtConstant.AT_ALL)) {
                    messageChainBuilder.append(AtAll.INSTANCE);
                }
            }
            if (contactsMessage != null) {
                //戳了戳
                if (ContactsConstant.NUDGE.equals(contactsMessage.getAction())) {
                    //群戳了戳
                    if (group != null) {
                        Nudge nudge = group.get(sendServerMessageDTO.getFriendId()).nudge();
                        nudge.sendTo(group);
                    }
                } else {
                    MemberPermission botPermission = group.getBotAsMember().getPermission();
                    int botPermissionLevel = botPermission.getLevel();
                    MemberPermission friendPermission = group.get(sendServerMessageDTO.getFriendId()).getPermission();
                    int friendPermissionLevel = friendPermission.getLevel();
                    if (botPermissionLevel <= friendPermissionLevel) {
                        throw new Exception("bot权限比friend: " + sendServerMessageDTO.getFriendId() + "低");
                    }
                    //解禁言
                    if (ContactsConstant.MEMBER_UNMUTE.equals(contactsMessage.getAction())) {
                        NormalMember normalMember = group.get(group.get(sendServerMessageDTO.getFriendId()).getId());
                        normalMember.unmute();
                    }
                    //禁言
                    else if (ContactsConstant.MEMBER_MUTE.equals(contactsMessage.getAction())) {
                        NormalMember normalMember = group.get(sendServerMessageDTO.getFriendId());
                        normalMember.mute(contactsMessage.getMinute() * 60);
                    }
                    //T人
                    else if (ContactsConstant.MEMBER_KICK.equals(contactsMessage.getAction())) {
                        NormalMember normalMember = group.get(group.get(sendServerMessageDTO.getFriendId()).getId());
                        normalMember.kick(contactsMessage.getKillMessage(), contactsMessage.isBlock());
                    }
                }

                //上传群文件
                if (sendFileMessage != null) {
                    ExternalResource res = ExternalResource.create(new File(sendFileMessage.getPath())).toAutoCloseable();
                    group.getFiles().uploadNewFile(sendFileMessage.getFileName(), res); // 2.8+
                }
            }

            wrapMessage(group, messageChainBuilder, message);
        }
        return messageChainBuilder.build();
    }

    public static MessageChain wrapFriendMessage(Friend friend, SendServerMessageDTO sendServerMessage) {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        List<SendServerMessage> sendServerMessageList = sendServerMessage.getSendServerMessageList();
        for (SendServerMessage message : sendServerMessageList) {
            ContactsMessage contactsMessage = message.getContactsMessage();
            VipFaceMessage vipFaceMessage = message.getVipFaceMessage();
            //vip表情-戳一戳升级版
            if (vipFaceMessage != null) {
                VipFace vipFace = new VipFace(vipFaceMessage.getKind(), 1);
                messageChainBuilder.append(vipFace);
            }
            if (contactsMessage != null) {
                //戳了戳
                if (ContactsConstant.NUDGE.equals(contactsMessage.getAction())) {
                    //群戳了戳
                    Nudge nudge = friend.nudge();
                    nudge.sendTo(friend);
                }
            }
            wrapMessage(friend, messageChainBuilder, message);
        }
        return messageChainBuilder.build();
    }


    public static MessageChain wrapMessage(Contact contact, MessageChainBuilder messageChainBuilder, SendServerMessage message) {
        String content = message.getMessage();
        net.cancolor.easymiraiapi.model.message.PokeMessage pokeMessage = message.getPokeMessage();
        SendServerImageMessageDTO imageMessage = message.getImageMessage();
        List<FaceMessage> faceMessageList = message.getFaceMessageList();
        net.cancolor.easymiraiapi.model.message.SimpleServiceMessage simpleServiceMessage = message.getSimpleServiceMessage();
        //白文
        if (content != null) {
            messageChainBuilder.append(content);
        }
        //戳一戳
        if (pokeMessage != null) {
            messageChainBuilder.append(new PokeMessage(pokeMessage.getName(), pokeMessage.getPokeType(), pokeMessage.getId()));
        }
        //图片
        if (imageMessage != null) {
            Image image = null;
            if (imageMessage.getImageId() != null) {
                messageChainBuilder.append(Image.fromId(imageMessage.getImageId()));
            } else if (imageMessage.getPath() != null) {
                image = uploadImage(contact, "path", imageMessage.getPath());
            } else {
                image = uploadImage(contact, "url", imageMessage.getOriginUrl());
            }
            messageChainBuilder.append(image);
        }
        //表情
        if (faceMessageList != null) {
            for (FaceMessage faceMessage : faceMessageList) {
                Face face = new Face(faceMessage.getId());
                messageChainBuilder.append(face);
            }
        }
        //外链
        if (simpleServiceMessage != null) {
            messageChainBuilder.append(new net.mamoe.mirai.message.data.SimpleServiceMessage(simpleServiceMessage.getServiceId(), simpleServiceMessage.getContent()));
        }
        return messageChainBuilder.build();
    }

    public static Image uploadImage(Contact contact, String uploadType, String imageAdress) {
        Image image = null;
        ExternalResource res = null;
        try {
            if ("path".equals(uploadType)) {
                res = ExternalResource.create(new File(imageAdress));
            } else {
                InputStream inputStream = OkHttpUtils.builder().url(imageAdress)
                        .get()
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .get()
                        .inputStream();
                try {
                    res = ExternalResource.create(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            image = contact.uploadImage(res);
        } finally {
            try {
                res.close(); // 记得关闭资源
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }
}
