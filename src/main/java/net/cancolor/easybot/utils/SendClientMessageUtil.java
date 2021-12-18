package net.cancolor.easybot.utils;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.cancolor.easybotapi.constant.MessageConstant;

import net.cancolor.easybotapi.model.message.dto.SendClientMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//服务端发送信息到客户端
public class SendClientMessageUtil {
    static Logger logger = LoggerFactory.getLogger(SendClientMessageUtil.class);


    public static void sendClient(Channel channel, SendClientMessageDTO sendClientMessageDTO) {
        channel.writeAndFlush(wrapMessage(JSONObject.toJSONString(sendClientMessageDTO)));
        send(channel, sendClientMessageDTO);
        logger.info("发送客户端消息: {}", JSONObject.toJSONString(sendClientMessageDTO));
    }


    public static void sendClient(Channel channel, String comond, Integer isUseMiraiCode, SendClientMessageDTO sendClientMessageDTO) {
        //mirai消息
        sendClientMessageDTO.setComond(comond);
        sendClientMessageDTO.setIsUseMiraiCode(isUseMiraiCode);
        send(channel, sendClientMessageDTO);
        logger.info("发送客户端消息: {}", JSONObject.toJSONString(sendClientMessageDTO));
    }

    //系统消息
    public static void sendClient(Channel channel, String systemMessage) {
        SendClientMessageDTO sendClientMessageDTO = new SendClientMessageDTO();
        sendClientMessageDTO.setSystemMessage(systemMessage);
        sendClientMessageDTO.setComond(MessageConstant.SYSTEM);
        send(channel, sendClientMessageDTO);
        logger.info("发送客户端系统消息: {}", JSONObject.toJSONString(systemMessage));

    }

    public static TextWebSocketFrame wrapMessage(String systemMessage) {
        return new TextWebSocketFrame(systemMessage);
    }


    public static void send(Channel channel, SendClientMessageDTO sendClientMessageDTO) {
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(JSONObject.toJSONString(sendClientMessageDTO));
        channel.writeAndFlush(textWebSocketFrame).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    logger.info("发送客户端消息成功：" + JSONObject.toJSONString(sendClientMessageDTO));
                } else {
                    logger.info("发送客户端消息失败 " + channelFuture.cause().getMessage());
                }

            }
        });
    }


}
