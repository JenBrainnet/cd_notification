package ru.checkdev.notification.service;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.dto.CancelInterviewNotificationDTO;
import ru.checkdev.notification.dto.CategoryWithTopicDTO;
import ru.checkdev.notification.dto.FeedbackNotificationDTO;
import ru.checkdev.notification.dto.InnerMessageDTO;
import ru.checkdev.notification.dto.InterviewNotifyDTO;
import ru.checkdev.notification.dto.WisherApprovedDTO;
import ru.checkdev.notification.dto.WisherDismissedDTO;
import ru.checkdev.notification.dto.WisherNotifyDTO;

import static ru.checkdev.notification.config.NotificationKafkaTopics.FEEDBACK_INTERVIEW;
import static ru.checkdev.notification.config.NotificationKafkaTopics.INNER_MESSAGE;
import static ru.checkdev.notification.config.NotificationKafkaTopics.INTERVIEW_CANCEL;
import static ru.checkdev.notification.config.NotificationKafkaTopics.INTERVIEW_PARTICIPANT_DISMISSED;
import static ru.checkdev.notification.config.NotificationKafkaTopics.INTERVIEW_PARTICIPATE;
import static ru.checkdev.notification.config.NotificationKafkaTopics.INTERVIEW_TOPIC;
import static ru.checkdev.notification.config.NotificationKafkaTopics.NEW_INTERVIEW;
import static ru.checkdev.notification.config.NotificationKafkaTopics.WISHER_APPROVED;

@Service
@AllArgsConstructor
public class NotificationKafkaListener {
    private final NotificationEventService notificationEventService;

    @KafkaListener(topics = INTERVIEW_TOPIC)
    public void receiveInterviewTopic(InterviewNotifyDTO interviewNotifyDTO) {
        notificationEventService.sendMessageSubscribeTopic(interviewNotifyDTO);
    }

    @KafkaListener(topics = INTERVIEW_PARTICIPATE)
    public void receiveInterviewParticipate(WisherNotifyDTO wisherNotifyDTO) {
        notificationEventService.sendMessageSubmitterInterview(wisherNotifyDTO);
    }

    @KafkaListener(topics = INTERVIEW_CANCEL)
    public void receiveInterviewCancel(CancelInterviewNotificationDTO cancelInterviewDTO) {
        notificationEventService.sendMessageCancelInterview(cancelInterviewDTO);
    }

    @KafkaListener(topics = INTERVIEW_PARTICIPANT_DISMISSED)
    public void receiveParticipantDismissed(WisherDismissedDTO wisherDismissedDTO) {
        notificationEventService.sendMessageParticipantIsDismissed(wisherDismissedDTO);
    }

    @KafkaListener(topics = WISHER_APPROVED)
    public void receiveWisherApproved(WisherApprovedDTO wisherApprovedDTO) {
        notificationEventService.sendMessageApprovedWisher(wisherApprovedDTO);
    }

    @KafkaListener(topics = FEEDBACK_INTERVIEW)
    public void receiveFeedbackInterview(FeedbackNotificationDTO feedbackNotification) {
        notificationEventService.sendFeedbackNotification(feedbackNotification);
    }

    @KafkaListener(topics = NEW_INTERVIEW)
    public void receiveNewInterview(CategoryWithTopicDTO categoryWithTopicDTO) {
        notificationEventService.createMessage(categoryWithTopicDTO);
    }

    @KafkaListener(topics = INNER_MESSAGE)
    public void receiveInnerMessage(InnerMessageDTO innerMessage) {
        notificationEventService.sendMessage(innerMessage);
    }
}
