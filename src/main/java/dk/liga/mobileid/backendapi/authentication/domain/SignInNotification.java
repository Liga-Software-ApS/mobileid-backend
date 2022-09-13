package dk.liga.mobileid.backendapi.authentication.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import dk.liga.mobileid.backendapi.notification.domain.NotificationType;
import dk.liga.mobileid.backendapi.notification.interfaces.AbstractNotification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;


@Entity
@Schema(allOf = AbstractNotification.class)
@Data
@ToString
// @Table(name="notifications")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// @DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="signin")
public class SignInNotification extends AbstractNotification {
    public String authenticationId;


    public SignInNotification() {};


    public SignInNotification (String subject, String authenticationId, byte[] payload) {
        this.subject = subject;
        this.authenticationId = authenticationId;
        this.payload = payload;
    }




    @Override
    public byte[] getPayload() {
        return this.payload;
    }


    @Override
    public NotificationType getType() {
        return NotificationType.SIGNIN;
    }
}