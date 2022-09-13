package dk.liga.mobileid.backendapi.notification.application;

import java.util.List;

import dk.liga.mobileid.backendapi.authentication.domain.SignInNotification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema
@Data
@ToString
class NotificationListResponse {
    List<SignInNotification> notifications;
}