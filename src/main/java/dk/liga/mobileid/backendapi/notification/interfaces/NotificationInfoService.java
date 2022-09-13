package dk.liga.mobileid.backendapi.notification.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import dk.liga.mobileid.backendapi.FirebaseService;
import dk.liga.mobileid.backendapi.authentication.domain.SignInNotification;
import dk.liga.mobileid.backendapi.notification.domain.NotificationRepository;
import dk.liga.mobileid.backendapi.registration.interfaces.IRegistrationInfoService;

@Service
public class NotificationInfoService implements INotificationInfoService {

    @Autowired
    NotificationRepository repo;

    @Autowired
    FirebaseService firebaseService;

    @Autowired
    IRegistrationInfoService reg;

    Logger logger = LoggerFactory.getLogger(NotificationInfoService.class);

    @Value("${firebase.notification.title:MobileID Sign-in}")
    public String notificationTitle;

    @Value("${firebase.notification.body:Please approve a sign-in}")
    public String notificationBody;

    @Override
    public void publish(String subject, SignInNotification notification) {

        // save the notification
        var t = repo.save(notification);

        var firebaseToken = reg.findBySubject(subject).get().token;
        // notify client via Firebase
        Message message = Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(notificationTitle)
                                .setBody(notificationBody)
                                .build())
                .putData("type", "auth")
                .putData("id", t.getId())
                .setToken(firebaseToken)
                .build();

        try {
            // publish to firebase
            logger.debug("Sending auth message to " + firebaseToken);
            firebaseService.send(message);
        } catch (FirebaseMessagingException e) {
            logger.error(e.getMessage());
        }

    }

}
