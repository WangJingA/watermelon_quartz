package org.com.watermelon.watermelon_quartz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;



@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(value = "org.com.watermelon.watermelon_quartz.mapper")
public class WatermelonQuartzApplication {

    public static void main(String[] args) {
        SpringApplication.run(WatermelonQuartzApplication.class, args);
    }

}
