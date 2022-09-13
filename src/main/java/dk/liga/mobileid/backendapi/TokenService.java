package dk.liga.mobileid.backendapi;

import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class TokenService {

    @Value("${registration.token.secret}")
    private String registrationTokenSecret;

	@Value("${registration.token.validity}")
    private int tokenValidity;

    @Value("${authentication.token.secret}")
    private String authenticationTokenSecret;

    
    public String generateRegistrationToken(String subject) {
        Algorithm algorithm = Algorithm.HMAC256(registrationTokenSecret);

        var expirationDate = Date.from(ZonedDateTime.now().plusSeconds(tokenValidity).toInstant());

        String token = JWT.create()
                .withIssuer("mobileid")
                .withExpiresAt(expirationDate)
                .withSubject(subject)
                .sign(algorithm);

        return token;

    }

    public String generateAuthenticationToken(String subject) {
        Algorithm algorithm = Algorithm.HMAC256(authenticationTokenSecret);

        var expirationDate = Date.from(ZonedDateTime.now().plusSeconds(tokenValidity).toInstant());

        String token = JWT.create()
                .withIssuer("mobileid")
                .withExpiresAt(expirationDate)
                .withSubject(subject)
                .sign(algorithm);

        return token;
        
    }
}
