package net.keyisese.easymirai;

import net.keyisese.easymirai.init.InitMirai;
import net.keyisese.easymirai.utils.SpringBeanFactoryUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author soarDao
 * @date 2021-12-10 15:35
 */
@SpringBootApplication
public class EasyMiraiApplication implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(EasyMiraiApplication.class, args);
    }


    //多线程启动mirai
    @Override
    public void run(String... args) throws Exception {
        Thread scanerTheard = new Thread(new BotThread());
        scanerTheard.run();
    }


    public class BotThread implements Runnable {
        private final InitMirai mirai;

        public BotThread() {
            this.mirai = (InitMirai) SpringBeanFactoryUtil.getBean(InitMirai.class);
        }

        @Override
        public void run() {
            //初始化
            Bot bot = mirai.start();
            //登录
            bot.login();
            //监听信息
            // 创建监听
            //监听群消息
            Listener listenerGroup = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
                MessageChain chain = event.getMessage();
                // 可获取到消息内容等, 详细查阅 `GroupMessageEvent`
                PlainText plainText = (PlainText) chain.get(1);
                String message = plainText.getContent();
                System.out.println(message);
                event.getSubject().sendMessage("Hello World!"); // 回复消息
            });


            //监听私聊
            Listener listenerFriend = GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
                MessageChain chain = event.getMessage();
                // 可获取到消息内容等, 详细查阅 `GroupMessageEvent`
                PlainText plainText = (PlainText) chain.get(1);
                String message = plainText.getContent();
                System.out.println(message);
                event.getSubject().sendMessage("Hello World!"); // 回复消息
            });
//            listener.complete(); // 停止监听
        }
    }

}
