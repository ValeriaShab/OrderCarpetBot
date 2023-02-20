package groupId.OrderCarpetBot.service;

import groupId.OrderCarpetBot.config.BotConfig;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileNotFoundException;
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
        if (update.hasMessage() && update.getMessage().isCommand()) {
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
                    if (update.hasCallbackQuery()) {
                        String callbackData = update.getCallbackQuery().getData();
                        chatId = update.getCallbackQuery().getMessage().getChatId();
                        if (callbackData.equals("CIRCLE")) {
                        }
                    }
                    break;
                case "/choosecarpet":
                    try {
                        sendPhoto(chatId,"Назва: Килим з руною Берсерк, Розмір: 50*35 см, Ціна: 800грн", "photos/berserk.jpg");
                        sendPhoto(chatId,"Назва: Килим <Stone Island>, Розмір: 100*50 см, Ціна: 1400грн", "photos/stone island.jpg");
                        sendPhoto(chatId,"Назва: Килим <Binance>, Розмір: 100*50 см, Ціна: 1200грн", "photos/binance.jpg");
                        sendPhoto(chatId,"Назва: Килим-диск <EMINEM>, Розмір: 90*90 см, Ціна: 1300грн", "photos/EMINEM cd.jpg");
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "/createcarpet":

                    break;
                default:
                    sendMessage(chatId, "Ця команда наразі недоступна");
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callbackData.equals("CIRCLE")) {
                chooseSizeCircle(chatId);
            } else if (callbackData.equals("SQUARE")) {
                chooseSizeSquare(chatId);
            } else if (callbackData.equals("RECTANGLE")) {
                chooseSizeRectangle(chatId);
            } else if (callbackData.equals("TRIANGLE")) {
                chooseSizeTriangle(chatId);
            } else if (callbackData.equals("TWENTY")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 300грн");
            } else if (callbackData.equals("THIRTY")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 570грн");
            } else if (callbackData.equals("FORTY")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 1000грн");
            } else if (callbackData.equals("FIFTY")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 1570грн");
            } else if (callbackData.equals("FIFTY_SQUARE")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 1500грн");
            } else if (callbackData.equals("SIXTY")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 2160грн");
            } else if (callbackData.equals("SEVENTY")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 2940грн");
            } else if (callbackData.equals("EIGHTY")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 3840грн");
            } else if (callbackData.equals("FIRST_RECTANGLE")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 1440грн");
            } else if (callbackData.equals("SECOND_RECTANGLE")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 2100грн");
            } else if (callbackData.equals("THIRD_RECTANGLE")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 2880грн");
            } else if (callbackData.equals("FOURTH_RECTANGLE")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 3780грн");
            } else if (callbackData.equals("FORTY_TRIANGLE")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 1300грн");
            } else if (callbackData.equals("FIFTY_TRIANGLE")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 2165грн");
            } else if (callbackData.equals("SIXTY_TRIANGLE")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 3120грн");
            } else if (callbackData.equals("SEVENTY_TRIANGLE")) {
                prepareAndSendMessage(chatId, "Такий килим буде коштувати 4243грн");
            }
        } else {
            long chatId = update.getMessage().getChatId();
            prepareAndSendMessage(chatId, "Оберіть команду зі списку");
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
        String answer = "Вітаємо, " + name + ", в боті для замовлення килимів ручної роботи";
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

    public void chooseSizeCircle(long chatId) {
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

    public void chooseSizeSquare(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Тут ви можете обрати розмір сторони килима");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var button1 = new InlineKeyboardButton();

        button1.setText("50");
        button1.setCallbackData("FIFTY_SQUARE");

        var button2 = new InlineKeyboardButton();

        button2.setText("60");
        button2.setCallbackData("SIXTY");

        var button3 = new InlineKeyboardButton();

        button3.setText("70");
        button3.setCallbackData("SEVENTY");

        var button4 = new InlineKeyboardButton();

        button4.setText("80");
        button4.setCallbackData("EIGHTY");

        rowInLine.add(button1);
        rowInLine.add(button2);
        rowInLine.add(button3);
        rowInLine.add(button4);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    public void chooseSizeRectangle(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Тут ви можете обрати розмір сторіни килима");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var button1 = new InlineKeyboardButton();

        button1.setText("40*60");
        button1.setCallbackData("FIRST_RECTANGLE");

        var button2 = new InlineKeyboardButton();

        button2.setText("50*70");
        button2.setCallbackData("SECOND_RECTANGLE");

        var button3 = new InlineKeyboardButton();

        button3.setText("60*80");
        button3.setCallbackData("THIRD_RECTANGLE");

        var button4 = new InlineKeyboardButton();

        button4.setText("70*90");
        button4.setCallbackData("FOURTH_RECTANGLE");

        rowInLine.add(button1);
        rowInLine.add(button2);
        rowInLine.add(button3);
        rowInLine.add(button4);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    public void chooseSizeTriangle(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Тут ви можете обрати розмір сторони килима");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var button1 = new InlineKeyboardButton();

        button1.setText("40");
        button1.setCallbackData("FORTY_TRIANGLE");

        var button2 = new InlineKeyboardButton();

        button2.setText("50");
        button2.setCallbackData("FIFTY_TRIANGLE");

        var button3 = new InlineKeyboardButton();

        button3.setText("60");
        button3.setCallbackData("SIXTY_TRIANGLE");

        var button4 = new InlineKeyboardButton();

        button4.setText("70");
        button4.setCallbackData("SEVENTY_TRIANGLE");

        rowInLine.add(button1);
        rowInLine.add(button2);
        rowInLine.add(button3);
        rowInLine.add(button4);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    public void sendPhoto(long chatId, String imageCaption, String imagePath) throws TelegramApiException, FileNotFoundException {
        File image = ResourceUtils.getFile("classpath:" + imagePath);
        InputFile inputFile = new InputFile(image);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setCaption(imageCaption);
        execute(sendPhoto);
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
