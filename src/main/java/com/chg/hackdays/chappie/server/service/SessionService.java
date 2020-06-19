package com.chg.hackdays.chappie.server.service;

public interface SessionService {
    String getSessionId();

    Long getCurrentConversationId();

    void setCurrentConversationId(long conversationId);

    void setUsername(String username);

    String getUsername();

    void setUserId(long id);

    Long getUserId();
}
