package dk.liga.mobileid.backendapi.notification.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema
@Data
@ToString
public class NotificationConfirmationRequest {
    public String id;
    @Schema(name = "signedPayload",  required = true, type = "string", format = "binary")
    public byte[] signedPayload;
}