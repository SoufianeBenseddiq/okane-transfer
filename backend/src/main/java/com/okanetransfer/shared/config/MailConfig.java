package com.okanetransfer.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${mail.host:localhost}")
    private String host;

    @Value("${mail.port:1025}")
    private int port;

    @Value("${mail.username:}")
    private String username;

    @Value("${mail.password:}")
    private String password;

    @Value("${mail.smtp.auth:false}")
    private boolean smtpAuth;

    @Value("${mail.smtp.starttls.enable:false}")
    private boolean startTlsEnable;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        if (username != null && !username.isBlank()) {
            mailSender.setUsername(username);
        }
        if (password != null && !password.isBlank()) {
            mailSender.setPassword(password);
        }

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", String.valueOf(smtpAuth));
        properties.put("mail.smtp.starttls.enable", String.valueOf(startTlsEnable));
        properties.put("mail.smtp.connectiontimeout", "5000");
        properties.put("mail.smtp.timeout", "5000");
        properties.put("mail.smtp.writetimeout", "5000");

        return mailSender;
    }
}