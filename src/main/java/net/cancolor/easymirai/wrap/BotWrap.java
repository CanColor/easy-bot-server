package net.cancolor.easymirai.wrap;


import net.cancolor.easymiraiapi.model.message.role.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

/**
 * @author Soar
 * @title: BotWrap
 * @projectName easy-mirai
 * @description: 机器包装类
 * @date 2021/12/11 1:03
 */
public class BotWrap {


    public static Bot wrap(Member member) {
        Bot bot = new Bot();
        //机器人信息
        net.mamoe.mirai.Bot memberBot = member.getBot();
        //机器QQ号
        Long id = memberBot.getId();
        String name = memberBot.getNick();
        bot.setId(id).setName(name);
        return bot;
    }

    public static Bot wrap(net.mamoe.mirai.Bot bot) {
        Bot myBot = new Bot();
        //机器QQ号
        Long id = bot.getId();
        String name = bot.getNick();
        myBot.setId(id).setName(name);
        return myBot;
    }

    public static Bot wrap(Friend friend) {
        Bot bot = new Bot();
        //机器人信息
        net.mamoe.mirai.Bot memberBot = friend.getBot();
        //机器QQ号
        Long id = memberBot.getId();
        String name = memberBot.getNick();
        bot.setId(id).setName(name);
        return bot;
    }


}
