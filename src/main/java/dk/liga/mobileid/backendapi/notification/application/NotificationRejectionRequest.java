package dk.liga.mobileid.backendapi.notification.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema
@Data
@ToString
public class NotificationRejectionRequest {
    String nonce;
}