package ru.checkdev.notification.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.CancelInterviewNotificationDTO;
import ru.checkdev.notification.dto.CategoryWithTopicDTO;
import ru.checkdev.notification.dto.FeedbackNotificationDTO;
import ru.checkdev.notification.dto.InnerMessageDTO;
import ru.checkdev.notification.dto.InterviewNotifyDTO;
import ru.checkdev.notification.dto.WisherApprovedDTO;
import ru.checkdev.notification.dto.WisherDismissedDTO;
import ru.checkdev.notification.dto.WisherNotifyDTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class NotificationEventService {
    private final UserTelegramService userTelegramService;
    private final InnerMessageService innerMessageService;
    private final NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;
    private final MessagesGenerator messagesGenerator;
    private final SubscribeCategoryService categoryService;
    private final SubscribeTopicService topicService;
    private final NotificationMessagesService notificationMessagesService;

    public List<InnerMessage> sendMessageSubscribeTopic(InterviewNotifyDTO interviewNotifyDTO) {
        List<UserTelegram> usersTopic = userTelegramService
                .findAllByTopicIdAndUserIdNot(interviewNotifyDTO.getTopicId(),
                        interviewNotifyDTO.getSubmitterId());
        var message = messagesGenerator.getMessageSubscribeTopic(interviewNotifyDTO);
        return notificationMessage.sendMessage(usersTopic, message);
    }

    public InnerMessage sendMessageSubmitterInterview(WisherNotifyDTO wisherNotifyDTO) {
        var message = messagesGenerator.getMessageParticipateWisher(wisherNotifyDTO);
        InnerMessage innerMessage = InnerMessage.of()
                .userId(wisherNotifyDTO.getSubmitterId())
                .text(message)
                .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .read(false)
                .interviewId(wisherNotifyDTO.getInterviewId())
                .build();
        innerMessageService.saveMessage(innerMessage);
        userTelegramService
                .findByUserId(wisherNotifyDTO.getSubmitterId())
                .ifPresent(tg -> notificationMessage.sendMessage(tg, message));
        return innerMessage;
    }

    public InnerMessage sendMessageCancelInterview(CancelInterviewNotificationDTO cancelInterviewDTO) {
        var message = messagesGenerator.getMessageCancelInterview(cancelInterviewDTO);
        InnerMessage innerMessage = InnerMessage.of()
                .userId(cancelInterviewDTO.getUserId())
                .text(message)
                .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .read(false)
                .interviewId(cancelInterviewDTO.getInterviewId())
                .build();
        CompletableFuture.supplyAsync(() -> innerMessageService.saveMessage(innerMessage));
        userTelegramService
                .findByUserId(cancelInterviewDTO.getUserId())
                .ifPresent(tg -> notificationMessage.sendMessage(tg, message));
        return innerMessage;
    }

    public List<InnerMessage> sendMessageParticipantIsDismissed(List<WisherDismissedDTO> wisherDtoList) {
        return wisherDtoList.stream()
                .map(wisher -> {
                    var message = messagesGenerator.getMessageDismissedWisher(wisher);
                    var innerMessage = InnerMessage.of()
                            .userId(wisher.getUserId())
                            .text(message)
                            .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                            .read(false)
                            .interviewId(wisher.getInterviewId())
                            .build();
                    CompletableFuture.supplyAsync(() -> innerMessageService.saveMessage(innerMessage));
                    userTelegramService
                            .findByUserId(wisher.getUserId())
                            .ifPresent(tg -> notificationMessage.sendMessage(tg, message));
                    return innerMessage;
                })
                .toList();
    }

    public InnerMessage sendMessageParticipantIsDismissed(WisherDismissedDTO wisher) {
        return sendMessageParticipantIsDismissed(List.of(wisher)).get(0);
    }

    public void sendMessageApprovedWisher(WisherApprovedDTO wisherApprovedDTO) {
        notificationMessagesService.sendApprovedNotification(wisherApprovedDTO);
    }

    public void sendFeedbackNotification(FeedbackNotificationDTO feedbackNotification) {
        notificationMessagesService.sendFeedbackNotification(feedbackNotification);
    }

    @Transactional
    public void createMessage(CategoryWithTopicDTO categoryWithTopicDTO) {
        List<Integer> categorySubscribersIds =
                categoryService.findUserIdsByCategoryIdExcludeCurrent(
                        categoryWithTopicDTO.getCategoryId(),
                        categoryWithTopicDTO.getSubmitterId());

        List<Integer> topicSubscribersIds =
                topicService.findUserIdsByTopicIdExcludeCurrent(
                        categoryWithTopicDTO.getTopicId(),
                        categoryWithTopicDTO.getSubmitterId());

        innerMessageService.saveMessagesForSubscribers(
                categoryWithTopicDTO,
                categorySubscribersIds, topicSubscribersIds);

        notificationMessagesService.sendMessagesToCategorySubscribers(
                categorySubscribersIds,
                categoryWithTopicDTO);
    }

    public void sendMessage(InnerMessage innerMessage) {
        innerMessageService.send(innerMessage);
    }

    public void sendMessage(InnerMessageDTO innerMessageDTO) {
        var innerMessage = InnerMessage.of()
                .id(innerMessageDTO.getId())
                .userId(innerMessageDTO.getUserId())
                .text(innerMessageDTO.getText())
                .created(innerMessageDTO.getCreated())
                .read(false)
                .interviewId(innerMessageDTO.getInterviewId())
                .build();
        sendMessage(innerMessage);
    }
}
