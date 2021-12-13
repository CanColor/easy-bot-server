package net.cancolor.easymirai.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Soar
 * @date 2021-12-10 15:35
 * @description 配置类
 */
@Data
@Configuration
public class MyConfiguration {
    //账号
    @Value("${qq.username}")
    private Long qqUsername;
    //密码
    @Value("${qq.password}")
    private String qqPassword;
    //缓存目录
    @Value("${cacheDir}")
    private String cacheDir;
    //设备记录信息文件
    @Value("${basedDeviceInfo}")
    private String basedDeviceInfo;
    //缓存保存时间
    @Value("${saveIntervalMillis}")
    private Integer saveIntervalMillis;
}
