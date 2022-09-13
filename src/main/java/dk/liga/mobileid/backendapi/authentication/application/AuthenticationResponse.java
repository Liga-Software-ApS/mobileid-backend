package dk.liga.mobileid.backendapi.authentication.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema
@Data
@ToString
public
class AuthenticationResponse {
	String authenticationSessionId;

	public AuthenticationResponse(String authenticationSessionId) {
		this.authenticationSessionId = authenticationSessionId;
	}
}