package dk.liga.mobileid.backendapi.notification.interfaces;

import dk.liga.mobileid.backendapi.authentication.domain.SignInNotification;

public interface INotificationInfoService {
    void publish(String subject, SignInNotification notification);
}
