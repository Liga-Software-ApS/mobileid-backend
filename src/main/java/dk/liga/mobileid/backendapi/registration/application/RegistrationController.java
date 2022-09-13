package dk.liga.mobileid.backendapi.registration.application;


import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.liga.mobileid.backendapi.registration.domain.IRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

enum DevicePlatform {
	ANDROID,
	IOS
}

@RestController
@RequestMapping("/api/registration")
@Tag(name = "Registration")
public class RegistrationController {

	@Autowired
	private IRegistrationService service;

	Logger logger = LoggerFactory.getLogger(RegistrationController.class);


	@PostMapping("/")
	@Operation(summary = "Starts registration procedure", description = "none")
	public ResponseEntity<StartRegistrationResponse> startRegister(@RequestBody StartRegistrationRequest request) throws Exception {

		try {
			var pending = service.start(request.certificate, request.timestamp, request.signature);
			var body = new StartRegistrationResponse(pending.getChallenge());
			return ResponseEntity.status(HttpStatus.OK).body(body);
		} catch (CertificateException e) {
			logger.info(e.toString());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		} 
		catch (Exception e) {
			logger.info(e.toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		

		
	}

	@PostMapping("/complete")
	@Operation(summary = "Completes registration with challenge", description = "none")
	public ResponseEntity<CompleteRegistrationResponse> completeRegister(@RequestBody CompleteRegistrationRequest request) throws Exception {

		try {
			var token = service.complete(request.challenge, request.signedNonce, request.firebaseToken);
			var body = new CompleteRegistrationResponse(token);
			return ResponseEntity.status(HttpStatus.OK).body(body);
		} catch (CertificateException e) {
			logger.error(e.toString());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		//  catch (Exception e) {
		// 	logger.error(e.toString());
		// 	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		// }
		

	}
}
