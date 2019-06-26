package com.lotstock.eddid.ibmp.config;


import com.lotstock.eddid.ibmp.common.ServiceFallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
@EnableZuulProxy // 文件上传乱码
@ImportAutoConfiguration({AccessAutoConfigure.class, ParamSafeAutoConfigure.class, CorsAutoConfigure.class})
@ComponentScan({"com.lotstock.eddid.ibmp.i18n"})
public class ZuulAutoConfigure {


    //    @ConditionalOnMissingBean
    @LoadBalanced
    @Bean(name = "gatewayRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    /**
     * 异常处理
     */
//    @Bean
//    public ErrorFilter errorFilter(AppProperties appProperties) {
//        return new ErrorFilter(appProperties.getErrorPath());
//    }

    /**
     * 服务调用失败处理
     */
    @Bean
    public ServiceFallback serviceFallback() {
        return new ServiceFallback();
    }


}
