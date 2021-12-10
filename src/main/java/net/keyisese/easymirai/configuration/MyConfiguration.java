package net.keyisese.easymirai.configuration;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * @author soarDao
 * @date 2021-12-10 15:35
 */
@Data
@Configuration
public class MyConfiguration {
    //账号
    private Long username = 2052737713L;
    //密码
    private String password = "woainimadebi1";
    //缓存目录
    private String cacheDir = "D:/cache";
    //设备记录信息文件
    private String basedDeviceInfo = "D:/myDeviceInfo.json";
    //缓存保存时间
    private Integer saveIntervalMillis = 60000;
}
