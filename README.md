# NotificationService

A lightweight Spring Boot microservice that listens for **course enrollment events** on Kafka and sends **confirmation emails** to students once their payment is successful. Built as part of a larger course-selling / e-learning platform (event-driven microservices architecture).

## Overview

When a student successfully enrolls in and pays for a course, the main application publishes an `EnrollmentNotification` event to a Kafka topic. `NotificationService` consumes that event asynchronously and sends the student a payment/enrollment confirmation email — decoupling notification delivery from the core enrollment/payment flow.

## Features

- Event-driven architecture using **Apache Kafka** (consumer)
- Automated **enrollment confirmation emails** via SMTP (Gmail)
- JSON message deserialization with type-safe Kafka listener
- Configurable, toggleable mail sending (`app.mail.enabled`) — useful for local dev/testing without spamming real inboxes
- Externalized, type-safe configuration using `@ConfigurationProperties`
- Clean separation of concerns: config, model, service, consumer layers

## Tech Stack

| Layer            | Technology                          |
|-------------------|--------------------------------------|
| Language           | Java 21                             |
| Framework          | Spring Boot 4.1.0                   |
| Messaging          | Apache Kafka (Spring Kafka)         |
| Email              | Spring Boot Starter Mail (JavaMailSender, SMTP) |
| Serialization      | Jackson (JSON)                      |
| Build Tool         | Maven                               |

## Architecture Flow

```
[Main App / Enrollment-Payment Service]
        │  publishes EnrollmentNotification event
        ▼
   Kafka Topic: course-enrollment-notification
        ▼
 [NotificationConsumer] (@KafkaListener)
        ▼
   [EmailService] --> SMTP (Gmail) --> Student's Inbox
```

## Project Structure

```
src/main/java/com/cfs/notificationservice
├── config/
│   └── AppProperties.java        # Externalized config (Kafka topic, mail settings)
├── model/
│   └── EnrollmentNotification.java  # Kafka event payload (Java record)
└── service/
    ├── EmailService.java         # Builds and sends confirmation emails
    └── NotificationConsumer.java # Kafka listener that triggers email sending
```

## Kafka Event Payload

The service consumes messages of type `EnrollmentNotification`:

```json
{
  "studentName": "John Doe",
  "email": "john@example.com",
  "courseId": "CSE101",
  "courseTitle": "Java Backend Development",
  "amountInPaise": 499900,
  "razorpayOrderId": "order_XXXXXXXX",
  "razorpayPaymentId": "pay_XXXXXXXX",
  "paidAt": "2026-07-01T10:15:30Z"
}
```

## Prerequisites

- Java 21+
- Maven 3.9+
- A running Kafka broker (default expected at `localhost:9092`)
- A Gmail account with an **App Password** generated (do not use your normal account password)

## Configuration

Set the following in `src/main/resources/application.properties` (or better, as environment variables):

```properties
spring.application.name=NotificationService
server.port=8081

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
app.mail.enabled=true
app.mail.from=${spring.mail.username}

# Kafka
app.kafka.topic=course-enrollment-notification
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.use.type.headers=false
spring.kafka.consumer.properties.spring.json.value.default.type=com.cfs.notificationservice.model.EnrollmentNotification
```

> ⚠️ **Never commit real credentials.** Use environment variables or a secrets manager in production, and add `application.properties` (or a `.env` file) to `.gitignore` if it contains secrets.

## Getting Started

```bash
# Clone the repository
git clone https://github.com/mohitpawar61/NotificationService.git
cd NotificationService

# Set required environment variables
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password

# Make sure Kafka is running locally (or update bootstrap-servers)

# Build and run
mvn clean install
mvn spring-boot:run
```

The service will start on `http://localhost:8081` and begin listening on the `course-enrollment-notification` Kafka topic.

## How It Works

1. `NotificationConsumer` listens on the configured Kafka topic using `@KafkaListener`.
2. On receiving an `EnrollmentNotification` message, it logs the event and delegates to `EmailService`.
3. `EmailService` checks if mail sending is enabled (`app.mail.enabled`), builds a `SimpleMailMessage` with enrollment details, and sends it via `JavaMailSender`.
4. Success/failure is logged for observability.

## Future Improvements

- Add retry & dead-letter-topic handling for failed email deliveries
- Support HTML email templates (Thymeleaf) instead of plain text
- Add SMS/push notification channels
- Add unit and integration tests (Kafka embedded test broker)
- Add Dockerfile and docker-compose for local Kafka + service setup

## License

This project currently has no license specified. Add one (e.g., MIT) if you plan to open-source it.
