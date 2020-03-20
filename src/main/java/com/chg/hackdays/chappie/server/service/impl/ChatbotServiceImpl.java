package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.ChatbotService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

@Service
public class ChatbotServiceImpl implements ChatbotService {
    @Override
    public List<Message> exchange(List<Message> messages) {
        List<Message> responses = new LinkedList<>();
        if(!CollectionUtils.isEmpty(messages)) {
            Message mockResponse = new Message();
            mockResponse.setType("text");
            mockResponse.setSource("mock");
            mockResponse.setReplyTo(messages.iterator().next().getId());
            mockResponse.setTarget((messages.iterator().next().getSource()));
            mockResponse.setText("Got it!");
            responses.add(mockResponse);
        }
        return responses;
    }
}
