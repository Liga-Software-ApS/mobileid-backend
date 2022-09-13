package dk.liga.mobileid.backendapi.authentication.domain.exceptions;

public class NoAuthSessionException extends Exception { 
    public NoAuthSessionException(String errorMessage) {
        super(errorMessage);
    }
}