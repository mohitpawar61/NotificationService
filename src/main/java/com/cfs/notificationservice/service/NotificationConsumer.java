package com.cfs.notificationservice.service;

import com.cfs.notificationservice.model.EnrollmentNotification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private final EmailService emailService;


    public NotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "notificationservice")
    public void consume(EnrollmentNotification notification) {

        System.out.println("Message Received");
        System.out.println(notification);

        emailService.sendEnrollmentEmail(notification);
    }
}
