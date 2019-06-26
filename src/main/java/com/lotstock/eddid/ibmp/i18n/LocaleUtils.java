package com.lotstock.eddid.ibmp.i18n;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Slf4j
@Component
public class LocaleUtils implements BeanFactoryAware, ApplicationContextAware {

    private static MessageSource messageSource;
    private static ApplicationContext applicationContext;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.messageSource = beanFactory.getBean("messageSource", MessageSource.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取国际化信息
     *
     * @param code 唯一编码
     * @param args
     * @return
     */
    public static String getMessage(String code, Object... args) {
        if (code == null) {
            return null;
        }
        Locale locale = LocaleContextHolder.getLocale();
//        try {
            return messageSource.getMessage(code, args, locale);
//        } catch (NoSuchMessageException e) {
//            log.warn("缺少国际化配置", e);
//            return code;
//        }
//        LocaleResolver localeResolver = applicationContext.getBean("localeResolver", LocaleResolver.class);
//        Locale locale = localeResolver.resolveLocale(null);
//        return applicationContext.getMessage(code, args, locale);
    }


}
