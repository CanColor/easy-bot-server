package net.cancolor.easymirai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class InitConfiguration {


    @Value("${netty.port}")
    public int nettyPort;


}

