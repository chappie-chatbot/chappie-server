package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.MessagingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class KafkaMessagingProvider implements MessagingProvider {
    @Autowired
    private KafkaTemplate<String, Message> template;

    @Override
    public void post(Message msg) {
        this.template.send(msg.getTopic(), msg);
    }
}
