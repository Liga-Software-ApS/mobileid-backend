package dk.liga.mobileid.backendapi.notification.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import dk.liga.mobileid.backendapi.authentication.domain.SignInNotification;


public interface NotificationRepository extends JpaRepository<SignInNotification, Long> {
    // check if we have the id

		// create authentication session.request

		public Optional<List<SignInNotification>> findAllBySubject(String subject);
        public List<SignInNotification> findAllBySubjectAndCreatedAtGreaterThan(String subject, LocalDateTime datetime);
        
        @Transactional
        public void deleteById(String id);

        @Transactional
        public void deleteByCreatedAtLessThan(LocalDateTime dateTime);


        @Transactional
        @Query("select u from SignInNotification u where u.id = ?1")
        public Optional<SignInNotification> findById(String id);

        @Transactional
        @Query("select u from SignInNotification u where u.subject = ?1")
        public Optional<List<SignInNotification>> findAllBySubjectRegardless(String subject);
}



