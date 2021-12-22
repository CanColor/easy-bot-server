package net.cancolor.easybot.handler;


import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import net.cancolor.easybot.bot.BotContainer;
import net.cancolor.easybot.handler.message.send.SendMessageHandler;
import net.cancolor.easybot.server.ChannelContainer;
import net.cancolor.easybot.server.OnlineChannelContainer;
import net.cancolor.easybot.utils.SendClientMessageUtil;
import net.cancolor.easybotapi.constant.MessageConstant;
import net.cancolor.easybotapi.model.message.dto.SendClientMessageDTO;
import net.cancolor.easybotapi.model.message.dto.SendServerMessageDTO;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WebSocketServerHandle extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(WebSocketServerHandle.class);
    private final AttributeKey<Integer> uid = AttributeKey.valueOf("username");
    private final Map<Channel, Integer> channelMap = ChannelContainer.newInstance().getChannelMap();
    private final Map<Long, Bot> botContainer = BotContainer.newInstance().getBotContainer();

    //接受客户端消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;
            logger.info("接受客户端消息:{}", textWebSocketFrame.text());
            String comond = null;
            String clientName = null;
            Integer clientId = null;
            Integer isUseMiraiCode = null;
            List<Long> botIdList = null;
            Integer isPrivate = null;
            Long friendId = null;
            Long groupId = null;
            SendServerMessageDTO sendServerMessageDTO = null;
            try {
                sendServerMessageDTO = JSONObject.parseObject(textWebSocketFrame.text(), SendServerMessageDTO.class);
                if (sendServerMessageDTO != null) {
                    comond = sendServerMessageDTO.getComond();
                    clientName = sendServerMessageDTO.getClientName();
                    clientId = sendServerMessageDTO.getClientId();
                    isUseMiraiCode = sendServerMessageDTO.getIsUseMiraiCode();
                    botIdList = sendServerMessageDTO.getBotIdList();
                    friendId = sendServerMessageDTO.getFriendId();
                    groupId = sendServerMessageDTO.getGroupId();
                }
            } catch (Exception e) {
                SendClientMessageUtil.sendClient(ctx.channel(), "消息格式错误！");
                return;
            }

            //系统指令
            if (comond.equals(MessageConstant.LOGIN)) {
                //登陆
                ctx.channel().attr(uid).set(clientId.intValue());
                ChannelContainer.newInstance().delChannel(ctx.channel(), clientId);
                OnlineChannelContainer.newInstance().addChannel(clientId, ctx.channel());
                logger.info("客户端--->clentId:{} , clientName: {} ,登录！", clientId, clientName);
                SendClientMessageDTO clientMessageDTO = new SendClientMessageDTO();
                clientMessageDTO.setSystemMessage("clientId: " + clientId + ",clientName: " + clientName + ",已上线");
                clientMessageDTO.setComond("system");
                ctx.channel().writeAndFlush(JSONObject.toJSONString(clientMessageDTO));
            }

            if (comond.equals(MessageConstant.LOGIN_OUT)) {
                //用户主动下线
                logger.info("客户端--->clentId:{} , clientName: {} ,已主动端开链接", clientId, clientName);
                ctx.channel().write("下线成功！");
                ctx.channel().close();
                ChannelContainer.newInstance().delChannel(ctx.channel(), clientId);
                OnlineChannelContainer.newInstance().delChannel(clientId, ctx.channel());
            } else if (comond.equals(MessageConstant.CHAT)) {
                Iterator<Map.Entry<Long, Bot>> it = botContainer.entrySet().iterator();
                while (it.hasNext()) {
                    //接受客户端消息
                    Map.Entry<Long, Bot> entry = it.next();
                    //发给指定bot
                    for (Long botId : botIdList) {
                        if (entry.getKey().longValue() == botId.longValue()) {
                            Bot bot = BotContainer.newInstance().getBotContainer().get(botId);
                            Friend friend = null;
                            //私聊
                            if (groupId == null) {
                                friend = BotContainer.getFriend(botId, friendId);
                                //获取好友
                                if (isUseMiraiCode == 1) {
                                    String miraiCode = sendServerMessageDTO.getMiraiCode();
                                    SendMessageHandler.sendFriendMessage(ctx.channel(), bot, friend, miraiCode);
                                } else {
                                    SendMessageHandler.sendFriendMessage(ctx.channel(), bot, friend, sendServerMessageDTO);
                                }
                            } else {
                                Group group = BotContainer.getGroup(botId, groupId);
                                NormalMember normalMember = null;
                                if (friendId != null) {
                                    normalMember = group.get(friendId);
                                }
                                //群聊获取群列表
                                if (isUseMiraiCode == 1) {
                                    String miraiCode = sendServerMessageDTO.getMiraiCode();
                                    SendMessageHandler.sendGroupMessage(ctx.channel(), bot, group, normalMember, miraiCode);
                                } else {
                                    SendMessageHandler.sendGroupMessage(ctx.channel(), bot, group, normalMember, sendServerMessageDTO);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelContainer.newInstance().addChannel(ctx.channel(), null);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        ChannelContainer.newInstance().delChannel(ctx.channel(), null);
        Iterator<Map.Entry<Integer, Channel>> it = OnlineChannelContainer.newInstance().getChannelMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Channel> entry = it.next();
            if (entry.getValue().id().equals(ctx.channel().id())) {
                OnlineChannelContainer.newInstance().delChannel(entry.getKey(), entry.getValue());
                logger.info("客户端断开:{}, channel:{}", entry.getKey(), entry.getValue());
            }
        }
    }
}

