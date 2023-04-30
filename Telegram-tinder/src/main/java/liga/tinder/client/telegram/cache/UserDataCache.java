package liga.tinder.client.telegram.cache;


import liga.tinder.client.domain.User;
import liga.tinder.client.telegram.botapi.BotState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Отвечает за хранение состояния каждого пользователя по chatId
 * во время его работы с ботом, а так же для кэша самих пользователей
 */
@Component
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private final Map<Long, User> usersProfileData = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(long userId) {
        return usersBotStates.get(userId);
    }

    @Override
    public User getUserProfileData(long userId) {
        User user = usersProfileData.getOrDefault(userId, new User(userId));
        usersProfileData.put(userId, user);
        return user;
    }
}