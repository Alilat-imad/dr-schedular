package com.ositel.apiserver.mail;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

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
    public void sendNotification(String to, String patientName, Date date) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("alilat.imad@gmail.com");
        message.setSubject("New appointement added.");
        message.setText("Le patient "+ patientName+ " vient de prendre un rendez-vous pour le : "+date );
        this.mailSender.send(message);
    }

    @Override
    public void notifyPatient(boolean status ,String medecinFullName, String patientMail, LocalDate date, LocalTime timeStart, LocalTime timeEnd) {
        SimpleMailMessage message = new SimpleMailMessage();
        var reponse = (status)? "confirmer" : "annuler";
        message.setTo(patientMail);
        message.setFrom("alilat.imad@gmail.com");
        message.setSubject("Rendez-vous "+ reponse +" par Dr."+ medecinFullName);
        message.setText("Le docteur. "+ medecinFullName+" a " + reponse +
                " votre demande de rendez-vous pour le "+date+" de "+ timeStart+ " à "+timeEnd+".");
        this.mailSender.send(message);
    }
}
