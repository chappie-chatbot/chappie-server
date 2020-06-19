package com.chg.hackdays.chappie.server.service;

import com.chg.hackdays.chappie.model.Conversation;

import java.util.Collection;
import java.util.List;

public interface ConversationService {
    List<Conversation> getConversations(Long id, String participant);

    Conversation createConversation();
}
