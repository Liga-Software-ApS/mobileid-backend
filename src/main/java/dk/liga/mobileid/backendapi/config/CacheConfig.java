package dk.liga.mobileid.backendapi.config;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    public final static String AUTHENTICATION_CACHE_NAME = "auth";
    public final static String REGISTRATION_CACHE_NAME = "reg";
    public final static String NOTIFICATION_CACHE_NAME = "not";
    
    @Bean
    @Override
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {

            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS)
                        .maximumSize(100).build().asMap(), true);
            }
        };

        cacheManager.setCacheNames(Arrays.asList(AUTHENTICATION_CACHE_NAME, REGISTRATION_CACHE_NAME, NOTIFICATION_CACHE_NAME));
        return cacheManager;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }
}

@Component("myCacheKeyGenerator")
class CacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb = new StringBuilder();

        if (target != null) {
            sb.append(target.getClass().getSimpleName()).append("-");
        }

        if (method != null) {
            sb.append(method.getName());
        }

        if (params != null) {
            for (Object param : params) {
                sb.append("-").append(param.getClass().getSimpleName()).append(":").append(param);
            }
        }
        return sb.toString();
    }

}