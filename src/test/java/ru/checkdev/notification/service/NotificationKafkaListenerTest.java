package ru.checkdev.notification.service;

import org.junit.jupiter.api.Test;
import ru.checkdev.notification.dto.CancelInterviewNotificationDTO;
import ru.checkdev.notification.dto.CategoryWithTopicDTO;
import ru.checkdev.notification.dto.FeedbackNotificationDTO;
import ru.checkdev.notification.dto.InnerMessageDTO;
import ru.checkdev.notification.dto.InterviewNotifyDTO;
import ru.checkdev.notification.dto.WisherApprovedDTO;
import ru.checkdev.notification.dto.WisherDismissedDTO;
import ru.checkdev.notification.dto.WisherNotifyDTO;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificationKafkaListenerTest {

    private final NotificationEventService notificationEventService = mock(NotificationEventService.class);
    private final NotificationKafkaListener listener = new NotificationKafkaListener(notificationEventService);

    @Test
    void whenReceiveInterviewTopicThenDelegateToEventService() {
        var dto = new InterviewNotifyDTO();

        listener.receiveInterviewTopic(dto);

        verify(notificationEventService).sendMessageSubscribeTopic(dto);
    }

    @Test
    void whenReceiveInterviewParticipateThenDelegateToEventService() {
        var dto = new WisherNotifyDTO();

        listener.receiveInterviewParticipate(dto);

        verify(notificationEventService).sendMessageSubmitterInterview(dto);
    }

    @Test
    void whenReceiveInterviewCancelThenDelegateToEventService() {
        var dto = new CancelInterviewNotificationDTO();

        listener.receiveInterviewCancel(dto);

        verify(notificationEventService).sendMessageCancelInterview(dto);
    }

    @Test
    void whenReceiveParticipantDismissedThenDelegateToEventService() {
        var dto = new WisherDismissedDTO();

        listener.receiveParticipantDismissed(dto);

        verify(notificationEventService).sendMessageParticipantIsDismissed(dto);
    }

    @Test
    void whenReceiveWisherApprovedThenDelegateToEventService() {
        var dto = new WisherApprovedDTO();

        listener.receiveWisherApproved(dto);

        verify(notificationEventService).sendMessageApprovedWisher(dto);
    }

    @Test
    void whenReceiveFeedbackInterviewThenDelegateToEventService() {
        var dto = new FeedbackNotificationDTO();

        listener.receiveFeedbackInterview(dto);

        verify(notificationEventService).sendFeedbackNotification(dto);
    }

    @Test
    void whenReceiveNewInterviewThenDelegateToEventService() {
        var dto = new CategoryWithTopicDTO();

        listener.receiveNewInterview(dto);

        verify(notificationEventService).createMessage(dto);
    }

    @Test
    void whenReceiveInnerMessageThenDelegateToEventService() {
        var innerMessage = new InnerMessageDTO();

        listener.receiveInnerMessage(innerMessage);

        verify(notificationEventService).sendMessage(innerMessage);
    }
}
