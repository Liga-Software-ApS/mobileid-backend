package dk.liga.mobileid.backendapi.registration.interfaces;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.liga.mobileid.backendapi.registration.domain.Registration;
import dk.liga.mobileid.backendapi.registration.domain.RegistrationRepository;

@Service
public class RegistrationInfoService implements IRegistrationInfoService {

    @Autowired
    RegistrationRepository repo;

    @Override
    public Optional<Registration> findBySubject(String subject) {
        return repo.findBySubject(subject);
    }
    
}
