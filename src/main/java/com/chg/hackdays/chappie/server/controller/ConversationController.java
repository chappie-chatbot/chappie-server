package com.chg.hackdays.chappie.server.controller;

import com.chg.hackdays.chappie.model.Conversation;
import com.chg.hackdays.chappie.model.ItemResponse;
import com.chg.hackdays.chappie.model.ListResponse;
import com.chg.hackdays.chappie.server.service.ConversationService;
import com.chg.hackdays.chappie.server.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConversationController extends BaseController {
    @Autowired
    ConversationService conversationService;
    @Autowired
    SessionService sessionService;

    @GetMapping("/conversation")
    public ResponseEntity<ListResponse<Conversation>> getConversations(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(name = "participant", required = false) String participant) {
        return respond(new ListResponse(), resp -> {
            resp.getItems().addAll(conversationService.getConversations(id, participant));
        });
    }

    @PostMapping("/conversation/{id}")
    public ResponseEntity<ItemResponse<Conversation>> setConversation(@PathVariable("id") long id) {
        return respond(new ItemResponse(), resp -> {
            List<Conversation> conversations = conversationService.getConversations(id, null);
            sessionService.setCurrentConversationId(id);
            resp.setItem(conversations.isEmpty() ? null : conversations.get(0));
        });
    }

    @PostMapping("/conversation/new")
    public ResponseEntity<ItemResponse<Conversation>> createNewConversation() {
        return respond(new ItemResponse(), resp -> {
            Conversation conversation = conversationService.createConversation();
            sessionService.setCurrentConversationId(conversation.getId());
            resp.setItem(conversation);
        });
    }
}
