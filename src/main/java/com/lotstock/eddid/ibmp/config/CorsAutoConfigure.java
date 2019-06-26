package com.lotstock.eddid.ibmp.config;

import com.lotstock.eddid.ibmp.constant.Constants;
import com.lotstock.eddid.ibmp.cors.CorsProperteis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Slf4j
@ConditionalOnProperty(prefix = Constants.CONFIG_PREFIX + ".cors", value = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(CorsProperteis.class)
public class CorsAutoConfigure {

    /**
     * 跨域方案一
     */
    @Bean
    public FilterRegistrationBean corsFilter(CorsProperteis corsConfiguration) {
        if (corsConfiguration == null) {
            corsConfiguration = new CorsProperteis();
        }
        corsConfiguration.applyPermitDefaultValues();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        if (corsConfiguration.getPathPatterns() != null) {
            for (String path : corsConfiguration.getPathPatterns()) {
                source.registerCorsConfiguration(path, corsConfiguration);
            }
        } else {
            source.registerCorsConfiguration("/**", corsConfiguration);
        }
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    // 跨域方案二
//    @Bean
//    public FirstFilter firstFilter(AppProperties appProperties){
//        return new FirstFilter(appProperties);
//    }
//
//    @Bean
//    public PostFilter postFilter(AppProperties appProperties){
//        return new PostFilter(appProperties);
//    }


}
