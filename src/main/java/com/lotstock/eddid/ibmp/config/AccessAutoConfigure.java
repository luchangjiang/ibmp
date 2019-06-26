package com.lotstock.eddid.ibmp.config;


import com.lotstock.eddid.ibmp.constant.Constants;
import com.lotstock.eddid.ibmp.access.AccessFilter;
import com.lotstock.eddid.ibmp.access.AccessProperties;
import com.lotstock.eddid.ibmp.access.AccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Slf4j
@ConditionalOnProperty(prefix = Constants.CONFIG_PREFIX + ".access", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(AccessProperties.class)
public class AccessAutoConfigure {

    /**
     * 配置方式二：单独配置用户中心服务ID，不配置的话代表默认当前请求服务
     */
    @Bean
    public AccessFilter accessFilter(@Qualifier("gatewayRestTemplate") RestTemplate restTemplate, AccessProperties accessProperties) {
        AccessService accessService = new AccessService(restTemplate, accessProperties.getUserPermissionUrl());
        AccessFilter accessFilter = new AccessFilter(accessService);
        accessFilter.setAccessProperties(accessProperties);
//        accessFilter.setZuulServletPath(zuulProperties.getServletPath());
        return accessFilter;
    }


}
