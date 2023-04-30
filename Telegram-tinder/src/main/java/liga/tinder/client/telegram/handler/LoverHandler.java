package liga.tinder.client.telegram.handler;

import liga.tinder.client.domain.Profile;
import liga.tinder.client.domain.ScrollableListWrapper;
import liga.tinder.client.domain.User;
import liga.tinder.client.service.*;
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
public class LoverHandler implements InputMessageHandler {
    private final DataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        if (message.getText().equals(messagesService.getText("button.lovers"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.LOVERS);
            List<Profile> users = serverService.getLowersProfilesToUser(user);
            log.info("Список всех подходящих анкет с размером {}" + users.size());
            if (users.isEmpty()) {
                userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
                return Collections.singletonList(botMethodService.getSendMessage(chatId,
                        messagesService.getText("reply.noProfile"), keyboardService.getMainKeyboard()));
            }
            user.setScrollableListWrapper(new ScrollableListWrapper(users));
            return Collections.singletonList(botMethodService.getSendPhoto(
                    chatId,
                    imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                    keyboardService.getKeyboardLowers(),
                    serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)
            ));
        }
        if (message.getText().equals(messagesService.getText("button.next"))) {
            if (user.getScrollableListWrapper().isLast()) {
                user.getScrollableListWrapper().resetCurrentIndex();
                return Collections.singletonList(botMethodService.getSendPhoto(chatId,
                        imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                        keyboardService.getKeyboardLowers(),
                        serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
            }
            return Collections.singletonList(botMethodService.getSendPhoto(chatId,
                    imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                    keyboardService.getKeyboardLowers(),
                    serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
        }
        if (message.getText().equals(messagesService.getText("button.prev"))) {
            if (user.getScrollableListWrapper().isFirst()) {
                user.getScrollableListWrapper().resetCurrentIndexFromLast();
                return Collections.singletonList(botMethodService.getSendPhoto(chatId,
                        imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                        keyboardService.getKeyboardLowers(),
                        serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
            }
            return Collections.singletonList(botMethodService.getSendPhoto(chatId,
                    imageService.getFile(user.getScrollableListWrapper().getPreviousProfile()),
                    keyboardService.getKeyboardLowers(),
                    serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
        }
        if (message.getText().equals(messagesService.getText("button.menu"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
            return Collections.singletonList(botMethodService.getSendMessage(
                    chatId, messagesService.getText("reply.menu"),
                    keyboardService.getMainKeyboard()
            ));
        }
        return Collections.emptyList();
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LOVERS;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return Collections.emptyList();
    }
}
