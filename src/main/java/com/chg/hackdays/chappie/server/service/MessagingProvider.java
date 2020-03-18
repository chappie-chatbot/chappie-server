package com.chg.hackdays.chappie.server.service;

import com.chg.hackdays.chappie.model.Message;

public interface MessagingProvider {
    void post(Message msg);
}
