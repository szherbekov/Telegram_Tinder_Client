package liga.tinder.client.telegram;

import liga.tinder.client.telegram.botapi.TelegramFacade;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Getter
@Setter
@RequiredArgsConstructor
public class LigaOldTinderBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramLongPollingBot.class);

    @Value("${telegram.bot-name}")
    private String botUsername;
    @Value("${telegram.bot-token}")
    private String botToken;

    private final TelegramFacade telegramFacade;

    /**
     * Метод, куда приходят обновления update от телеграмм бота и отправляются на дальнейшую обработку
     * в результате обработки возвращается ответ, который в зависимости от содержимого
     * приводится к определенному типу и отправляется клиенту
     *
     * @param update - обновление от телеграмм бота, содержащее сообщение
     */
    @Override
    public void onUpdateReceived(Update update) {
        for (PartialBotApiMethod<?> method : telegramFacade.handleUpdate(update)) {
            try {
                if (method instanceof SendPhoto) {
                    execute((SendPhoto) method);
                } else if (method instanceof SendMessage) {
                    execute((SendMessage) method);
                } else if (method instanceof AnswerCallbackQuery) {
                    execute((AnswerCallbackQuery) method);
                }
            } catch (TelegramApiException e) {
                log.info(e.getMessage());
            }
        }
    }
}



