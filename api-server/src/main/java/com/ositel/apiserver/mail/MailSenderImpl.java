package com.ositel.apiserver.mail;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class MailSenderImpl implements IMailSender {

    private JavaMailSenderImpl mailSender;

    public MailSenderImpl(Environment environment) {
        mailSender = new JavaMailSenderImpl();

        mailSender.setHost(environment.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.parseInt(environment.getProperty("spring.mail.port")));
        mailSender.setUsername(environment.getProperty("spring.mail.username"));
        mailSender.setPassword(environment.getProperty("spring.mail.password"));
    }

    @Override
    public void sendFeedback(String from, String name, String feedback) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("feedback@noteit.com");
        message.setFrom(from);
        message.setSubject("New feedback from " + name);
        message.setText(feedback);
        this.mailSender.send(message);
    }

    @Override
    public void sendNotificaton(String to, String name, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("alilat.imad@gmail.com");
        message.setSubject("Notification from " + name);
        message.setText(body);
        this.mailSender.send(message);
    }
}
