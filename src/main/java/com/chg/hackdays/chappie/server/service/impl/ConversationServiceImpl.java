package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Conversation;
import com.chg.hackdays.chappie.server.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {
    @Autowired
    DBMessagingProvider messagingProvider;

    @Override
    public List<Conversation> getConversations(Long id, String participant) {
        return messagingProvider.getConversations(id, participant);
    }

    @Override
    public Conversation createConversation() {
        return messagingProvider.createConversation();
    }
}
