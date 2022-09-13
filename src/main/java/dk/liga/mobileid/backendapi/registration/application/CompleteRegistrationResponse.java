package dk.liga.mobileid.backendapi.registration.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema
@Data
@ToString
class CompleteRegistrationResponse {
	String idToken;

    CompleteRegistrationResponse(String idToken) {
        this.idToken = idToken;
    }
}