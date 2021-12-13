package net.cancolor.easymirai;

import net.cancolor.easymirai.bot.BotThread;
import net.cancolor.easymirai.server.WebServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Soar
 * @date 2021-12-10 15:35
 */
@SpringBootApplication
public class EasyMiraiApplication implements CommandLineRunner {

    @Autowired
    private WebServer webServer;
    public static void main(String[] args) {
        SpringApplication.run(EasyMiraiApplication.class, args);
    }


    //多线程启动mirai
    @Override
    public void run(String... args) throws Exception {
        Thread scanerTheard = new Thread(new BotThread());
        scanerTheard.run();
        webServer.start();
    }


}
