package dk.liga.mobileid.backendapi.registration.interfaces;

import java.security.cert.X509Certificate;

public class RegistrationInfo {
    String subject;
    X509Certificate cert;

    RegistrationInfo(String subject, X509Certificate cert) {
        this.subject = subject;
        this.cert = cert;
    }
}
