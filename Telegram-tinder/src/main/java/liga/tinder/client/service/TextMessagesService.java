package liga.tinder.client.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


/**
 * Формирует готовые ответные сообщения в чат.
 */
@Service
public class TextMessagesService {

    private final LocaleMessageService localeMessageService;

    public TextMessagesService(LocaleMessageService messageService) {
        this.localeMessageService = messageService;
    }

    public SendMessage getReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(String.valueOf(chatId), localeMessageService.getMessage(replyMessage));
    }

    public String getText(String code) {
        return localeMessageService.getMessage(code);
    }

}
