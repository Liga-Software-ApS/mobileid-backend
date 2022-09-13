package dk.liga.mobileid.backendapi.authentication.domain;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import dk.liga.mobileid.backendapi.authentication.domain.exceptions.NoAuthSessionException;
import dk.liga.mobileid.backendapi.authentication.domain.exceptions.PendingTokenAuthSessionException;
import dk.liga.mobileid.backendapi.config.CacheConfig;

@Repository
public class PendingAuthenticationRepository {

	@Autowired
    private CacheManager cacheManager;

    Logger logger = LoggerFactory.getLogger(PendingAuthenticationRepository.class);


    Cache getCache() {
        return cacheManager.getCache(CacheConfig.AUTHENTICATION_CACHE_NAME);
    }


	public PendingAuthentication save(PendingAuthentication p) {
        return save(p.getId(), p.getPayload());
	}

    public PendingAuthentication save(String id, String payload) {
        var cache = cacheManager.getCache(CacheConfig.AUTHENTICATION_CACHE_NAME);

        logger.info("Saving authentication for id: {}", id);
        cache.put(id, payload);

        return new PendingAuthentication(id, payload);
	}

    public Optional<PendingAuthentication> findById(String id) throws PendingTokenAuthSessionException, NoAuthSessionException {
        logger.info("Loading authentication for id: {}", id);

        var entry = getCache().get(id);
        if (entry == null) { // cache key nonce not found
            throw new NoAuthSessionException("No auth session");
        }
        var payload = (String)entry.get();
        if (payload == null) {
            throw new PendingTokenAuthSessionException("Waiting for token");
        }

        return Optional.ofNullable(new PendingAuthentication(id, payload));
    }


}
