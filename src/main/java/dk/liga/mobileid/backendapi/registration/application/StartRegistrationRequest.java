package dk.liga.mobileid.backendapi.registration.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema
@Data
@ToString
class StartRegistrationRequest {
	@Schema(name = "certificate",  required = true, type = "string", format = "binary")
	byte[] certificate;
	@Schema(name = "timestamp",  required = true)
	long timestamp;
	@Schema(name = "signature",  required = true, type = "string", format = "binary")
	byte[] signature;
}