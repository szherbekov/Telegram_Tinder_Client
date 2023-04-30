package liga.tinder.client.telegram.handler;

import liga.tinder.client.domain.AuthenticUser;
import liga.tinder.client.domain.User;
import liga.tinder.client.service.BotMethodService;
import liga.tinder.client.service.KeyboardService;
import liga.tinder.client.service.ServerService;
import liga.tinder.client.service.TextMessagesService;
import liga.tinder.client.telegram.botapi.BotState;
import liga.tinder.client.telegram.cache.DataCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginHandler implements InputMessageHandler {
    private final DataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final BotMethodService botMethodService;

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        user.setToken(serverService.loginUser(new AuthenticUser(chatId, message.getText())));
        if (user.getToken().isEmpty()) {
            return List.of(
                    botMethodService.getSendMessage(chatId,
                            messagesService.getText("reply.wrongPassword"), keyboardService.getAuthenticateKeyboard())
            );
        }
        user.setProfile(serverService.getLoginUserProfile(user));
        userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
        return Collections.singletonList(botMethodService.getSendMessage(chatId,
                messagesService.getText("reply.menu"), keyboardService.getMainKeyboard()));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LOGIN;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return Collections.emptyList();
    }
}
