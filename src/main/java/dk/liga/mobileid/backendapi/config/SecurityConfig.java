package dk.liga.mobileid.backendapi.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import dk.liga.mobileid.backendapi.authentication.CustomAuthenticationProvider;
import dk.liga.mobileid.backendapi.notification.infrastructure.JwtRequestFilter;

@Configuration
public class SecurityConfig {

	@Autowired
	JwtRequestFilter jwtRequestFilter;

	@Autowired
	CustomAuthenticationProvider customAuthProvider;

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
			throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);



		http
			.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt) 
				.exceptionHandling((exceptions) -> exceptions
						.authenticationEntryPoint(
								new LoginUrlAuthenticationEntryPoint("/login")));

		return http.build();
	}

	@Bean 
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
			throws Exception {

				AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class); 


		http
				.authenticationManager(authenticationManagerBuilder.eraseCredentials(false).build())
				.authenticationProvider(customAuthProvider)
				.cors().and()
				.csrf().disable()
				// .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
				// .addFilterBefore(jwtRequestFilter, AnonymousAuthenticationFilter.class)
				.authorizeHttpRequests((authorize) -> authorize
						.antMatchers("/api/registration/**", "/api/authentication/**").permitAll()
						.antMatchers("/api/notifications/**").permitAll() // authenticated using JWT filter
						.antMatchers("/static/**").permitAll()
						.antMatchers("/userinfo").permitAll()
						.antMatchers("/test123").authenticated()
						// .anyRequest().authenticated()
				)
				// .oauth2Login();
				// Form login handles the redirect to the login page from the
				// authorization server filter chain
				// .formLogin(Customizer.withDefaults());
				.formLogin(form -> form
						.loginPage("/login")
						.permitAll());

		return http.build();
	}


	// @Bean
	// public UserDetailsService userDetailsService() {
	// 	UserDetails userDetails = User.withDefaultPasswordEncoder()
	// 			.username("aaa")
	// 			.password("sss")
	// 			.roles("USER")
	// 			.build();

	// 	return new InMemoryUserDetailsManager(userDetails);
	// }

	@Value("${serviceprovider.redirect}")
	private String serviceProviderRedirecturi;

	@Value("${serviceprovider.private-redirect}")
	private String serviceProviderPrivateRedirecturi;

	@Value("${serviceprovider.idp-redirect}")
	private String serviceProviderIdpRedirecturi;

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient publicClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("mobileid-sp")
				.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri(serviceProviderRedirecturi)
				.scope(OidcScopes.OPENID)
				.build();

				Set<String> scopes = new HashSet<String>();
				scopes.add(OidcScopes.OPENID);
				scopes.add(OidcScopes.EMAIL);
				scopes.add(OidcScopes.PHONE);
				scopes.add(OidcScopes.PROFILE);
				scopes.add("service:asdf1234");

		RegisteredClient privateClient = RegisteredClient.withId(UUID.randomUUID().toString())
			.clientId("am")
			.clientSecret("asdf1234")
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
			.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri(serviceProviderPrivateRedirecturi)
				.scope("openid")
				.scope("profile")
				.scope("email")
				.scope("phone")
				.scope("service:asdf1234")
				.build();

			RegisteredClient simpleSamlIdp = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("simplesaml")
				.clientSecret("{noop}Asdf1234!")
				.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
					.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
					.redirectUri(serviceProviderIdpRedirecturi)
					.scope("openid")
					.build();

		return new InMemoryRegisteredClientRepository(publicClient, privateClient, simpleSamlIdp);
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	private static KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

}
