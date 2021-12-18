package net.cancolor.easybot.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/*
 * @author Soar
 * @date 2021-12-10
 */
@Component
public class SpringBeanFactoryUtil implements ApplicationContextAware {
    public static ApplicationContext context;


    public static Object getBean(Class clz) {
        return context.getBean(clz);
    }

    public static Object getBean(String name, Class clz) {
        return context.getBean(name, clz);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
