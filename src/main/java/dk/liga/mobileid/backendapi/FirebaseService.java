package dk.liga.mobileid.backendapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

@Service
public class FirebaseService {


    Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    public boolean verifyToken(String token) {

        Message message = Message.builder()
                .setToken(token)
                .build();
                try {
                    logger.info("Verifying FCM token");
                    String response = FirebaseMessaging.getInstance().send(message, true);
                    logger.info(response);
                    return true;
                } catch (FirebaseMessagingException e) {
                    logger.warn(e.toString());
                    return false;
                }
    }

    public void send(Message message) throws FirebaseMessagingException {

        String response = FirebaseMessaging.getInstance().send(message);
        // Response is a message ID string.
        System.out.println("Successfully sent message: " + response);
    }
}
