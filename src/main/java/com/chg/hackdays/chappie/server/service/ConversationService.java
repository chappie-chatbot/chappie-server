package com.chg.hackdays.chappie.server.service;

import java.util.Collection;

public interface ConversationService {
    Collection getConversations(Long id, String participant);
}
