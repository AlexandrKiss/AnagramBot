package ua.kiev.prog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.kiev.prog.models.CustomMessage;
import ua.kiev.prog.models.CustomUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
@PropertySource("classpath:telegram.properties")
public class ChatBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(MyController.class);

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    private final UserService userService;

    public ChatBot(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        final String text = update.getMessage().getText();
        final long chatId = update.getMessage().getChatId();
        final String userName = update.getMessage().getFrom().getUserName();
        if(update.getMessage().hasContact()) {
            Contact contact = update.getMessage().getContact();
            logger.info(contact.toString());
        }

        CustomUser user = userService.findByChatId(chatId);
        if(acquaintance(user, chatId, userName, text)) {
            return; //знакомство с новым User
        }

        try {
            userService.addMessage(new CustomMessage(user, text, false)); //сохраняем сообщение User
            sendMessage(user, anagram(text, words()),false);//отправляем сообщение бота
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean acquaintance(CustomUser user, long chatId, String userName, String text){
        String startText = "Я бот для решения анаграмм на русском языке. " +
                "Напиши мне анаграмму и я попробую ее разгадать \uD83D\uDE09. Например: <pre>банка</pre>";
        if (user == null) {
            if(userName==null) {
                user = new CustomUser(chatId);
                user.setQestion(true);
                userService.addUser(user);
                sendMessage(user,
                        "Привет \uD83D\uDC4B. Не узнаю тебя. Давай познакомимся. Как твое имя?",true);
            } else {
                user = new CustomUser(chatId,userName);
                userService.addUser(user);
                sendMessage(user,
                        "Привет \uD83D\uDC4B <b>"+user.getName()+"</b>. Рад с тобой познакомиться \uD83D\uDE0A.",false);
                sendMessage(user, startText,false);
            }
            return true;
        } else {
            if(user.getQestion()){
                user.setName(text);
                user.setQestion(false);
                userService.updateUser(user);
                userService.addMessage(new CustomMessage(user, text, false));
                sendMessage(user,
                        "<b>"+user.getName()+"</b> - шикарное имя \uD83D\uDC4C. Рад с тобой познакомиться \uD83D\uDE0A.", false);
                sendMessage(user, startText, false);
                return true;
            }
        }
        return false;
    }

    private String anagram(String string, List<String>words){
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        boolean eq = false;
        for (String s: words){
            if(!string.toLowerCase().equals(s.toLowerCase())) {
                if (string.length() == s.length()) {
                    String s1 = string.toLowerCase().chars()
                            .sorted()
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString();
                    String s2 = s.toLowerCase().chars()
                            .sorted()
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString();
                    if (s1.equals(s2)) {
                        count++;
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(", ").append(s);
                        } else {
                            stringBuilder.append(s);
                        }
                    }
                }
            } else {
                eq = true;
            }
        }
        if(count==1) {
            return "Я нашел для тебя подходящее слово \uD83D\uDE0A: <b>"+stringBuilder.toString()+"</b>";
        } else if(count>1) {
            return "Я нашел для тебя несколько подходящих слов \uD83D\uDE0E ("+count+"): <b>"+stringBuilder.toString()+"</b>";
        } else {
            if(eq){
                return "Слово <b>\""+string+"\"</b> не являеться анограммой \uD83D\uDE33. Давай попробуем ещё.";
            } else {
                return "Я не нашел подходящих слов для анаграммы \uD83D\uDE26: <b>" + string + "</b>";
            }
        }
    }

    private void sendMessage(CustomUser user, String text, boolean requestContact) {
        CustomMessage customMessage = new CustomMessage(user, text, true);
        userService.addMessage(customMessage);

        SendChatAction sendChatAction = new SendChatAction()
                .setChatId(user.getChatId())
                .setAction(ActionType.get("typing"));
        SendMessage message = new SendMessage()
                .setChatId(user.getChatId())
                .setText(text)
                .setParseMode("html");
        try {
            execute(sendChatAction);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<String> words() throws IOException {
        return Files.readAllLines(Paths.get("pldf-win.txt"));
    }
}