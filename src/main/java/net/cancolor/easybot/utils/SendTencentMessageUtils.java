package net.cancolor.easybot.utils;


import net.cancolor.easybotapi.constant.AtConstant;
import net.cancolor.easybotapi.constant.ContactsConstant;
import net.cancolor.easybotapi.constant.OrginTypeConsant;
import net.cancolor.easybotapi.model.message.FileMessage;
import net.cancolor.easybotapi.model.message.Message;
import net.cancolor.easybotapi.model.message.*;
import net.cancolor.easybotapi.model.message.dto.AudioMessageDTO;
import net.cancolor.easybotapi.model.message.dto.SendServerMessageDTO;
import net.cancolor.easybotapi.okhttp3.OkHttpUtils;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.message.action.Nudge;
import net.mamoe.mirai.message.data.PokeMessage;
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
        List<Message> messageList = sendServerMessageDTO.getMessageList();

        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        for (Message message : messageList) {
            AtMessage atMessage = message.getAtMessage();
            ContactsMessage contactsMessage = message.getContactsMessage();
            FileMessage sendFileMessage = message.getFileMessage();
            AudioMessageDTO audioMessage = message.getAudioMessage();

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
            if (audioMessage != null) {
                Audio audio = uploadAudio(group, null, audioMessage.getUploadType(), audioMessage.getResource());
                messageChainBuilder.append(audio);
            }
            wrapMessage(group, messageChainBuilder, message);
        }
        return messageChainBuilder.build();
    }

    public static MessageChain wrapFriendMessage(Friend friend, SendServerMessageDTO sendServerMessageDTO) throws Exception {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        List<Message> sendServerMessageList = sendServerMessageDTO.getMessageList();
        for (Message message : sendServerMessageList) {
            ContactsMessage contactsMessage = message.getContactsMessage();
            AudioMessageDTO audioMessage = message.getAudioMessage();
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
            if (audioMessage != null) {
                uploadAudio(null, friend, audioMessage.getUploadType(), audioMessage.getResource());
            }
            wrapMessage(friend, messageChainBuilder, message);
        }
        return messageChainBuilder.build();
    }


    public static MessageChain wrapMessage(Contact contact, MessageChainBuilder messageChainBuilder, Message message) throws Exception {
        String content = message.getMessage();
        net.cancolor.easybotapi.model.message.PokeMessage pokeMessage = message.getPokeMessage();
        ImageMessage imageMessage = message.getImageMessage();
        ImageMessage flashImageMessage = message.getFlashImageMessage();
        List<FaceMessage> faceMessageList = message.getFaceMessageList();
        MusicShareMessage musicShare = message.getMusicShare();
        UrlMessage urlMessage = message.getUrlMessage();
        net.cancolor.easybotapi.model.message.SimpleServiceMessage simpleServiceMessage = message.getSimpleServiceMessage();
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
                image = Image.fromId(imageMessage.getImageId());
            } else if (imageMessage.getFilePath() != null) {
                image = uploadImage(contact, "path", imageMessage.getFilePath());
            } else {
                image = uploadImage(contact, "url", imageMessage.getOriginUrl());
            }
            messageChainBuilder.append(image);
        }
        if (flashImageMessage != null) {
            Image image = null;
            if (flashImageMessage.getImageId() != null) {
                image = Image.fromId(flashImageMessage.getImageId());
            } else if (flashImageMessage.getFilePath() != null) {
                image = uploadImage(contact, "path", flashImageMessage.getFilePath());
            } else {
                image = uploadImage(contact, "url", flashImageMessage.getOriginUrl());
            }
            messageChainBuilder.append(new FlashImage(image));
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
        if (urlMessage != null) {
            messageChainBuilder.append(RichMessage.share(urlMessage.getUrl(), urlMessage.getTitle(), urlMessage.getContent(), urlMessage.getCoverUrl()));
        }
        if (musicShare != null) {
            messageChainBuilder.append(new MusicShare(musicShare.getPlayer(), musicShare.getTitile(), musicShare.getSummary(), musicShare.getJumpUrl(), musicShare.getPictureUrl(), musicShare.getMusicUrl()));
        }

        return messageChainBuilder.build();
    }

    public static Image uploadImage(Contact contact, String uploadType, String imageAdress) throws Exception {
        Image image = null;
        ExternalResource res = null;
        try {
            if ("path".equals(uploadType)) {
                res = ExternalResource.create(new File(imageAdress));
            } else {
                InputStream inputStream = OkHttpUtils.builder().url(imageAdress)
                        .get()
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .inputStream();
                try {
                    res = ExternalResource.create(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            image = contact.uploadImage(res);
        } catch (Exception e) {
            throw new Exception("图片发送失败,请检查图片地址是否有效！");
        } finally {
            try {
                res.close(); // 记得关闭资源
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    public static Audio uploadAudio(Group group, Friend friend, String uploadType, String audioAdress) throws Exception {
        Audio audio = null;
        ExternalResource res = null;
        try {
            if (OrginTypeConsant.FILE_PATH.equals(uploadType)) {
                res = ExternalResource.create(new File(audioAdress));
            } else {
                InputStream inputStream = OkHttpUtils.builder().url(audioAdress)
                        .get()
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .inputStream();
                try {
                    res = ExternalResource.create(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (group != null) {
                group.uploadAudio(res);
            } else {
                friend.uploadAudio(res);
            }
        } catch (Exception e) {
            throw new Exception("图片发送失败,请检查图片地址是否有效！");
        } finally {
            try {
                res.close(); // 记得关闭资源
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return audio;
    }
}
