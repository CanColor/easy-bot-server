package net.cancolor.easybot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class InitConfiguration {


    @Value("${webSocket.port}")
    public int webSocketPort;


}

