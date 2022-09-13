package dk.liga.mobileid.backendapi.notification.infrastructure;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Component
@SecurityScheme(
  name = "bearerAuth",
  type = SecuritySchemeType.HTTP,
  bearerFormat = "JWT",
  scheme = "bearer"
)

public class JwtRequestFilter extends OncePerRequestFilter {

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getServletPath();

		boolean isFiltered = path.startsWith("/api/notifications/");

		return !isFiltered;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");


		try {
			var jwt = decodeAndVerify(requestTokenHeader.substring(7));
			var subject = jwt.getSubject();

			// var user = new User(subject, jwt.getSignature(), new ArrayList<>());

			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					subject, jwt.getSignature(), new ArrayList<>());
			usernamePasswordAuthenticationToken
					.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			// After setting the Authentication in the context, we specify
			// that the current user is authenticated. So it passes the
			// Spring Security Configurations successfully.
			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			logger.warn("all set");
		} catch (Exception e) {
			logger.warn("doFilterInternal " + e.toString());
		}

		chain.doFilter(request, response);
	}

	@Value("${registration.token.secret}")
	private String tokenSecret;

	DecodedJWT decodeAndVerify(String token) {

		Algorithm algorithm = Algorithm.HMAC256(tokenSecret); // use more secure key
		JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer("mobileid")
				.build(); // Reusable verifier instance
		DecodedJWT jwt = verifier.verify(token);

		return jwt;

	}

}