package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.model.MessageId;
import com.chg.hackdays.chappie.server.service.MessageService;
import com.chg.hackdays.chappie.server.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private static final int DEFAULT_MESSAGE_COUNT = 1000;

    @Autowired
    DBMessagingProvider messagingProvider;
    @Autowired
    SessionService sessionService;

    @Override
    public void postMessage(Message message) {
        initializeMessage(message);

        messagingProvider.post(message);
    }

    @Override
    public List<Message> getMessages() {
        return getMessages(null);
    }

    @Override
    public List<Message> getMessages(String topic) {
        return getMessages(topic, 0);
    }

    @Override
    public List<Message> getMessages(String topic, long first) {
        return getMessages(topic, first, DEFAULT_MESSAGE_COUNT);
    }

    @Override
    public List<Message> getMessages(String topic, long first, int count) {
        return messagingProvider.get(topic, first, count);
    }

    @Override
    public List<Message> getMessagesByConversation(String topic, long conversationId, long first) {
        return getMessagesByConversation(topic, conversationId, first, DEFAULT_MESSAGE_COUNT);
    }

    @Override
    public List<Message> getMessagesByConversation(String topic, long conversationId, long first, int count) {
        return messagingProvider.getByConversation(topic, conversationId, first, count);
    }

    @Override
    public Message getMessage(MessageId msgId) {
        return getMessages(msgId.getTopic(), msgId.getOffset(), 1).stream().findFirst().orElse(null);
    }

    private void initializeMessage(Message message) {
        if (message.getConversation() == null || message.getConversation() <= 0)
            message.setConversation(sessionService.getCurrentConversationId());
        if (message.getType() == null)
            message.setType("text");
        if (message.getMime() == null)
            message.setMime("text/plain");
        if (message.getSource() == null)
            message.setSource(sessionService.getUsername());
        message.setTimestamp(ZonedDateTime.now());
    }
}
