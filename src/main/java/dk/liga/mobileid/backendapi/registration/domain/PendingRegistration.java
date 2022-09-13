package dk.liga.mobileid.backendapi.registration.domain;

import java.security.Signature;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;

import dk.liga.mobileid.backendapi.X509Utils;
import lombok.Getter;

public class PendingRegistration {
    // @Getter String id;
    @Getter byte[] challenge;
    @Getter X509Certificate certificate;

    PendingRegistration(byte[] challenge, X509Certificate cert) {
        this.challenge = challenge;
        this.certificate = cert;
    }

	public boolean verifySignature(byte[] signature) {
		String algorithm = "SHA256withRSA";

		try {
			Signature verifier = Signature.getInstance(algorithm);
			verifier.initVerify(certificate.getPublicKey()); // This one checks key usage in the cert
			verifier.update(challenge);
			return verifier.verify(signature);
		} catch (Exception e) {
			return false;
		}
	}

    public String getSAN() throws CertificateParsingException {
        // var sans = certificate.getSubjectAlternativeNames();

		var email = X509Utils.getSAN(certificate);
		var upn = X509Utils.getUPN(certificate);

		if (email != null) return email;
		if (upn != null) return upn;

		throw new CertificateParsingException();
    }
}
