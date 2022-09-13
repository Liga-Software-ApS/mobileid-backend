package dk.liga.mobileid.backendapi.registration.domain;

import java.security.cert.X509Certificate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.Base64Utils;

@Repository
public class PendingRegistrationRepository {
    
    @Autowired
    private CacheManager cacheManager;
    


    Logger logger = LoggerFactory.getLogger(PendingRegistrationRepository.class);


    PendingRegistrationRepository() { }

    Cache getCache() {
        return cacheManager.getCache("reg");
    }

    String generateCacheKey(byte[] id) {
        return Base64Utils.encodeToString(id);
    }

    PendingRegistration save(PendingRegistration p) {
        var cacheKey = generateCacheKey(p.getChallenge());
        getCache().put(cacheKey, p.getCertificate());

        return p;
    }
    Optional<PendingRegistration> findById(String id) {
        return findById(id.getBytes());
    }

    Optional<PendingRegistration> findById(byte[] id) {
        var cacheKey = generateCacheKey(id);
        var cachedEntry = getCache().get(cacheKey);
		if (cachedEntry == null) return Optional.empty();
		var cert = (X509Certificate)cachedEntry.get();

        return Optional.ofNullable(new PendingRegistration(id, cert));
    }


    // void createPendingRegistration(String nonce, X509Certificate cert) {
    //     logger.debug("{} {}", "Saving", nonce);
    //     getCache().put(nonce, cert);
    // }

    // X509Certificate confirmPendingRegistration(String nonce) throws Exception {
    //     logger.debug("{} {}", "Retrieving", nonce);	
    // }
}
