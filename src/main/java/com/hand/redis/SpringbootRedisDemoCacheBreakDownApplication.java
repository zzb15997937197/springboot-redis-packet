package com.hand.redis;

import org.mybatis.spring.annotation.MapperScan;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.hand.redis.mapper")
public class SpringbootRedisDemoCacheBreakDownApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootRedisDemoCacheBreakDownApplication.class, args);
    }


    @Bean
    public Redisson redisson(){
        //初始化redisson客户端，单机模式
        Config config=new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(0).setPassword("123456");
        return (Redisson)Redisson.create(config);
    }
}
