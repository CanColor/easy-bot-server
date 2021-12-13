package net.cancolor.easymirai.server;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelContainer {
    private final static Map<Channel, Integer> CHANNEL_MAP = new ConcurrentHashMap();
    static Logger logger = LoggerFactory.getLogger(ChannelContainer.class);
    private static volatile ChannelContainer instance;

    /**
     * 当有客户端连接是创建channelMap
     * 懒汉
     *
     * @return
     */
    public static ChannelContainer newInstance() {
        synchronized (ChannelContainer.class) {
            if (instance == null) {
                instance = new ChannelContainer();
            }
        }
        return instance;
    }

    public Map<Channel, Integer> getChannelMap() {
        return CHANNEL_MAP;
    }

    /**
     * 客户端上线 添加进集合
     *
     * @param clientId
     * @param channel
     */
    public void addChannel(Channel channel, Integer clientId) {
        if(clientId==null){
            clientId=CHANNEL_MAP.size()+1;
        }
        logger.info("客户端: " + clientId + " 已链接," + channel);
        CHANNEL_MAP.put(channel, clientId);
    }

    /**
     * 客户端下线 删除
     *
     * @param clientId
     * @param channel
     */
    public void delChannel(Channel channel, Integer clientId) {
        if(clientId==null) {
            clientId = CHANNEL_MAP.get(channel);
        }
        logger.info("客户端: " + clientId + " 已断开链接");
        CHANNEL_MAP.remove(channel);
    }


}
