package net.cancolor.easymirai.server;

import io.netty.channel.Channel;
import net.cancolor.easymirai.utils.SendClientMessageUtil;
import net.cancolor.easymiraiapi.model.message.client.receive.ClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnlineChannelContainer {
    private final static Map<Integer, Channel> CHANNEL_MAP = new ConcurrentHashMap();
    static Logger logger = LoggerFactory.getLogger(OnlineChannelContainer.class);
    private static volatile OnlineChannelContainer instance;

    /**
     * 当有客户端连接是创建channelMap
     * 懒汉
     *
     * @return
     */
    public static OnlineChannelContainer newInstance() {
        synchronized (OnlineChannelContainer.class) {
            if (instance == null) {
                instance = new OnlineChannelContainer();
            }
        }
        return instance;
    }

    public static void sendAllChannel(String type, Integer isUseMiraiCode, ClientMessage message) {
        Iterator<Map.Entry<Integer, Channel>> it = CHANNEL_MAP.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Channel> entry = it.next();
            //发给除自己以外的所有人
            SendClientMessageUtil.sendClient(entry.getValue(), type, isUseMiraiCode, message);
        }
    }

    public Map<Integer, Channel> getChannelMap() {
        return CHANNEL_MAP;
    }

    /**
     * 客户端链接 添加进集合
     *
     * @param clientId
     * @param channel
     */
    public void addChannel(Integer clientId, Channel channel) {
        logger.info("客户端: " + clientId + " 已连接," + channel);
        CHANNEL_MAP.put(clientId, channel);
    }

    /**
     * 客户端断开链接 删除
     *
     * @param clientId
     * @param channel
     */
    public void delChannel(Integer clientId, Channel channel) {
        logger.info("客户端: " + clientId + " 已断开链接");
        CHANNEL_MAP.remove(clientId);
    }


}
