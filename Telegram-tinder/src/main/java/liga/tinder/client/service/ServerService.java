package liga.tinder.client.service;

import liga.tinder.client.domain.AuthenticUser;
import liga.tinder.client.domain.Profile;
import liga.tinder.client.domain.Token;
import liga.tinder.client.domain.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Клас отвечающий за связь с сервером.
 */
@Service
@RequiredArgsConstructor
public class ServerService {

    private static final Logger log = LoggerFactory.getLogger(ServerService.class);

    private final RestTemplate restTemplate;
    private final AuthorizationService authorizationService;
    private final PrerevolutionaryTranslator translator;

    @Value("${key.localPath}")
    private String localPath;

    static final String URL_USERS = "users/search";/*Поиск по анкетам пользователей со страницами и размером страницы GET*/
    static final String URL_LIKE = "users/like/id"; /*Лайкнуть другого пользователя POST*/
    static final String URL_UNLIKE = "users/unlike/id"; /*Убрать лайк с анкеты пользователя DELETE*/
    static final String URL_REGISTRATION = "/users/create";/*Создать новую анкету текущего пользователя POST*/
    static final String URL_LOGIN = "login";
    static final String URL_CURRENT_USER = "users/view"; /*Получить анкету текущего пользователя GET*/
    static final String URL_IS_REGISTERED = "users/exists/id";
    static final String URL_LOWERS = "users/likers";/*Получить всех пользователей, кого текущий пользователь лайкнул GET*/
    static final String URL_CAPTION = "users/imgdescr/id";/*Получение описания к картинке профиля для пользователя id GET*/
    static final String URL_LOVE = "users/islove/id";/*Получить список пользователей, с кем взаимные лайки GET*/
    static final String URL_CHANGE_CURRENT = "/users/update/id";/*редактирует анкету текущего пользователя PUT*/
//    static final String URL_OF_ANOTHER_USER = "/users/view/id";/*Получить анкету другого пользователя GET*/

//    static final String URL_USERS_LIKE = "/search?size=\"\"&page=\"\"&withSympathy=true";/*Получить список пользователей, кто лайкнул текущего пользователя*/

//Создать новую анкету текущего пользователя
//POST /users/create
//
//Редактировать анкету текущего пользователя
//PUT /users/update/{id}
//
//Получить анкету текущего пользователя
//GET /users/view
//(анкета придет в виде картинки)
//
//Получить анкету другого пользователя
//GET /users/view/{id}
//
//Лайкнуть другого пользователя
//POST users/like/{id}
//
//Убрать лайк с анкеты пользователя
//DELETE users/like/{id}
//
//Поиск по анкетам пользователей со страницами и размером страницы
//GET users/search

    public List<Profile> getValidProfilesToUser(User user) {
        ResponseEntity<Profile[]> usersResponse = restTemplate.exchange(localPath + URL_USERS, HttpMethod.GET, authorizationService.getAuthorizationHeader(user), Profile[].class);
        log.info("Получение {} анкет в поиске для текущего пользователя", usersResponse.getBody().length);
        return List.of(usersResponse.getBody());
    }

    public void updateCurrentUser(Profile profile, User user) {
        HttpEntity<Void> authEntity = authorizationService.getAuthorizationHeader(user);
        HttpEntity<Profile> entity = new HttpEntity<>(profile, authEntity.getHeaders());
        log.info("Обновление текущего пользователя с id = {}", profile.getUserId());
        restTemplate.put(localPath + URL_CHANGE_CURRENT, entity, Void.class);
    }

    public void unLikeProfile(Long profileId, User user) {
        restTemplate.delete(String.format(localPath + URL_UNLIKE, profileId), authorizationService.getAuthorizationHeader(user), profileId, Long.class);
        log.info("Текущий пользователь id = {} ставит лайк пользователю id ={}", user.getProfile().getUserId(), profileId);
    }

    public boolean isRegistered(Long userId) {
        log.info("Регистрации пользователя с id = {}", userId);
        return restTemplate.getForObject(String.format(localPath + URL_IS_REGISTERED, userId), Boolean.class);
    }

    public void registerUser(Profile profile) {
        log.info("Регистрации пользователя с id = {}", profile.getUserId());
        restTemplate.postForObject(localPath + URL_REGISTRATION, profile, Profile.class);
    }

    public String loginUser(AuthenticUser authenticUser) {
        HttpEntity<AuthenticUser> entity = new HttpEntity<>(authenticUser);
        try {
            Token token = restTemplate.postForObject(localPath + URL_LOGIN, entity, Token.class);
            log.info("Авторизация пользователя = {}", authenticUser.getUsername());
            return token.getToken();
        } catch (HttpClientErrorException e) {
            log.error("Ошибка авторизации " + e);
            return "";
        }
    }

    public Profile getLoginUserProfile(User user) {
        log.info("Запрос текущего пользователя id = {}", user.getProfile().getUserId());
        HttpHeaders headers = authorizationService.getAuthorizationHeader(user).getHeaders();
        return restTemplate.exchange(localPath + URL_CURRENT_USER, HttpMethod.GET, new HttpEntity<>(headers), Profile.class).getBody();
    }

    public void likeProfile(Long profileId, User user) {
        restTemplate.exchange(String.format(localPath + URL_LIKE, profileId), HttpMethod.POST, authorizationService.getAuthorizationHeader(user), Long.class);
        log.info("Текущий пользователь id = {} ставит лайк пользователю id ={}", user.getProfile().getUserId(), profileId);
    }

    public List<Profile> getLowersProfilesToUser(User user) {
        ResponseEntity<Profile[]> usersResponse = restTemplate.exchange(localPath + URL_LOWERS, HttpMethod.GET, authorizationService.getAuthorizationHeader(user), Profile[].class);
        log.info("Получение всех анкет, у которых есть отношения с текущим пользователем id = {} в количестве = {}", user.getProfile().getUserId(), usersResponse.getBody().length);
        return List.of(usersResponse.getBody());
    }

    public String getCaption(Long userId, User user) {
        ResponseEntity<String> caption = restTemplate.exchange(String.format(localPath + URL_CAPTION, userId), HttpMethod.GET, authorizationService.getAuthorizationHeader(user), String.class);
        log.info("Получение описания к картинке профиля для пользователя id = {}", userId);
        return translator.translate(caption.getBody());
    }

    public boolean weLove(Long userId, User user) {
        ResponseEntity<Boolean> love = restTemplate.exchange(String.format(localPath + URL_LOVE, userId), HttpMethod.GET, authorizationService.getAuthorizationHeader(user), boolean.class);
        log.info("Получение popUp сообщения для пользователя id = {}", userId);
        return love.getBody();
    }
}


