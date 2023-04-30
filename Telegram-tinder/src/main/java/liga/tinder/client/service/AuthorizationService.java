package liga.tinder.client.service;

import liga.tinder.client.domain.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthorizationService {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";

    public HttpEntity<Void> getAuthorizationHeader(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, TOKEN_PREFIX + user.getToken());
        return new HttpEntity<>(headers);
    }
}
