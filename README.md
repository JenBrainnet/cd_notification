## Notification Service

Notification Service is a Spring Boot microservice for CheckDev user notifications. It stores internal messages, sends interview-related notifications, manages topic
and category subscriptions, and integrates with Telegram for external alerts.

## Responsibilities

- Store and manage internal user messages.
- Notify users about interview events.
- Send notifications to topic and category subscribers.
- Support Telegram bot registration, account binding, and message delivery.
- Register as a Eureka client for service discovery.

## Build

  ```bash
  mvn clean install
  ```

## Run

  mvn spring-boot:run