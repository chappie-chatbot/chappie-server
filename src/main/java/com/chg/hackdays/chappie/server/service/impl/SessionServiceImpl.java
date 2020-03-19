package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.server.service.SessionService;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {
    @Override
    public int getCurrentConversationId() {
        return 1;
    }

    @Override
    public String getUsername() {
        return "todo";
    }
}
