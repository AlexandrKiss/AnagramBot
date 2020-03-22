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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Component
@PropertySource("classpath:telegram.properties")
public class ChatBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(ChatBot.class);

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
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        String text = update.getMessage().getText();;
        final long chatId = update.getMessage().getChatId();
        final String userName = update.getMessage().getFrom().getUserName();

        if(text.equals("/start")) {
            sendMessage(chatId, "Я бот для решения анограмм на русском языке. " +
                    "Напиши мне анаграмму и я попробую ее разгадать \uD83D\uDE09. Например: <pre>банка</pre>");
            return;
        }

        User user = userService.findByChatId(chatId);

        if (user == null) {
            if(userName==null) {
                user = new User(chatId);
                sendMessage(chatId,"Привет \uD83D\uDC4B. Не узнаю тебя. Давай познакомимся. Как твое имя?");
                user.setQestion(true);
                user.setLastRequest(text);
                userService.addUser(user);
                return;
            } else {
                user = new User(chatId,userName);
                sendMessage(chatId, "Привет \uD83D\uDC4B <b>"+user.getName()+"</b>. Рад с тобой познакомиться \uD83D\uDE0A.");
                userService.addUser(user);
            }
        } else {
            if(user.getQestion()){
                user.setName(text);
                user.setQestion(false);
                sendMessage(chatId, "<b>"+user.getName()+"</b> - шикарное имя \uD83D\uDC4C. Рад с тобой познакомиться \uD83D\uDE0A.");
                userService.addUser(user);
                text = user.getLastRequest();
            }
        }

        logger.info(user.toString());

        logger.info(userName+"("+chatId+"): "+text);
        try {
            sendMessage(chatId, anag(text, words()));
            user.setCountRequest(user.getCountRequest()+1);
            userService.addUser(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String anag(String string, List<String>words){
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

    private void sendMessage(long chatId, String text) {
        SendChatAction sendChatAction = new SendChatAction()
                .setChatId(chatId)
                .setAction(ActionType.get("typing"));
        SendMessage message = new SendMessage()
                .setChatId(chatId)
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