package com.jobflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

@Configuration
public class HttpClientConfig {

    @Bean
    public CookieManager cookieManager() {
        CookieManager cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        return cm;
    }
}
