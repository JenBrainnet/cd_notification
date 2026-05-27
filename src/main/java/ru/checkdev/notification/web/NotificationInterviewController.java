package ru.checkdev.notification.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.dto.CancelInterviewNotificationDTO;
import ru.checkdev.notification.dto.InterviewNotifyDTO;
import ru.checkdev.notification.dto.WisherDismissedDTO;
import ru.checkdev.notification.dto.WisherNotifyDTO;
import ru.checkdev.notification.service.NotificationEventService;

import java.util.List;

/**
 * @author Dmitry Stepanov, user Dmitry
 * @since 17.11.2023
 */
@Tag(name = "NotificationInterviewController", description = "NotificationTopic REST API")
@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationInterviewController {
    private final NotificationEventService notificationEventService;

    /**
     * Метод обрабатывает пост запрос для рассылки уведомлений
     * подписчикам на тему.
     *
     * @param interviewNotifyDTO InterviewNotifyDTO
     * @return ResponseEntity<List < InnerMessage>>
     */
    @PostMapping("/topic/")
    public ResponseEntity<List<InnerMessage>> sendMessageSubscribeTopic(@RequestBody InterviewNotifyDTO interviewNotifyDTO) {
        return ResponseEntity.ok(notificationEventService.sendMessageSubscribeTopic(interviewNotifyDTO));
    }

    /**
     * Метод обрабатывает пост запрос для отправки уведомления автору собеседования,
     * о том что добавился участник собеседования.
     *
     * @param wisherNotifyDTO WisherNotifyDTO
     * @return ResponseEntity.
     */
    @PostMapping("/participate/")
    public ResponseEntity<InnerMessage> sendMessageSubmitterInterview(@RequestBody WisherNotifyDTO wisherNotifyDTO) {
        return ResponseEntity.ok(notificationEventService.sendMessageSubmitterInterview(wisherNotifyDTO));
    }

    /**
     * Метод обрабатывает пост запрос для отправки уведомления участнику собеседования,
     * о том что автор собеседования отменил его.
     *
     * @param cancelInterviewDTO CancelInterviewNotificationDTO
     * @return ResponseEntity.
     */
    @PostMapping("/cancelInterview/")
    public ResponseEntity<InnerMessage> sendMessageCancelInterview(@RequestBody CancelInterviewNotificationDTO cancelInterviewDTO) {
        return ResponseEntity.ok(notificationEventService.sendMessageCancelInterview(cancelInterviewDTO));
    }

    /**
     * Метод обрабатывает пост запрос для отправки уведомления участнику собеседования,
     * о том что автор собеседования одобрил другого участника.
     *
     * @param wisherDtoList List<WisherDto>
     * @return ResponseEntity.
     */
    @PostMapping("/participantIsDismissed/")
    public ResponseEntity<List<InnerMessage>> sendMessageCancelInterview(@RequestBody List<WisherDismissedDTO> wisherDtoList) {
        return ResponseEntity.ok(notificationEventService.sendMessageParticipantIsDismissed(wisherDtoList));
    }
}
