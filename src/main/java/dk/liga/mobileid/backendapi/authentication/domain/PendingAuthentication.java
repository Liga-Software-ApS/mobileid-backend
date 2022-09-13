package dk.liga.mobileid.backendapi.authentication.domain;

import lombok.Getter;

public class PendingAuthentication {
    @Getter String id;
    @Getter String payload;

    PendingAuthentication(String id, String payload) {
        this.id = id;
        this.payload = payload;
    }
}
