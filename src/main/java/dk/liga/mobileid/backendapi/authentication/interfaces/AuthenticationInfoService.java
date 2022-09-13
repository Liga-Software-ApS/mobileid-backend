package dk.liga.mobileid.backendapi.authentication.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.liga.mobileid.backendapi.authentication.domain.PendingAuthenticationService;

@Service

public class AuthenticationInfoService implements IAuthenticationInfoService {
    @Autowired
    private PendingAuthenticationService service;

    Logger logger = LoggerFactory.getLogger(PendingAuthenticationService.class);

    public void confirmAuthentication(String id, String payload) {
        try {
            service.put(id, payload);
        } catch (Exception e) {
            logger.info("Trying to confirm non existing session {}: {}", id, e.getMessage());
        }
        
    }
}
