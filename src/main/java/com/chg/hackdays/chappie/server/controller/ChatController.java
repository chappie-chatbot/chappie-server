package com.chg.hackdays.chappie.server.controller;

import com.chg.hackdays.chappie.model.ListRequest;
import com.chg.hackdays.chappie.model.ListResponse;
import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.ChatbotService;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatController extends BaseController{
    @Autowired
    ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ListResponse> postMessages(@RequestBody ListRequest<Message> req) {
        return respond(new ListResponse(), resp -> {
            List<Message> messages = modelMapper.map(req.getItems(), new TypeToken<List<Message>>() {}.getType());
            resp.getItems().addAll(chatbotService.exchange(messages));
        });
    }
}
