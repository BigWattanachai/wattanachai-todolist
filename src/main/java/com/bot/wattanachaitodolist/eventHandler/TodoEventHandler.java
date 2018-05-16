package com.bot.wattanachaitodolist.eventHandler;

import com.bot.wattanachaitodolist.service.MessageService;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;

@LineMessageHandler
public class TodoEventHandler {

    private MessageService messageService;

    @Autowired
    public TodoEventHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @EventMapping
    public void receiveMessage(MessageEvent<TextMessageContent> messageEvent) {
         messageService.messageCommand(messageEvent);
    }
}
