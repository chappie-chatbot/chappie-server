package com.chg.hackdays.chappie.server.service;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.model.MessageId;

import java.util.List;

public interface MessageService {
    default void postMessages(Iterable<Message> messages) {
        for (Message message : messages) {
            postMessage(message);
        }
    }

    void postMessage(Message message);

    List<Message> getMessages();

    List<Message> getMessages(String topic);

    List<Message> getMessages(String topic, long first);

    List<Message> getMessages(String topic, long first, int count);

    List<Message> getMessagesByConversation(String topic, long conversationId, long first);

    List<Message> getMessagesByConversation(String topic, long conversationId, long first, int count);

    Message getMessage(MessageId msgId);
}
