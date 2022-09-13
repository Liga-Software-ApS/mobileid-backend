package dk.liga.mobileid.backendapi.notification.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;


@Schema
@Data
@ToString
class NotificationOverview {
    public String target;
    public String nonce;

    public NotificationOverview(String target, String nonce) {
        this.target = target;
        this.nonce = nonce;
    }
}