package net.cancolor.easymirai.wrap;


import net.cancolor.easymiraiapi.model.message.*;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import net.mamoe.mirai.internal.message.FileMessageImpl;
import net.mamoe.mirai.internal.message.OnlineFriendImage;
import net.mamoe.mirai.internal.message.OnlineGroupImageImpl;


import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Soar
 * @title: MessageWrap
 * @projectName easy-mirai
 * @description: 消息包装类
 * @date 2021/12/11 0:51
 */
public class MessageWrap {
    static Logger logger = LoggerFactory.getLogger(MessageWrap.class);

    public static Message wrap(GroupMessageEvent event) {
        Message message = new Message();
        MessageChain chain = event.getMessage();
        setMessageFiled(chain, message, event.getTime());
        return message;
    }

    public static Message wrap(FriendMessageEvent event) {
        Message message = new Message();
        MessageChain chain = event.getMessage();
        setMessageFiled(chain, message, event.getTime());
        return message;
    }


    public static Message setMessageFiled(MessageChain chain, Message message, int time) {
        setMessageType(chain, message);
        message.setTime(time);
        return message;
    }


    public static void setMessageType(MessageChain chain, Message message) {
        String content = null;
        for (Object obj : chain) {
            logger.info(obj.getClass().toString());
            if (obj instanceof PlainText) {
                content = ((PlainText) obj).getContent();
            } else {
                checkMessageType(obj, message);
            }
        }

        //消息 QQ空间相册
        message.setContent(content);
    }

    public static void checkMessageType(Object obj, Message message) {
        //At
        if (obj instanceof At) {
            At at = (At) obj;
            Class clz = at.getClass();
            AtMessage atMessage = new AtMessage();
            atMessage.setType(clz.getSimpleName());
            message.setAtMessage(atMessage);
        }
        //at全体
        else if (obj instanceof AtAll) {
            AtAll atAll = (AtAll) obj;
            AtMessage atMessage = new AtMessage();
            atMessage.setType(atAll.getClass().getSimpleName());
            message.setAtMessage(atMessage);
        }
        //图片 gif
        else if (obj instanceof OnlineGroupImageImpl) {
            List<ImageMessage> imageList = message.getImageMessageList();
            if (imageList == null) {
                imageList = new ArrayList<>();
            }
            OnlineGroupImageImpl onlineGroupImage = (OnlineGroupImageImpl) obj;
            ImageMessage imageMessage = new ImageMessage();
            BeanUtils.copyProperties(onlineGroupImage, imageMessage);
            message.setImageMessageList(imageList);
        }
        //表情
        else if (obj instanceof net.mamoe.mirai.message.data.Face) {
            List<FaceMessage> faceList = message.getFaceMessageList();
            if (faceList == null) {
                faceList = new ArrayList<>();
            }
            FaceMessage face = new FaceMessage();
            BeanUtils.copyProperties(obj, face);
            faceList.add(face);
            message.setFaceMessageList(faceList);
        }
        //上传文件,录像
        else if (obj instanceof FileMessageImpl) {
            FileMessage fileMessage = new FileMessage();
            BeanUtils.copyProperties(obj, fileMessage);
            message.setFileMessage(fileMessage);
        }
        //url外链
        else if (obj instanceof SimpleServiceMessage) {
            net.cancolor.easymiraiapi.model.message.SimpleServiceMessage simpleServiceMessage = new net.cancolor.easymiraiapi.model.message.SimpleServiceMessage();
            BeanUtils.copyProperties(obj, simpleServiceMessage);
            message.setSimpleServiceMessage(simpleServiceMessage);
        }
        //私聊gif
        else if (obj instanceof OnlineFriendImage) {
            List<ImageMessage> imageList = message.getImageMessageList();
            if (imageList == null) {
                imageList = new ArrayList<>();
            }
            OnlineFriendImage onlineFriendImage = (OnlineFriendImage) obj;
            ImageMessage image = new ImageMessage();
            BeanUtils.copyProperties(onlineFriendImage, image);
            imageList.add(image);
            message.setImageMessageList(imageList);
        }
        //pc震动/戳一戳（私聊）
        else if (obj instanceof net.mamoe.mirai.message.data.PokeMessage) {
            PokeMessage pokeMessage = new PokeMessage();
            BeanUtils.copyProperties(obj, pokeMessage);
            message.setPokeMessage(pokeMessage);
        }
        //手机戳一戳傍边那个
        else if (obj instanceof net.mamoe.mirai.message.data.VipFace) {
            net.mamoe.mirai.message.data.VipFace orignVipFace = (net.mamoe.mirai.message.data.VipFace) obj;
            net.mamoe.mirai.message.data.VipFace.Kind kind = orignVipFace.getKind();
            VipFaceMessage vipFaceMessage = new VipFaceMessage();
            vipFaceMessage.setName(kind.getName());
            vipFaceMessage.setCount(orignVipFace.getCount());
            vipFaceMessage.setId(kind.getId());
            message.setVipFaceMessage(vipFaceMessage);
        }

    }
}
