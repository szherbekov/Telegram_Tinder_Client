package liga.tinder.client.telegram.handler;

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
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EditProfileHandler implements InputMessageHandler {

    private final DataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        Long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);
        if (botState.equals(BotState.EDIT_NAME)) {
            return getEditName(chatId, user, message.getText());
        }
        if (botState.equals(BotState.EDIT_DESCRIPTION)) {
            return getEditDescription(chatId, user, message.getText());
        }
        if (message.getText().equals(messagesService.getText("button.editSex"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.EDIT_SEX);
            return List.of(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.askSex"),
                    keyboardService.getInlineKeyboardFindSex())
            );
        }
        if (message.getText().equals(messagesService.getText("button.editName"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.EDIT_NAME);
            return List.of(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askName"))
            );
        }
        if (message.getText().equals(messagesService.getText("button.editDescription"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.EDIT_DESCRIPTION);
            return List.of(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askDescription"))
            );
        }
        if (message.getText().equals(messagesService.getText("button.editFindSex"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.EDIT_FIND);
            return List.of(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askFindSex"))
            );
        }
        if (message.getText().equals(messagesService.getText("button.menu"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
            return List.of(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.menu"))
            );
        }
        return Collections.emptyList();
    }

    @Override
    public BotState getHandlerName() {
        return BotState.EDIT;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);
        User user = userDataCache.getUserProfileData(chatId);
        String usersAnswer = callbackQuery.getData();
        if (botState.equals(BotState.EDIT_SEX)) {
            return getEditSex(chatId, user, usersAnswer);
        }
        if (botState.equals(BotState.EDIT_FIND)) {
            return getEditFindSex(chatId, user, usersAnswer);
        }

        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<?>> getEditFindSex(Long chatId, User user, String usersAnswer) {
        if (usersAnswer.equals(messagesService.getText("button.allSex"))) {
            user.getProfile().setFindSex(Set.of(Sex.values()));
        } else {
            user.getProfile().setFindSex(Set.of(Sex.valueOf(usersAnswer)));
        }
        serverService.updateCurrentUser(user.getProfile(), user);
        userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE);
        return List.of(botMethodService.getSendPhoto(chatId,
                imageService.getFile(user.getProfile()),
                keyboardService.getProfileMenu(), user.getProfile().getSex().getName() + ", " +
                        user.getProfile().getName()), botMethodService.getSendMessage(chatId,
                messagesService.getText("reply.editFindSex")));
    }

    private List<PartialBotApiMethod<?>> getEditSex(Long chatId, User user, String usersAnswer) {
        user.getProfile().setName(usersAnswer);
        serverService.updateCurrentUser(user.getProfile(), user);
        userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE);
        return List.of(botMethodService.getSendPhoto(chatId,
                imageService.getFile(user.getProfile()), keyboardService.getProfileMenu(), user.getProfile().getSex().getName() + ", " +
                        user.getProfile().getName()), botMethodService.getSendMessage(chatId,
                messagesService.getText("reply.editSex")));
    }

    private List<PartialBotApiMethod<?>> getEditDescription(Long chatId, User user, String text) {
        user.getProfile().setDescription(text);
        serverService.updateCurrentUser(user.getProfile(), user);
        userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE);
        return List.of(botMethodService.getSendPhoto(chatId,
                        imageService.getFile(user.getProfile()),
                        keyboardService.getProfileMenu(), user.getProfile().getSex().getName() + ", " +
                                user.getProfile().getName()), botMethodService.getSendMessage(
                        chatId, messagesService.getText("reply.editDescription")
                )
        );
    }

    private List<PartialBotApiMethod<?>> getEditName(Long chatId, User user, String usersAnswer) {
        user.getProfile().setName(usersAnswer);
        serverService.updateCurrentUser(user.getProfile(), user);
        userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE);
        return List.of(botMethodService.getSendPhoto(chatId,
                        imageService.getFile(user.getProfile()),
                        keyboardService.getProfileMenu(), user.getProfile().getSex().getName() + ", " +
                                user.getProfile().getName()),
                botMethodService.getSendMessage(chatId, messagesService.getText("reply.editName"))
        );
    }
}
