package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.server.service.SessionService;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {
    @Override
    public Long getCurrentConversationId() {
        return 1L;
    }

    @Override
    public String getUsername() {
        return "todo";
    }
}
