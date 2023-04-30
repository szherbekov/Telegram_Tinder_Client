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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchHandler implements InputMessageHandler {
    private final DataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;
    private final ServerService serverService;


    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        if (message.getText().equals(messagesService.getText("button.search"))) {
            return getSearch(chatId, user);
        }
        if (message.getText().equals(messagesService.getText("button.like")) || message.getText().equals(messagesService.getText("button.next"))) {
            return getLike(message, chatId, user);
        }
        if (message.getText().equals(messagesService.getText("button.unlike"))
                || message.getText().equals(messagesService.getText("button.next"))) {
            return getUnLike(message, chatId, user);
        }
        if (message.getText().equals(messagesService.getText("button.menu"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
            return Collections.singletonList(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.menu"),
                    keyboardService.getMainKeyboard()));
        }

        return Collections.emptyList();
    }
    private List<PartialBotApiMethod<?>> getUnLike(Message message, long chatId, User user) {
        List<PartialBotApiMethod<?>> unlikeList = new ArrayList<>();
        if (message.getText().equals(messagesService.getText("button.unlike"))) {
            serverService.unLikeProfile(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user);
            unlikeList.add(botMethodService.getSendMessage(chatId, messagesService.getText("reply.unLiked")));
        }

        return unlikeList;
    }
    private List<PartialBotApiMethod<?>> getLike(Message message, long chatId, User user) {
        List<PartialBotApiMethod<?>> answerList = new ArrayList<>();
        SendPhoto profilePhoto = new SendPhoto();
        if (message.getText().equals(messagesService.getText("button.like"))) {
            serverService.likeProfile(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user);
            if (serverService.weLove(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)) {
                answerList.add(botMethodService.getSendMessage(chatId, messagesService.getText("button.reciprocity")));
            }
        }
        if (user.getScrollableListWrapper().isLast()) {
            user.setScrollableListWrapper(new ScrollableListWrapper(serverService.getValidProfilesToUser(user)));
            if (user.getScrollableListWrapper().isEmpty()) {
                SendMessage replyToUser;
                replyToUser = messagesService.getReplyMessage(chatId, "reply.noProfile");
                replyToUser.setReplyMarkup(keyboardService.getMainKeyboard());
                userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
                answerList.add(replyToUser);
                return answerList;
            }
            profilePhoto = botMethodService.getSendPhoto(chatId,
                    imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                    keyboardService.getSearchKeyboard(),
                    serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));
            answerList.add(profilePhoto);
            return answerList;
        }
        profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getNextProfile())));
        profilePhoto.setChatId(String.valueOf(chatId));
        profilePhoto.setReplyMarkup(keyboardService.getSearchKeyboard());
        profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));
        answerList.add(profilePhoto);
        return answerList;
    }

    private List<PartialBotApiMethod<?>> getSearch(long chatId, User user) {
        userDataCache.setUsersCurrentBotState(chatId, BotState.SEARCH);
        List<Profile> users = serverService.getValidProfilesToUser(user);
        log.info("Пришел список подходящих анкет с размером {}", users.size());
        log.info("Список: {}", users);

        if (users.isEmpty()) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
            return Collections.singletonList(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.noProfile"),
                    keyboardService.getMainKeyboard()));
        }

        user.setScrollableListWrapper(new ScrollableListWrapper(users));
        return Collections.singletonList(botMethodService.getSendPhoto(
                chatId,
                imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                keyboardService.getSearchKeyboard(),
                serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
    }



    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return Collections.emptyList();
    }
}
