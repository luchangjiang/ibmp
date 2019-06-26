package com.lotstock.eddid.ibmp.config;

import com.lotstock.eddid.ibmp.constant.Constants;
import com.lotstock.eddid.ibmp.param.ParamDecodeFilter;
import com.lotstock.eddid.ibmp.param.ParamManager;
import com.lotstock.eddid.ibmp.param.ParamProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@ConditionalOnProperty(prefix = Constants.CONFIG_PREFIX + ".param-safe", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(value = ParamProperties.class)
@ComponentScan(basePackages = "com.ibmp.route.param")
public class ParamSafeAutoConfigure {


    @ConditionalOnMissingBean
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

//    @Bean
//    public RedisClient redisClient(StringRedisTemplate stringRedisTemplate) {
//        return new RedisClient(stringRedisTemplate);
//    }

    @Bean
    public ParamManager paramManager(StringRedisTemplate stringRedisTemplate, ParamProperties paramProperties) throws Exception {
        return new ParamManager(stringRedisTemplate, paramProperties);
    }

    @Bean
    public ParamDecodeFilter paramDecodeFilter(ParamManager paramManager, ParamProperties paramProperties) {
        return new ParamDecodeFilter(paramManager, paramProperties);
    }


}
//@ConditionalOnClass(RedisConnectionFactory.class)
