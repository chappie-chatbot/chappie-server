package com.chg.hackdays.chappie.server.service;

import com.chg.hackdays.chappie.model.Message;

import java.util.Collection;
import java.util.List;

public interface ChatbotService {
    Collection exchange(List<Message> messages);
}
