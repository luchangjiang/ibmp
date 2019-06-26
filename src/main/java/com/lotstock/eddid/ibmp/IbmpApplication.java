package com.lotstock.eddid.ibmp;

import com.lotstock.eddid.ibmp.config.ZuulAutoConfigure;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@ImportAutoConfiguration({ZuulAutoConfigure.class})
public class IbmpApplication {
//    public class RouteApplication extends SpringBootServletInitializer {

    protected static final Logger log = Logger.getLogger(IbmpApplication.class);

    //开启均衡负载能力
    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(IbmpApplication.class, args);
        log.info("ibmp start success！");
    }
}
