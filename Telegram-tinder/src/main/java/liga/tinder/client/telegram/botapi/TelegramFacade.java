package liga.tinder.client.telegram.botapi;

import liga.tinder.client.telegram.cache.UserDataCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TelegramFacade {

    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;


    public List<PartialBotApiMethod<?>> handleUpdate(Update update) {
        List<PartialBotApiMethod<?>> replayMessage = new ArrayList<>();
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            replayMessage = handleInputCallBackQuery(callbackQuery);
        }
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            replayMessage = handleInputMessage(message);
        }
        return replayMessage;
    }

    private List<PartialBotApiMethod<?>> handleInputMessage(Message message) {
        String inputMessage = message.getText();
        Long chatId = message.getChatId();
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);
        if (inputMessage.equals("/start")) {
            botState = BotState.AUTHENTICATE;
            userDataCache.setUsersCurrentBotState(chatId, botState);
            return botStateContext.processInputMessage(botState, message);
        }
        return botStateContext.processInputMessage(botState, message);
    }

    private List<PartialBotApiMethod<?>> handleInputCallBackQuery(CallbackQuery callbackQuery) {
        return botStateContext.processInputCallBack(userDataCache.getUsersCurrentBotState(callbackQuery.getFrom().getId()), callbackQuery);
    }
}
