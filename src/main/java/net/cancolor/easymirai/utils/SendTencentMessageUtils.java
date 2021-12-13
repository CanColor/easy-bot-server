package net.cancolor.easymirai.utils;



import net.cancolor.easymiraiapi.constent.AtConstant;
import net.cancolor.easymiraiapi.model.message.AtMessage;
import net.cancolor.easymiraiapi.model.message.FaceMessage;
import net.cancolor.easymiraiapi.model.message.VipFaceMessage;
import net.cancolor.easymiraiapi.model.message.client.send.SendServerMessage;
import net.cancolor.easymiraiapi.model.message.dto.SendServerFileMessageDTO;
import net.cancolor.easymiraiapi.model.message.dto.SendServerImageMessageDTO;
import net.cancolor.easymiraiapi.model.message.dto.SendServerMessageDTO;
import net.cancolor.easymiraiapi.okhttp3.OkHttpUtils;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
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
    public static MessageChain wrapGroupMessage(Group group, SendServerMessageDTO sendServerMessage) {
       List<SendServerMessage> sendServerMessageList=  sendServerMessage.getSendServerMessageList();
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        for (SendServerMessage message : sendServerMessageList) {
            AtMessage atMessage = message.getAtMessage();
            SendServerFileMessageDTO sendFileMessage = message.getSendFileMessage();
            //at
            if (atMessage != null) {
                if (atMessage.getType().equalsIgnoreCase(AtConstant.AT)) {
                    messageChainBuilder.append(new At(sendServerMessage.getFriendId()));
                }
                if (atMessage.getType().equalsIgnoreCase(AtConstant.AT_ALL)) {
                    messageChainBuilder.append(AtAll.INSTANCE);
                }
            }
            //上传群文件
            if (sendFileMessage != null) {
                ExternalResource res = ExternalResource.create(new File(sendFileMessage.getPath())).toAutoCloseable();
                group.getFiles().uploadNewFile(sendFileMessage.getFileName(), res); // 2.8+
            }
            wrapMessage(group,sendServerMessageList );
        }
        return messageChainBuilder.build();
    }

    public static MessageChain wrapFriendMessage(Friend friend,SendServerMessageDTO sendServerMessage) {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        List<SendServerMessage> sendServerMessageList=sendServerMessage.getSendServerMessageList();
        for (SendServerMessage message : sendServerMessageList) {
            VipFaceMessage vipFaceMessage = message.getVipFaceMessage();
            //vip表情-戳一戳升级版
            if (vipFaceMessage != null) {
                net.mamoe.mirai.message.data.VipFace.Kind kind = new VipFace.Kind(vipFaceMessage.getId(), vipFaceMessage.getName());
                VipFace vipFace = new VipFace(kind, vipFaceMessage.getCount());
                messageChainBuilder.append(vipFace);
            }

            wrapMessage(friend, sendServerMessageList);
        }
        return messageChainBuilder.build();
    }


    public static MessageChain wrapMessage(Contact contact, List<SendServerMessage> messageList) {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        for (SendServerMessage message : messageList) {
            String content = message.getMessage();
            net.cancolor.easymiraiapi.model.message.PokeMessage pokeMessage = message.getPokeMessage();
            List<SendServerImageMessageDTO> sendImageMessageList = message.getSendImageMessageList();
            List<FaceMessage> faceMessageList = message.getFaceMessageList();
            net.cancolor.easymiraiapi.model.message.SimpleServiceMessage simpleServiceMessage = message.getSimpleServiceMessage();
            //白文
            if (content != null) {
                messageChainBuilder.append(content);
            }
            //戳一戳
            if (pokeMessage != null) {
                messageChainBuilder.append(new PokeMessage(pokeMessage.getName(), pokeMessage.getType(), pokeMessage.getId()));
            }
            //图片
            if (sendImageMessageList != null) {
                Image image = null;
                for (SendServerImageMessageDTO sendImageMessage : sendImageMessageList) {
                    if (sendImageMessage.getImageId() != null) {
                        messageChainBuilder.append(Image.fromId(sendImageMessage.getImageId()));
                    } else if (sendImageMessage.getPath() != null) {
                        image = uploadImage(contact, "path", sendImageMessage.getPath());
                    } else {
                        image = uploadImage(contact, "url", sendImageMessage.getOriginUrl());
                    }
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
