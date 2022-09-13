package dk.liga.mobileid.backendapi.authentication.domain.exceptions;

public class PendingTokenAuthSessionException extends Exception { 
    public PendingTokenAuthSessionException(String errorMessage) {
        super(errorMessage);
    }
}