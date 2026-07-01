package com.cfs.notificationservice.model;

import java.time.Instant;

public record EnrollmentNotification(

        String studentName,
        String email,
        String courseId,
        String courseTitle,
        int amountInPaise,
        String razorpayOrderId,
        String razorpayPaymentId,
        Instant paidAt
) {
}
