package com.chg.hackdays.chappie.server.controller;

import com.chg.hackdays.chappie.model.SessionResponse;
import com.chg.hackdays.chappie.model.User;
import com.chg.hackdays.chappie.server.service.SessionService;
import com.chg.hackdays.chappie.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController extends BaseController {
    @Autowired
    SessionService sessionService;
    @Autowired
    UserService userService;

    @GetMapping("/session")
    public ResponseEntity<SessionResponse> getSession() {
        SessionResponse resp = new SessionResponse();

        resp.setSessionId(sessionService.getSessionId());
        resp.setUserId(sessionService.getUserId());
        resp.setUsername(sessionService.getUsername());
        resp.setCurrentConversationId(sessionService.getCurrentConversationId());

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/session")
    public ResponseEntity<SessionResponse> createSession(@RequestBody SessionResponse session) {
        SessionResponse resp = new SessionResponse();

        if (session.getUsername() != null) {
            User user = userService.getUser(session.getUsername());
            if (user != null) {
                sessionService.setUserId(user.getId());
                sessionService.setUsername(user.getName());
            }
        }
        if (session.getCurrentConversationId() != null) {
            sessionService.setCurrentConversationId(session.getCurrentConversationId());
        }

        return getSession();
    }
}
