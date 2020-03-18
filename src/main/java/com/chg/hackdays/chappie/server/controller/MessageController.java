package com.chg.hackdays.chappie.server.controller;

import com.chg.hackdays.chappie.model.ListRequest;
import com.chg.hackdays.chappie.model.ListResponse;
import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.MessageService;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController extends BaseController {
    @Autowired
    MessageService messageService;

    @GetMapping("/message")
    public ResponseEntity<ListResponse> getMessages() {
        return respond(new ListResponse(), resp -> {
            resp.getItems().addAll(messageService.getMessages());
        });
    }

    @PostMapping("/message")
    public ResponseEntity<ListResponse> postMessages(@RequestBody ListRequest<Message> req) {
        return respond(new ListResponse(), resp -> {
            List<Message> messages = modelMapper.map(req.getItems(), new TypeToken<List<Message>>() {}.getType());
            messageService.postMessages(messages);
            resp.getItems().addAll(messages);
        });
    }
}
