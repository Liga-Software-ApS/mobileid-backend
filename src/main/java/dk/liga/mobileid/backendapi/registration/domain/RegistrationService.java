package dk.liga.mobileid.backendapi.registration.domain;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dk.liga.mobileid.backendapi.FirebaseService;
import dk.liga.mobileid.backendapi.TokenService;
import dk.liga.mobileid.backendapi.trust.TrustService;

@Service
public class RegistrationService implements IRegistrationService {

	@Autowired
	private RegistrationRepository repository;

	@Autowired
	private PendingRegistrationRepository pendingRepository;

	@Autowired
	private TrustService trustService;

	@Autowired
	private TokenService token;

	@Autowired
	private FirebaseService firebaseService;

	@Value("${certificate.verify:true}")
	private boolean verifyCertificate;

	@Value("${registration.clock-drift:300}")
	private long registrationClockDrift = 300;

	Logger logger = LoggerFactory.getLogger(RegistrationService.class);

	@Override
	public PendingRegistration start(byte[] certificate, long timestamp, byte[] signature) throws Exception {


		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			var is = new ByteArrayInputStream(certificate);
			var cert = (X509Certificate) cf.generateCertificate(is);

			// verifying the send certificate is valid in our given store
			if (verifyCertificate) {
				var verified = verifyCertificateChain(cert);
				if (!verified) {
					throw new CertificateException("Certificate could not be validated");
				}
			}

			// verifying the signed token was signed by the certificate
			// byte[] principal = "yes".getBytes(); //cert.getSubjectX500Principal().getName().getBytes();
			var currentTimestamp = Instant.now().getEpochSecond();
			var timeSinceSignature = currentTimestamp - timestamp;
			logger.info("StartRegistrationRequest: Time is {}, Signature from {} = drift {}/{}", currentTimestamp, timestamp, timeSinceSignature, registrationClockDrift);
			if (timeSinceSignature > registrationClockDrift) {
				throw new CertificateException("Clock drift since signature is to high: " + timeSinceSignature);
			}

			var principal = Long.toString(timestamp).getBytes();
			logger.info("StartRegistrationRequest: Timestamp Bytes: {}", principal);

			
			if (!verifySignature(cert.getPublicKey(), principal, signature)) {
				throw new CertificateException("Signature of principal is not correct");
			}



			var nonce = generateNonce();

			var pending = pendingRepository.save(new PendingRegistration(nonce, cert));

			return pending;
			

		} catch (Exception e) {
			throw e;

		} finally {

		}

	}

	static byte[] longToBytes(long l) {
		byte[] result = new byte[Long.BYTES];
		for (int i = Long.BYTES - 1; i >= 0; i--) {
			result[i] = (byte)(l & 0xFF);
			l >>= Byte.SIZE;
		}
		return result;
	}
	

	@Override
	public String complete(byte[] challenge, byte[] signedNonce, Optional<String> firebaseToken) throws Exception {



		try {
			var pending = pendingRepository.findById(challenge).orElseThrow();

			var isValid = pending.verifySignature(signedNonce);


			if (!isValid) {
				throw new Exception("Nonce signature not valid");
			}

			if (firebaseToken.isPresent() && !firebaseService.verifyToken(firebaseToken.get())) {
				throw new Exception("Firebase token is not valid");
			}

			var san = pending.getSAN();

			// register or update registration
			var registration = repository.findBySubject(san);
			if (registration.isPresent()) {
				logger.info("Updating registration for " + san);
				var reg = registration.get();
				reg.update(pending.getCertificate(), firebaseToken);

				repository.save(reg);
			} else {
				logger.info("Adding registration for " + san);
				var reg = new Registration(san, firebaseToken, pending.getCertificate());

				repository.save(reg);
			}

			return token.generateRegistrationToken(san);
		} finally {

		}
		
	}

	private boolean verifySignature(PublicKey pubKey, byte[] data, byte[] signature) {
		String algorithm = "SHA256withRSA";

		// Initialize JCE provider
		

		try {
			Signature verifier = Signature.getInstance(algorithm);
			verifier.initVerify(pubKey); // This one checks key usage in the cert
			verifier.update(data);
			return verifier.verify(signature);
		} catch (Exception e) {
			return false;
		}
		
	}

	private byte[] generateNonce() {
        var secureRandom = new SecureRandom();
        // to properly initialize it needs to be used once
        final byte[] ar = new byte[64];
        Arrays.fill(ar, (byte) 0);

        secureRandom.nextBytes(ar);
        
        return ar;
    }


	private boolean verifyCertificateChain(X509Certificate cert) {

		try {
			return trustService.verify(cert);
		} catch (Exception e) {
			return false;
		}

	}





}

class GeneralName {
	static int rfc822Name = 1;
}