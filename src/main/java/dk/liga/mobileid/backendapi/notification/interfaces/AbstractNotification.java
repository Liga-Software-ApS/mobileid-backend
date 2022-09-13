package dk.liga.mobileid.backendapi.notification.interfaces;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import dk.liga.mobileid.backendapi.authentication.domain.SignInNotification;
import dk.liga.mobileid.backendapi.notification.domain.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Entity
@Schema
@Data
@ToString
@Table(name = "notifications")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
    // @JsonSubTypes.Type(value = AbstractNotification.class, name = "abstract"),
        @JsonSubTypes.Type(value = SignInNotification.class, name = "signin"),
})
public abstract class AbstractNotification {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    protected String id;

    protected String subject;

    @Basic(optional = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // protected NotificationType type;

    @Schema(name = "payload",  required = true, type = "string", format = "binary")
    protected byte[] payload;

    public abstract byte[] getPayload();

    public abstract NotificationType getType();
}
