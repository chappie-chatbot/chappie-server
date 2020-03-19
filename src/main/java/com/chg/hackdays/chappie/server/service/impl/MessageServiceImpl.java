package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.MessageService;
import com.chg.hackdays.chappie.server.service.MessagingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private static final int DEFAULT_MESSAGE_COUNT = 1000;

    @Autowired
    MessagingProvider messagingProvider;

    @Override
    public void postMessages(Iterable<Message> messages) {
        for (Message message : messages) {
            postMessage(message);
        }
    }

    @Override
    public List<Message> getMessages() {
        return getMessages(null);
    }

    @Override
    public List<Message> getMessages(String topic) {
        return getMessages(topic,0);
    }

    @Override
    public List<Message> getMessages(String topic,int first) {
        return getMessages(topic,first,DEFAULT_MESSAGE_COUNT);
    }

    @Override
    public List<Message> getMessages(String topic, int first, int count) {
        return messagingProvider.get(topic,first,count);
    }

    private void postMessage(Message message) {
        messagingProvider.post(message);
    }
}
