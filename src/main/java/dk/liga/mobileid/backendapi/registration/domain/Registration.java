package dk.liga.mobileid.backendapi.registration.domain;

import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message.Builder;

import dk.liga.mobileid.backendapi.FirebaseService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@Schema
public class Registration {

	Registration(String subject, Optional<String> token, X509Certificate certificate) {
		this.subject = subject;
		this.token = token.get();
		this.certificate = certificate;
	}

    public Registration() {}

    @Id
	@GeneratedValue
    private long id;
    String subject;


	public String token;
	public Optional<String> getToken() {
        return Optional.ofNullable(token);
    }
	// String certificate;

	@Type(type = "dk.liga.mobileid.backendapi.hibernate.X509Type")
	X509Certificate certificate;



	public void update(X509Certificate cert, Optional<String> token) {
		this.certificate = cert;
		this.token = token.get();
	}

	public void buildAndSendFirebase(FirebaseService firebase, Builder messageBuilder) throws FirebaseMessagingException {
		if (token.isEmpty()) return;

		var message = messageBuilder.setToken(getToken().get()).build();
		firebase.send(message);

	}

	public boolean verifySignature(byte[] data, byte[] signature) {
		String algorithm = "SHA256withRSA";

		try {
			Signature verifier = Signature.getInstance(algorithm);
			verifier.initVerify(certificate.getPublicKey()); // This one checks key usage in the cert
			verifier.update(data);
			return verifier.verify(signature);
		} catch (Exception e) {
			return false;
		}
	}

}