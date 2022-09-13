package dk.liga.mobileid.backendapi.notification.application;

import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.liga.mobileid.backendapi.authentication.domain.SignInNotification;
import dk.liga.mobileid.backendapi.notification.domain.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

// APIs for the mobile device
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

	@Autowired
	private INotificationService service;

	Logger logger = LoggerFactory.getLogger(NotificationController.class);

	@Operation(summary = "List all notifications for the user", description = "none")
	// @ApiResponses(value = {
	// 		@ApiResponse(responseCode = "200", description = "List of all notifications", content = {
	// 				@Content(mediaType = "application/json") }),
	// 		@ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
	// })
	@GetMapping(value = "/")
	public NotificationListResponse listNotifications() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var subject = auth.getPrincipal().toString();

		logger.info("Get notifications for {}",subject);
			var list = service.getNotificationsFor(subject).stream();

			var body = new NotificationListResponse();
			body.notifications = list.collect(Collectors.toList());

			// return ResponseEntity.ok().body(body);
			return body;

	}

	@Operation(summary = "Retrieves a notification", description = "none")
	@PostMapping("/get")
	public Optional<SignInNotification> getNotification(@RequestBody NotificationRetrievalRequest request) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var subject = auth.getPrincipal().toString();

		logger.info("Get notification {} for {} ", request.id, subject);

		var nonce = service.getNotificationById(request.id);

		logger.info("Found {} ", nonce);
		return nonce;

	}

	@PutMapping("/")
	@Operation(summary = "Responds to a notification", description = "Confirms or denies the action contained in the notification")
	public NotificationConfirmationResponse putNotificationResponse(@RequestBody NotificationConfirmationRequest request) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var subject = auth.getPrincipal().toString();

		logger.info("Respond notification {} from {}", request.getId(), subject);

		try {
			service.confirm(subject, request.getId(), request.getSignedPayload());
			return new NotificationConfirmationResponse("good");
		} catch (Exception e) {
			logger.info(e.toString());
			logger.info("{} : {}", request.getId(), request.getSignedPayload());
		}
		return null;

		

	}

	// @PostMapping("/reject")
	// public String> rejectNotification(@RequestBody NotificationRejectionRequest
	// request) {
	// User user =
	// (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	// logger.error(Marker.ANY_MARKER, "Reject notifications for "+
	// user.getUsername());
	// try {
	// service.reject(user.getUsername(), request);
	// return ResponseEntity.status(HttpStatus.OK).body(null);
	// } catch (Exception e) {
	// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
	// }
	// }

}
