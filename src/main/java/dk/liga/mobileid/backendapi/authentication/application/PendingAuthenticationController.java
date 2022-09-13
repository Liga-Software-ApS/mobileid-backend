package dk.liga.mobileid.backendapi.authentication.application;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.liga.mobileid.backendapi.authentication.domain.PendingAuthenticationService;
import dk.liga.mobileid.backendapi.authentication.domain.exceptions.NoAuthSessionException;
import dk.liga.mobileid.backendapi.authentication.domain.exceptions.PendingTokenAuthSessionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.ToString;

@Schema
@Data
@ToString
class CheckRequest {
	String nonce;
}



@RestController
@Tag(name = "Authentication")
@RequestMapping("/api/authentication")
public class PendingAuthenticationController {

	@Autowired PendingAuthenticationService service;

	Logger logger = LoggerFactory.getLogger(PendingAuthenticationController.class);
    
	@PostMapping("/")
	@Operation(summary = "Start authentication flow for subject", description = "none")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		try {

			var pending = service.authenticate(request.subject);
			var body = new AuthenticationResponse(pending.getId());

			return ResponseEntity.status(HttpStatus.OK).body(body);
		} catch (FileNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			logger.error(e.toString());
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
		}
	}

	@PostMapping("/get")
	@Operation(summary = "Checks if the authenticating app has published a response yet", description = "none")
	public ResponseEntity<CheckAuthenticationResponse> check( @RequestBody CheckAuthenticationRequest request) {
		
		try {
			var pending = service.check(request.authenticationSessionId);
			var body = new CheckAuthenticationResponse(pending.getPayload());
			logger.info("CheckAuthResposne: {}", body);
			return ResponseEntity.status(HttpStatus.OK).body(body);
		} catch (PendingTokenAuthSessionException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} catch (NoAuthSessionException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			logger.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

		}

	}

    // @PostMapping("/confirm/{session}")
	// public ResponseEntity<String> confirm(@RequestBody ConfirmationRequest request) {
	// 	try {
	// 		service.confirm(request.nonce, request.confirmationToken);
	// 		if (service != null) throw new FileNotFoundException();
	// 		return ResponseEntity.status(HttpStatus.OK).body(null);
	// 	} catch (FileNotFoundException e) {
	// 		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	// 	} catch (Exception e) {
	// 		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
	// 	}
	// }

    
}
