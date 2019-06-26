package com.lotstock.eddid.ibmp.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;


@Configuration
@EnableConfigurationProperties(I18nProperties.class)
public class LocaleConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private I18nProperties i18nProperties;

    /**
     * Locale解析
     **/
    @Bean
    public LocaleResolver localeResolver() {

        if ("session".equalsIgnoreCase(i18nProperties.getResolveType())) {
            SessionLocaleResolver localeResolver = new SessionLocaleResolver();
            localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        } else if ("header".equalsIgnoreCase(i18nProperties.getResolveType())) {
            AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
            localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
            return localeResolver;
        }
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieName("lang");
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE); // 简体中文
        localeResolver.setCookieMaxAge(3600);//设置cookie有效期.
        return localeResolver;
    }


    /**
     * 切换语言
     * <a href="?lang=zh_CN">简体中文</a> &nbsp;&nbsp;
     * <a href="?lang=en_US">English</a><br>
     */
//    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor changeInterceptor = new LocaleChangeInterceptor();
        changeInterceptor.setParamName("lang");
        return changeInterceptor;
    }

    /**
     * 注册语言切换拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/message");
        return messageSource;
    }

}
