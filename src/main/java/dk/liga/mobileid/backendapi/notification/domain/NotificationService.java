package dk.liga.mobileid.backendapi.notification.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import dk.liga.mobileid.backendapi.TokenService;
import dk.liga.mobileid.backendapi.authentication.domain.SignInNotification;
import dk.liga.mobileid.backendapi.authentication.interfaces.IAuthenticationInfoService;
import dk.liga.mobileid.backendapi.notification.application.NotificationRejectionRequest;
import dk.liga.mobileid.backendapi.registration.interfaces.IRegistrationInfoService;




@Service
public class NotificationService implements INotificationService {

    @Autowired
    private IAuthenticationInfoService auth;

    @Autowired
    private NotificationRepository repo;

    @Autowired
    private IRegistrationInfoService reg;

    @Autowired
    private TokenService tokenService;


    Logger logger = LoggerFactory.getLogger(NotificationService.class);


    public void confirm(String verifiedSubject, String id, byte[] signedPayload) throws Exception {
        logger.info("confirming...");
        var registration = reg.findBySubject(verifiedSubject).orElseThrow();
        var notification = repo.findById(id).orElseThrow();


        var isValid = registration.verifySignature(notification.getPayload(), signedPayload);

        if (!isValid) throw new Exception("Signature not valid - rejecting confirmation attempt");
        logger.info("response is valid...");

        var authenticationToken = tokenService.generateAuthenticationToken(verifiedSubject);
        logger.info("authToken is generated");

        

        try {
            switch (notification.getType()) {
                case SIGNIN:
                    logger.info("saving auth token to {}", notification.getAuthenticationId());
                    auth.confirmAuthentication(notification.getAuthenticationId(), authenticationToken);
                    break;
            
                default:
                    logger.warn("Notification {} confirmed with unknown type {}", notification.getId(), notification.getType());
                    break;
            }
        } catch (NoSuchElementException e) {
            logger.info("Erorr will processing");
        } finally {
            repo.deleteById(id);
        }

    }

    public void reject(String subject, NotificationRejectionRequest request) {
        repo.deleteById(request.getNonce());
    }


    public List<SignInNotification> getNotificationsFor(String subject) {
        LocalDateTime timeout = LocalDateTime.now().minusSeconds(60);
        // List<SignInNotification> notifications =  repo.findAllBySubjectAndCreatedAtGreaterThan(subject, timeout);
        List<SignInNotification> notifications =  repo.findAllBySubjectRegardless(subject).get();

        return notifications;
    }

    public Optional<SignInNotification> getNotificationById(String nonce) {
            return repo.findById(nonce);

    }



}
