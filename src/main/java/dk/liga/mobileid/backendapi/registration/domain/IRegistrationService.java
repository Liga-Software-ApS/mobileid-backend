package dk.liga.mobileid.backendapi.registration.domain;

import java.util.Optional;

public interface IRegistrationService {
	PendingRegistration start(byte[] cert, long timestamp, byte[] signature) throws Exception;
	String complete(byte[] challenge, byte[] signedNonce, Optional<String> firebaseToken) throws Exception;
}