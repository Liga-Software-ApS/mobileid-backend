package dk.liga.mobileid.backendapi.registration.application;

import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema
@Data
@ToString
class CompleteRegistrationRequest {
	@Schema(name = "challenge",  required = true, type = "string", format = "binary")
	byte[] challenge;
	@Schema(name = "signedNonce",  required = true, type = "string", format = "binary")
	byte[] signedNonce;
	Optional<String> firebaseToken;
}