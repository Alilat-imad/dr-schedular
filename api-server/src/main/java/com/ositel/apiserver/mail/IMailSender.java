package com.ositel.apiserver.mail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public interface IMailSender {
    void sendFeedback(String from, String name, String feedback);
    void notifyPatient(boolean status, String medecinFullName, String patientMail, LocalDate date, LocalTime timeStart, LocalTime timeEnd);

}
