package liga.tinder.client.telegram.handler;

import liga.tinder.client.domain.User;
import liga.tinder.client.service.BotMethodService;
import liga.tinder.client.service.ImageService;
import liga.tinder.client.service.KeyboardService;
import liga.tinder.client.service.TextMessagesService;
import liga.tinder.client.telegram.botapi.BotState;
import liga.tinder.client.telegram.cache.DataCache;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class ProfileHandler implements InputMessageHandler{

    private final DataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;
    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE);
        if (message.getText().equals(messagesService.getText("button.profile"))) {
            return getProfile(chatId, user);
        }
        if (message.getText().equals(messagesService.getText("button.edit"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.EDIT);
            return Collections.singletonList(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.edit"), keyboardService.getProfileEditMenu()));
        }
        if (message.getText().equals(messagesService.getText("button.menu"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
            return Collections.singletonList(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.menu"), keyboardService.getMainKeyboard()));
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<?>> getProfile(long chatId, User user) {
        return Collections.singletonList(botMethodService.getSendPhoto(chatId,
                imageService.getFile(user.getProfile()), keyboardService.getProfileMenu(),
                user.getProfile().getSex().getName() + ", " + user.getProfile().getName()));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.PROFILE;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return Collections.emptyList();
    }
}
