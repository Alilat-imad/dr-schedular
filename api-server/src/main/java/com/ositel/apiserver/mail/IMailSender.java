package com.ositel.apiserver.mail;

public interface IMailSender {
    void sendFeedback(String from, String name, String feedback);
    void sendNotificaton(String to, String name, String body);
}
