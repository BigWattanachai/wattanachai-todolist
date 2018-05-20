package com.bot.wattanachaitodolist.service;

import com.bot.wattanachaitodolist.common.Tuple;
import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.domain.User;
import com.bot.wattanachaitodolist.repository.TodoRepository;
import com.bot.wattanachaitodolist.repository.UserRepository;
import com.bot.wattanachaitodolist.util.TodoMessageUtil;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class MessageService {
    private UserRepository userRepository;
    private TodoRepository todoRepository;
    private LineMessagingClient lineMessagingClient;

    @Autowired
    public MessageService(UserRepository userRepository, TodoRepository todoRepository,
                          LineMessagingClient lineMessagingClient) {
        this.userRepository = userRepository;
        this.todoRepository = todoRepository;
        this.lineMessagingClient = lineMessagingClient;
    }

    public void messageCommand(MessageEvent<TextMessageContent> messageEvent) {
        TextMessageContent message = messageEvent.getMessage();
        log.info("Got text message from {}: {}", messageEvent.getReplyToken(), message.getText());

        if ("edit".equalsIgnoreCase(message.getText())) {
            this.replyText(messageEvent.getReplyToken(), "https://wattanachai-todolist.herokuapp.com/todos");
        } else {
            executeCreateTodo(messageEvent.getReplyToken(), messageEvent, message);
        }
    }

    private void executeCreateTodo(String replyToken, MessageEvent<TextMessageContent> messageEvent,
                                   TextMessageContent message) {
        Optional<Tuple<String, Date>> taskAndDateTuper = TodoMessageUtil.getTaskAndDateTimeTuper(message.getText());
        if (taskAndDateTuper.isPresent()) {
            final String userId = messageEvent.getSource().getUserId();
            userRepository.findByUserId(userId).map(it -> updateUser(taskAndDateTuper.get(), it))
                    .orElseGet(() -> createNewUser(taskAndDateTuper.get(), userId));
            String response = "Your todo has been created successfully => " + message.getText();
            this.replyText(replyToken, response);
        } else {
            this.replyText(replyToken, "Please enter correct todo format.");
        }
    }

    private User updateUser(Tuple<String, Date> taskAndDateTuper, User it) {
        Todo todoCreated = createTodo(taskAndDateTuper);
        it.getTodoList().add(todoCreated);
        return userRepository.save(it);
    }

    private User createNewUser(Tuple<String, Date> taskAndDateTuper, String userId) {
        Todo todoCreated = createTodo(taskAndDateTuper);
        User user = new User();
        user.setUserId(userId);
        user.setTodoList(Collections.singletonList(todoCreated));
        return userRepository.save(user);
    }

    private Todo createTodo(Tuple<String, Date> taskAndDateTuper) {
        Todo todo = new Todo();
        todo.setCompleted(false);
        todo.setImportant(false);
        todo.setTask(taskAndDateTuper._1);
        todo.setDate(taskAndDateTuper._2);
        return todoRepository.save(todo);
    }

    private void reply(String replyToken, Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(String replyToken, List<Message> messages) {
        try {
            lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void replyText(String replyToken, String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        this.reply(replyToken, new TextMessage(message));
    }
}
