package groupId.OrderCarpetBot.service;

import groupId.OrderCarpetBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
   final BotConfig config;
    static final String ABOUTUS_TEXT = "Ми чудові творці казкових та дивовижних килимків\uD83D\uDE0D\uD83D\uDE0D\uD83D\uDE0DПрацюємо у Харькові вже пів року і відправляємо замовлення по всій країні!\n" +
            "Якщо ви хочете насолоджуватись красою та ніжністю у своїй домівкі, то ви звернулися за адресою. \n" +
            "Для замовлення все докладно розписано у боті, тому не зважай і не чекай, а скоріше зроби дивовижну атмосферу у свєму домі☺️\uD83D\uDC4D\uD83C\uDFFB";

    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config){
       this.config = config;
       List<BotCommand> listofCommands = new ArrayList<>();
       listofCommands.add(new BotCommand("/start", "start work with bot"));
       listofCommands.add(new BotCommand("/aboutus", "see information about owner"));
       listofCommands.add(new BotCommand("/checkprice", "check  approximate price"));
       listofCommands.add(new BotCommand("/choosecarpet", "choose carpet from one of proposed"));
       listofCommands.add(new BotCommand("/createcarpet", "create your own carpet"));
       listofCommands.add(new BotCommand("/contactme", "ask owner to contact you"));
       try {
           this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
       } catch (TelegramApiException e) {
           log.error("Error setting bot's command list: " + e.getMessage());
       }
   }
    @Override
    public void onUpdateReceived(Update update){
       if(update.hasMessage() && update.getMessage().hasText()) {
           String messageText = update.getMessage().getText();
           long chatId = update.getMessage().getChatId();

           switch (messageText) {
               case "/start":
                   startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                   break;
               case "/aboutus":
               prepareAndSendMessage(chatId, ABOUTUS_TEXT);
               break;
                default:
               sendMessage(chatId, "Ця команда наразі недоступна");
           }
       }
    }

    @Override
    public String getBotUsername(){
        return config.getBotName();
    }

    @Override
    public String getBotToken(){
       return config.getToken();
    }

    private void startCommandReceived(long chatId, String name){
       String answer = "Вітаємо в боті для замовлення килимів ручної роботи";
       sendMessage(chatId,answer);
    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        }
        catch (TelegramApiException e){
        }
    }
    private void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }
    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }
}
