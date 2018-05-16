package com.bot.wattanachaitodolist.service;

import com.bot.wattanachaitodolist.constant.MessageCommand;
import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.repository.TodoRepository;
import com.bot.wattanachaitodolist.util.JsonMapper;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class MessageService {
    private TodoRepository todoRepository;
    private LineMessagingClient lineMessagingClient;

    @Autowired
    public MessageService(TodoRepository todoRepository, LineMessagingClient lineMessagingClient) {
        this.todoRepository = todoRepository;
        this.lineMessagingClient = lineMessagingClient;
    }

    public void messageCommand(MessageEvent<TextMessageContent> messageEvent) {

        TextMessageContent message = messageEvent.getMessage();
        log.info("Got text message from {}: {}", messageEvent.getReplyToken(), message.getText());
        log.info("Source Info : {}", messageEvent.getSource());

        MessageCommand command = parseMessageCommand(message.getText());

        if ("edit".equalsIgnoreCase(message.getText())) {
            this.replyText(messageEvent.getReplyToken(), "https://medium.com/@Big.Wattanachai");
        } else {
            this.replyText(messageEvent.getReplyToken(), JsonMapper.toJson(messageEvent).orElse("cannot map json"));
        }

//        switch (command) {
//            case CREATE_TODO:
//                log.info("CREATE_TODO from {}: {}", messageEvent.getReplyToken(), message.getText());
//                executeCreateTodo(messageEvent.getReplyToken(), messageEvent, message);
//            case EDIT_TODO:
//                log.info("EDIT_TODO from {}: {}", messageEvent.getReplyToken(), message.getText());
//                executeEditTodoList();
//            default:
//                log.info("default {}", message.getText());
//
//        }
    }

    private void executeCreateTodo(String replyToken, MessageEvent<TextMessageContent> messageEvent,
                                   TextMessageContent message) {
        Todo todo = new Todo();
        todo.setImportant(false);
        todo.setCompleted(false);
        todo.setTask(message.getText());
        todoRepository.save(todo);
        String response = "Your todo has been created successfully : " + message.getText();
        this.replyText(replyToken, response);
    }

    private void executeEditTodoList() {

    }

    protected void reply(String replyToken, Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    protected void reply(String replyToken, List<Message> messages) {
        try {
            BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    protected void replyText(String replyToken, String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    protected static String createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
    }


    private MessageCommand parseMessageCommand(String receiveMessage) {
        return Arrays.stream(MessageCommand.values())
                .filter(it -> receiveMessage.contains(it.getCommand()))
                .findFirst().orElse(MessageCommand.NO_COMMAND);
    }
}
