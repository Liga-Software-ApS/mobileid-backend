package dk.liga.mobileid.backendapi.authentication.interfaces;

public interface IAuthenticationInfoService {
    void confirmAuthentication(String id, String payload);
}
