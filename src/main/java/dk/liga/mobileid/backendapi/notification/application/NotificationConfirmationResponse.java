package dk.liga.mobileid.backendapi.notification.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema
@Data
@ToString
class NotificationConfirmationResponse {

    NotificationConfirmationResponse(String status) {
        this.status = status;
    }
    public String status;
}