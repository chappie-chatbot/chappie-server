package com.chg.hackdays.chappie.server.controller;

import com.chg.hackdays.chappie.model.ListResponse;
import com.chg.hackdays.chappie.server.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConversationController extends BaseController {
    @Autowired
    ConversationService conversationService;

    @GetMapping("/conversation")
    public ResponseEntity<ListResponse> getConversations(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(name = "participant", required = false) String participant) {
        return respond(new ListResponse(), resp -> {
            resp.getItems().addAll(conversationService.getConversations(id, participant));
        });
    }
}
