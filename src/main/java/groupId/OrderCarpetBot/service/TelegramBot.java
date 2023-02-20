package groupId.OrderCarpetBot.service;

import com.pengrad.telegrambot.request.SendPhoto;
import groupId.OrderCarpetBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    static final String ABOUTUS_TEXT = "Ми чудові творці казкових та дивовижних килимків\uD83D\uDE0D\uD83D\uDE0D\uD83D\uDE0DПрацюємо у Харькові вже пів року і відправляємо замовлення по всій країні!\n" +
            "Якщо ви хочете насолоджуватись красою та ніжністю у своїй домівкі, то ви звернулися за адресою. \n" +
            "Для замовлення все докладно розписано у боті, тому не зважай і не чекай, а скоріше зроби дивовижну атмосферу у свєму домі☺️\uD83D\uDC4D\uD83C\uDFFB";
    static final String CHECKPRICE_TEXT = "Тут ти можеш дізнатися приблизну ціну за килим. Для початку обери форму бажаного килиму:";

    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "start work with bot"));
        listOfCommands.add(new BotCommand("/aboutus", "see information about owner"));
        listOfCommands.add(new BotCommand("/checkprice", "check  approximate price"));
        listOfCommands.add(new BotCommand("/choosecarpet", "choose carpet from one of proposed"));
        listOfCommands.add(new BotCommand("/createcarpet", "create your own carpet"));
        listOfCommands.add(new BotCommand("/contactme", "ask owner to contact you"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/aboutus":
                    prepareAndSendMessage(chatId, ABOUTUS_TEXT);
                    break;
                case "/checkprice":
                    chooseForm(chatId);
                    if (update.hasCallbackQuery()){
                        String callbackData = update.getCallbackQuery().getData();
                        chatId = update.getCallbackQuery().getMessage().getChatId();
                        if(callbackData.equals("CIRCLE")){
                            chooseSizeCircle(chatId);
                        }
                    }
                    break;
                case "/createcarpet":

                    break;
                default:
                    sendMessage(chatId, "Ця команда наразі недоступна");
            }
        } else if (update.hasCallbackQuery()){
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if(callbackData.equals("CIRCLE")){
                prepareAndSendMessage(chatId, "Введіть радіус килима");
                update.getMessage();
                int radius = Integer.parseInt(update.getMessage().getText());
                int cost = 5 * (3 * (radius * radius));
                prepareAndSendMessage(chatId, String.valueOf(cost));
            } else if(callbackData.equals("SQUARE")){
                chooseSizeCircle(chatId);
            } else if(callbackData.equals("RECTANGLE")){
                chooseSizeCircle(chatId);
            } else if(callbackData.equals("TRIANGLE")){
                chooseSizeCircle(chatId);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Вітаємо, " + name + "в боті для замовлення килимів ручної роботи";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
        }
    }

    private void chooseForm(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(CHECKPRICE_TEXT);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var button1 = new InlineKeyboardButton();

        button1.setText("Коло");
        button1.setCallbackData("CIRCLE");

        var button2 = new InlineKeyboardButton();

        button2.setText("Квадрат");
        button2.setCallbackData("SQUARE");

        var button3 = new InlineKeyboardButton();

        button3.setText("Прямокутник");
        button3.setCallbackData("RECTANGLE");

        var button4 = new InlineKeyboardButton();

        button4.setText("Трикутник");
        button4.setCallbackData("TRIANGLE");

        rowInLine.add(button1);
        rowInLine.add(button2);
        rowInLine.add(button3);
        rowInLine.add(button4);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    private void chooseSizeCircle(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Тут ви можете обрати радіус килима");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var button1 = new InlineKeyboardButton();

        button1.setText("20");
        button1.setCallbackData("TWENTY");

        var button2 = new InlineKeyboardButton();

        button2.setText("30");
        button2.setCallbackData("THIRTY");

        var button3 = new InlineKeyboardButton();

        button3.setText("40");
        button3.setCallbackData("FORTY");

        var button4 = new InlineKeyboardButton();

        button4.setText("50");
        button4.setCallbackData("FIFTY");

        rowInLine.add(button1);
        rowInLine.add(button2);
        rowInLine.add(button3);
        rowInLine.add(button4);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }


    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }
}
