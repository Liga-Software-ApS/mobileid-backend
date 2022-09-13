package dk.liga.mobileid.backendapi.registration.domain;

import java.util.Optional;


import org.springframework.data.repository.CrudRepository;

public interface RegistrationRepository extends CrudRepository<Registration, Long> {
    // check if we have the id

		// create authentication session.request

		public Optional<Registration> findBySubject(String subject); 
}
