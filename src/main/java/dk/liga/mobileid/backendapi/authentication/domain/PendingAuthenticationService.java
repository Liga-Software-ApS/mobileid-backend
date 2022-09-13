package dk.liga.mobileid.backendapi.authentication.domain;

import java.security.SecureRandom;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import dk.liga.mobileid.backendapi.authentication.domain.exceptions.NoAuthSessionException;
import dk.liga.mobileid.backendapi.authentication.domain.exceptions.NotRegisteredException;
import dk.liga.mobileid.backendapi.authentication.domain.exceptions.PendingTokenAuthSessionException;
import dk.liga.mobileid.backendapi.notification.interfaces.INotificationInfoService;
import dk.liga.mobileid.backendapi.registration.interfaces.IRegistrationInfoService;



@Service
public class PendingAuthenticationService {

    @Autowired
    private IRegistrationInfoService registrationInfo;

    @Autowired
    private INotificationInfoService notificationInfo;

    @Autowired
    private PendingAuthenticationRepository pendingAuthRepo;




    Logger logger = LoggerFactory.getLogger(PendingAuthenticationService.class);

    @Value("${challange.secret}")
    private String secret;

    public PendingAuthentication authenticate(String subject) throws Exception {
        logger.info("Logging in " + subject);
        var registration = registrationInfo.findBySubject(subject);

        if (registration.isEmpty())
            throw new NotRegisteredException("No device registered");



        var nonce = generateNonce();

        


        var pending = pendingAuthRepo.save(new PendingAuthentication(Base64Utils.encodeToString(nonce), null));

        var signInNotification = new SignInNotification(
            subject,
            pending.getId(),
            nonce
        );

        notificationInfo.publish(subject, signInNotification);

        return pending;
    }



    private byte[] generateNonce() {
        var secureRandom = new SecureRandom();
        // to properly initialize it needs to be used once
        final byte[] ar = new byte[64];
        Arrays.fill(ar, (byte) 0);

        secureRandom.nextBytes(ar);
        
        return ar;
    }

    public PendingAuthentication put(String id, String payload) throws PendingTokenAuthSessionException, NoAuthSessionException {
        return pendingAuthRepo.save(id, payload);
    }

    public PendingAuthentication check(String nonce) throws PendingTokenAuthSessionException, NoAuthSessionException  {
        var result = pendingAuthRepo.findById(nonce).orElseThrow();

        logger.info("Found {} with {}", result.getId(), result.getPayload());

        return result;
    }

}
