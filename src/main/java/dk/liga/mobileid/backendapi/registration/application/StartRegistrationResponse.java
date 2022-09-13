package dk.liga.mobileid.backendapi.registration.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema
@Data
@ToString
class StartRegistrationResponse {
    @Schema(name = "challenge",  required = true, type = "string", format = "binary")
	byte[] challenge;

    StartRegistrationResponse(byte[] challenge) {
        this.challenge = challenge;
    }
}