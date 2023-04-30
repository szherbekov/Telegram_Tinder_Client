package liga.tinder.client.telegram.cache;


import liga.tinder.client.domain.User;
import liga.tinder.client.telegram.botapi.BotState;

public interface DataCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    BotState getUsersCurrentBotState(long userId);

    User getUserProfileData(long userId);


}
