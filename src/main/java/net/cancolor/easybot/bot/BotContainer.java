package net.cancolor.easybot.bot;


import net.cancolor.easybot.server.ChannelContainer;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Soar
 * @title: BotContainer
 * @projectName easy-mirai
 * @description: 容器
 * @date 2021/12/12 12:21
 */
public class BotContainer {
    private final static Map<Long, Bot> CHANNEL_MAP = new ConcurrentHashMap();
    private static volatile BotContainer instance;
    static Logger logger = LoggerFactory.getLogger(BotContainer.class);

    /**
     * 当有客户端连接是创建channelMap
     * 懒汉
     *
     * @return
     */
    public static BotContainer newInstance() {
        synchronized (ChannelContainer.class) {
            if (instance == null) {
                instance = new BotContainer();
            }
        }
        return instance;
    }

    public Map<Long, Bot> getBotContainer() {
        return CHANNEL_MAP;
    }

    /**
     * bot上线 添加进集合
     *
     * @param botId
     * @param bot
     */
    public void addBot(Long botId, Bot bot) {
        logger.info("botId: {} ,botName: {},已登录 ", botId, bot.getNick());
        CHANNEL_MAP.put(botId, bot);
    }

    /**
     * Bot下线 删除
     *
     * @param botId
     * @param bot
     */
    public void delBot(Long botId, Bot bot) {
        logger.info("botId: {} ,botName: {},已下线 ", botId, bot.getNick());
        CHANNEL_MAP.remove(bot);
    }


    /**
     * 获取好友列表
     */
    public static ContactList<Friend> getFriendList(Long botId) {
        Bot bot = CHANNEL_MAP.get(botId);
        ContactList<Friend> friendList = bot.getFriends();
        return friendList;
    }

    /**
     * 获取群列表
     */
    public static ContactList<Group> getGroupList(Long botId) {
        Bot bot = CHANNEL_MAP.get(botId);
        ContactList<Group> groupList = bot.getGroups();
        return groupList;
    }

    /**
     * 获取好友
     */
    public static Friend getFriend(Long botId, Long friendId) {
        Bot bot = CHANNEL_MAP.get(botId);
        Friend friend = bot.getFriend(friendId);
        return friend;
    }

    /**
     * 获取群
     */
    public static Group getGroup(Long botId, Long groupId) {
        Bot bot = CHANNEL_MAP.get(botId);
        Group group = bot.getGroup(groupId);
        return group;
    }

}
