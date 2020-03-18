package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.MessageService;
import com.chg.hackdays.chappie.server.service.MessagingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
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
        throw new UnsupportedOperationException("TODO: Get messages");
    }

    private void postMessage(Message message) {
        messagingProvider.post(message);
    }
}
