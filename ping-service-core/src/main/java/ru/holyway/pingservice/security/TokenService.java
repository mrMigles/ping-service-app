package ru.holyway.pingservice.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenService {

  private LoadingCache<String, String> tokens = CacheBuilder.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .build(new CacheLoader<String, String>() {
        @Override
        public String load(String s) throws Exception {
          return UUID.randomUUID().toString().replaceAll("-", "");
        }
      });

  public String getToken(final String userName) {
    try {
      return tokens.get(userName);
    } catch (ExecutionException e) {
      log.warn(e.getMessage(), e);
      return "";
    }
  }

  public String getUserByToken(final String token) {
    Entry<String, String> tokenContainer = tokens.asMap().entrySet().stream()
        .filter(stringStringEntry -> stringStringEntry.getValue().equalsIgnoreCase(token))
        .findFirst().orElse(null);
    if (tokenContainer != null) {
      return tokenContainer.getKey();
    }
    return null;
  }

  public void invalidateToken(final String user) {
    tokens.invalidate(user);
  }
}
