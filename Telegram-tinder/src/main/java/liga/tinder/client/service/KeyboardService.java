package liga.tinder.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyboardService {
    private final TextMessagesService messagesService;

    private ReplyKeyboardMarkup getOneColumnKeyboard(Boolean oneTime, String... buttons) {
        ReplyKeyboardMarkup oneColumnKeyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        oneColumnKeyboard.setSelective(true);
        oneColumnKeyboard.setResizeKeyboard(true);
        oneColumnKeyboard.setOneTimeKeyboard(oneTime);
        for (String button : buttons) {
            KeyboardRow row = new KeyboardRow();
            row.add(button);
            keyboardRows.add(row);
        }
        oneColumnKeyboard.setKeyboard(keyboardRows);
        return oneColumnKeyboard;
    }

    private ReplyKeyboardMarkup getOneRowKeyboard(Boolean oneTime, String... buttons) {
        ReplyKeyboardMarkup oneRowKeyboard = new ReplyKeyboardMarkup();
        oneRowKeyboard.setSelective(true);
        oneRowKeyboard.setResizeKeyboard(true);
        oneRowKeyboard.setOneTimeKeyboard(oneTime);
        KeyboardRow keyboardRow = new KeyboardRow();
        for (String button : buttons) {
            keyboardRow.add(button);
        }
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardRow);
        oneRowKeyboard.setKeyboard(keyboardRows);
        return oneRowKeyboard;
    }

    public ReplyKeyboardMarkup getMainKeyboard() {
        return getOneRowKeyboard(true,
                messagesService.getText("button.search"),
                messagesService.getText("button.profile"),
                messagesService.getText("button.lovers"));
    }

    public ReplyKeyboardMarkup getSearchKeyboard() {
        return getOneRowKeyboard(true,
                messagesService.getText("button.next"),
                messagesService.getText("button.menu"),
                messagesService.getText("button.like"));
    }

    public ReplyKeyboardMarkup getKeyboardLowers() {
        return getOneRowKeyboard(true,
                messagesService.getText("button.prev"),
                messagesService.getText("button.menu"),
                messagesService.getText("button.next"));
    }

    public ReplyKeyboardMarkup getProfileMenu() {
        return getOneRowKeyboard(true,
                messagesService.getText("button.edit"),
                messagesService.getText("button.menu"));
    }

    public ReplyKeyboardMarkup getProfileEditMenu() {
        return getOneColumnKeyboard(true,
                messagesService.getText("button.editSex"),
                messagesService.getText("button.editName"),
                messagesService.getText("button.editDescription"),
                messagesService.getText("button.editFindSex"),
                messagesService.getText("button.menu"));
    }


    public ReplyKeyboardMarkup getAuthenticateKeyboard() {
        return getOneRowKeyboard(true,
                messagesService.getText("button.registration"),
                messagesService.getText("button.login"));
    }


    private InlineKeyboardButton makeButton(String nameButton, String dataButton) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(nameButton);
        button.setCallbackData(dataButton);
        return button;
    }

    private List<InlineKeyboardButton> makeInlineKeyboardButtonsRow(InlineKeyboardButton... buttons) {
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        Collections.addAll(keyboardButtonsRow, buttons);
        return keyboardButtonsRow;
    }

    public InlineKeyboardMarkup getInlineKeyboardSex() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton male = makeButton(messagesService.getText("button.male"), messagesService.getText("data.male"));
        InlineKeyboardButton female = makeButton(messagesService.getText("button.female"), messagesService.getText("data.female"));

        List<InlineKeyboardButton> keyboardButtonsRow = makeInlineKeyboardButtonsRow(male, female);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineKeyboardFindSex() {
        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardSex();
        inlineKeyboardMarkup.getKeyboard().get(0).add(makeButton(messagesService.getText("button.allSex"),
                messagesService.getText("button.allSex")));
        return inlineKeyboardMarkup;
    }



}
