package dk.liga.mobileid.backendapi.authentication.domain.exceptions;

public class NotRegisteredException extends Exception { 
    public NotRegisteredException(String errorMessage) {
        super(errorMessage);
    }
}