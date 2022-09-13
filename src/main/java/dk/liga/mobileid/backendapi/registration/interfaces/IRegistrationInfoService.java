package dk.liga.mobileid.backendapi.registration.interfaces;

import java.util.Optional;

import dk.liga.mobileid.backendapi.registration.domain.Registration;

public interface IRegistrationInfoService {
    Optional<Registration> findBySubject(String subject);
}