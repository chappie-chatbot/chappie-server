package com.chg.hackdays.chappie.server.service;

import com.chg.hackdays.chappie.model.Conversation;
import com.chg.hackdays.chappie.model.Message;

import java.util.List;

public interface MessagingProvider {
    void post(Message msg);

    List<Message> get(String topic, long first, int count);

    List<Conversation> getConversations(Long id, String participant);
}
