package liga.tinder.client.telegram.handler;

import liga.tinder.client.domain.AuthenticUser;
import liga.tinder.client.domain.Sex;
import liga.tinder.client.domain.User;
import liga.tinder.client.service.*;
import liga.tinder.client.telegram.botapi.BotState;
import liga.tinder.client.telegram.cache.DataCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FillingProfileHandler implements InputMessageHandler {
    private final DataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_NAME);
        }
        return processUsersInput(message);
    }

    private List<PartialBotApiMethod<?>> processUsersInput(Message inputMessage) {
        String usersAnswer = inputMessage.getText();
        long chatId = inputMessage.getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);
        if (botState.equals(BotState.ASK_SEX)) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_NAME);
            user.getProfile().setPassword(usersAnswer);
            return Collections.singletonList(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.askSex"), keyboardService.getInlineKeyboardFindSex()));
        }
        if (botState.equals(BotState.ASK_DESCRIPTION)) {
            user.getProfile().setName(usersAnswer);
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_FIND);
            return Collections.singletonList(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.description")));
        }
        if (botState.equals(BotState.ASK_FIND)) {
            user.getProfile().setName(usersAnswer);
            userDataCache.setUsersCurrentBotState(chatId, BotState.FILLING_PROFILE);
            return Collections.singletonList(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.askFindSex"), keyboardService.getInlineKeyboardFindSex()));
        }
        return Collections.emptyList();
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        String usersAnswer = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);
        if (botState.equals(BotState.ASK_NAME)) {
            user.getProfile().setSex(Sex.valueOf(usersAnswer));
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_DESCRIPTION);
            return Collections.singletonList(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.askName")));
        }
        if (botState.equals(BotState.PROFILE_FILLED)) {
            user.getProfile().setFindSex(new HashSet<>());
            if (usersAnswer.equals(messagesService.getText("button.allSex"))) {
                user.getProfile().getFindSex().add(Sex.MALE);
                user.getProfile().getFindSex().add(Sex.FEMALE);
            } else {
                user.getProfile().getFindSex().add(Sex.valueOf(usersAnswer));
            }

            serverService.registerUser(user.getProfile());
            user.setToken(serverService.loginUser(new AuthenticUser(user.getProfile().getUserId(), user.getProfile().getPassword())));
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);


            return Collections.singletonList(botMethodService.getSendPhoto(chatId,
                    imageService.getFile(user.getProfile()),
                    keyboardService.getMainKeyboard(), user.getProfile().getSex().getName() + ", " +
                            user.getProfile().getName()));
        }
        return Collections.emptyList();
    }
}
