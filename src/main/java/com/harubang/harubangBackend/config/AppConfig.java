package com.harubang.harubangBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // RestTemplate을 Spring Bean으로 등록
    // @Bean을 붙여두면 다른 Service에서 @Autowired나 생성자 주입으로 바로 사용 가능
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}