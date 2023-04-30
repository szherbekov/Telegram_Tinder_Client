package liga.tinder.client.telegram.handler;

import liga.tinder.client.telegram.botapi.BotState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

/**
 * Обработчик сообщений
 */
public interface InputMessageHandler {
    List<PartialBotApiMethod<?>> handle(Message message);

    BotState getHandlerName();

    List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery);
}

