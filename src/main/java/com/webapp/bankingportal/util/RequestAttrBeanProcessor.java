package com.webapp.bankingportal.util;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

@Component
public class RequestAttrBeanProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof HttpServletRequest){
            DispatcherServlet filter = (DispatcherServlet)bean;
            filter.setThreadContextInheritable(true);
        }
        return bean;
    }
}
