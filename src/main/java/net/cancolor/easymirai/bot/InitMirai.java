package net.cancolor.easymirai.bot;


import net.cancolor.easymirai.configuration.MyConfiguration;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Soar
 * @date 2021-12-10 15:35
 * @description 初始化
 */
@Component
public class InitMirai {

    @Autowired
    MyConfiguration configuration;

    //初始化机器人 启动应用时启动这个方法
    public Bot start() {
        // Java
        Bot bot = BotFactory.INSTANCE.newBot(configuration.getQqUsername(), configuration.getQqPassword(), new BotConfiguration() {{
            // 配置，例如：
            fileBasedDeviceInfo();
            //心跳策略默认为最佳的 STAT_HB，但不适用于一些账号。
            //Mirai 支持多种登录协议：ANDROID_PHONE，ANDROID_PAD，ANDROID_WATCH，默认使用 ANDROID_PHONE。
            //ANDROID_PHONE戳一戳
            setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PHONE);
            //如果遇到 Bot 闲置一段时间后，发消息返回成功但群内收不到的情况，请切换心跳策略，依次尝试 STAT_HB、REGISTER 和 NONE。
            setHeartbeatStrategy(BotConfiguration.HeartbeatStrategy.REGISTER);
            setCacheDir(new File(configuration.getCacheDir()));// 最终为 workingDir 目录中的 cache 目录
            fileBasedDeviceInfo(configuration.getBasedDeviceInfo());
            ContactListCache contactListCache = getContactListCache();
            contactListCache.setSaveIntervalMillis(contactListCache.getSaveIntervalMillis());// 可选设置有更新时的保存时间间隔, 默认 60 秒
            //开启缓存
            enableContactCache();

        }});
        //登录
        bot.login();
        BotContainer.newInstance().addBot(configuration.getQqUsername(),bot);
        return bot;
    }


}
