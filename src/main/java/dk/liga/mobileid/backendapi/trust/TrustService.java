package dk.liga.mobileid.backendapi.trust;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dk.liga.mobileid.backendapi.X509Utils;

@Service 
public class TrustService {
	@Value("${certificate.truststore.enabled}")
	private boolean usingTruststore;

	@Value("${certificate.truststore.path}")
	private String trustStorePath;

	@Value("${certificate.truststore.password}")
	private String trustStorePassword;

	KeyStore trustStore;

	Logger logger = LoggerFactory.getLogger(TrustService.class);

	public TrustService() {
		
	}

	private void loadKeystore() {
		if (trustStore == null) {
			try {
				trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(new FileInputStream(trustStorePath), trustStorePassword.toCharArray());
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}

	public boolean verify(X509Certificate cert) {
		loadKeystore();

		try {
			var trustAnchors = X509Utils.keyStoreToTrustAnchors(trustStore);
			for (TrustAnchor t : trustAnchors) {
				try {
					var c= t.getTrustedCert();
					cert.verify(c.getPublicKey());
					return true;
				} catch (Exception e) {
					// ignore
				}
			}

			return false;
		} catch (Exception e) {
			return false;
		}
		
		
	}
}