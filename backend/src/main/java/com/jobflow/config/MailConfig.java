package com.jobflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

        @Bean
        public JavaMailSender javaMailSender() {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("localhost");
            mailSender.setPort(1025);
            mailSender.setProtocol("smtp");
            Properties p = mailSender.getJavaMailProperties();
            p.put("mail.smtp.auth", "false");
            p.put("mail.smtp.starttls.enable", "false");
            return mailSender;
        }

}
