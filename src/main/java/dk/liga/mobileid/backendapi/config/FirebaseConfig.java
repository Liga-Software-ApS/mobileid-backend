package dk.liga.mobileid.backendapi.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {
    Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.fcm.service-account-key}")
		public String serviceAccountKeyPath;

    @PostConstruct
	public void init() {

		/**
		 * https://firebase.google.com/docs/server/setup
		 * 
		 * Create service account , download json
		 */
        FirebaseOptions options;
        try {
            logger.info("firebase initializing...");
            options = FirebaseOptions.builder()
                    .setCredentials(getCredentials())
                    .build();
            FirebaseApp.initializeApp(options);
            logger.info("firebase initialized");
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
		
	}

    private GoogleCredentials getCredentials() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(serviceAccountKeyPath))
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredentials.refreshAccessToken();
        return googleCredentials;
    }
}
