package com.chg.hackdays.chappie.server.service;

import com.chg.hackdays.chappie.model.Message;

import java.util.Collection;
import java.util.List;

public interface MessageService {
    void postMessages(Iterable<Message> map);

    List<Message> getMessages();

    List<Message> getMessages(String topic);

    List<Message> getMessages(String topic,int first);

    List<Message> getMessages(String topic,int first, int count);
}
