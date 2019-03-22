package com.ositel.apiserver.mail;

import java.util.Date;

public interface IMailSender {
    void sendFeedback(String from, String name, String feedback);
    void sendNotification(String to, String name, Date date);
}
