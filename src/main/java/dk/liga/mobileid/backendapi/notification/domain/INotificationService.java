package dk.liga.mobileid.backendapi.notification.domain;

import java.util.List;
import java.util.Optional;

import dk.liga.mobileid.backendapi.authentication.domain.SignInNotification;
import dk.liga.mobileid.backendapi.notification.application.NotificationRejectionRequest;

public interface INotificationService {
    // void publish(SignInNotification entity);
    List<SignInNotification> getNotificationsFor(String subject);
    void reject(String username, NotificationRejectionRequest request) throws Exception ;
    void confirm(String subject, String nonce, byte[] signedPayload) throws Exception ;
    // void publish(String subject, SignInNotification notification);
    Optional<SignInNotification> getNotificationById(String nonce);
}