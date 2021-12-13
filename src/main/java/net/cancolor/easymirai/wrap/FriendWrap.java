package net.cancolor.easymirai.wrap;

import net.cancolor.easymiraiapi.model.message.role.Friend;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.UserOrBot;

/**
 * @author Soar
 * @title: FriendWrap
 * @projectName easy-mirai
 * @description: QQ用户包装类
 * @date 2021/12/11 0:51
 */
public class FriendWrap {

    public static Friend wrap(Member member){
        Friend friend=new Friend();
        //群里QQ名片
        String nameCard = member.getNameCard();
        //QQ名称
        String nike = member.getNick();
        //QQ账号
        Long id = member.getId();
        friend.setId(id).setNike(nike).setNameCard(nameCard);
        return friend;
    }


    public static Friend wrap(UserOrBot userOrBot){
        Friend friend=new Friend();
        //QQ名称
        String nike = userOrBot.getNick();
        //QQ账号
        Long id = userOrBot.getId();
        friend.setId(id).setNike(nike);
        return friend;
    }

    public static Friend wrap(net.mamoe.mirai.contact.Friend member){
        Friend friend=new Friend();
        //别名
        String remark = member.getRemark();
        //QQ名称
        String nike = member.getNick();
        //QQ账号
        Long id = member.getId();
        friend.setId(id).setNike(nike).setRemake(remark).setId(id);
        return friend;
    }
}
