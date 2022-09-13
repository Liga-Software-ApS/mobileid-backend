package dk.liga.mobileid.backendapi.registration.domain.exceptions;

public class InvalidCertificateException extends Exception { 
    public InvalidCertificateException(String errorMessage) {
        super(errorMessage);
    }
}