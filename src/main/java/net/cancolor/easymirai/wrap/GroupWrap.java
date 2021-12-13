package net.cancolor.easymirai.wrap;

import net.cancolor.easymiraiapi.model.message.role.Group;
import net.mamoe.mirai.contact.Member;

/**
 * @author Soar
 * @title: GroupMessageWrap
 * @projectName easy-mirai
 * @description: 群组实体包装实现类
 * @date 2021/12/10 23:30
 */

public class GroupWrap {

    public static Group wrap(Member member) {
        return wrap(member.getGroup());
    }

    public static Group wrap(net.mamoe.mirai.contact.Group memberGroup) {
        Group group = new Group();
        //群号
        Long id = memberGroup.getId();
        //群名
        String name = memberGroup.getName();
        group.setId(id).setName(name);
        return group;
    }


}
