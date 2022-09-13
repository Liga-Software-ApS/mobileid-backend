package dk.liga.mobileid.backendapi.authentication;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;


class MyUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public MyUsernamePasswordAuthenticationToken(Object principal, Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        super.setAuthenticated(true);
    }



}


@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        logger.info("Authenticating");
        
        String name = authentication.getName();
        logger.info("Name:  {}", name);
        String password = (String)authentication.getCredentials();
        logger.info("Details:  {} ", authentication.getDetails());
        logger.info("Principal:  {} ", authentication.getPrincipal());
        logger.info("Credentials:  {} ", password);


        
        try {
            var token = decodeAndVerify(password, name);
            var retauthentication = new UsernamePasswordAuthenticationToken(token.getSubject(), password, new ArrayList<>());
            // authentication.setAuthenticated(true);
            return retauthentication;
        } catch (Exception e) {
            
            logger.info(e.toString());
        }


        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    @Value("${authentication.token.secret}")
	private String tokenSecret;

    DecodedJWT decodeAndVerify(String token, String subject) {

		Algorithm algorithm = Algorithm.HMAC256(tokenSecret); // use more secure key
		JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer("mobileid")
                .withSubject(subject)
				.build(); // Reusable verifier instance
		DecodedJWT jwt = verifier.verify(token);

		return jwt;

	}
    
}