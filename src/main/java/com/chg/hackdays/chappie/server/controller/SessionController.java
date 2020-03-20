package com.chg.hackdays.chappie.server.controller;

import com.chg.hackdays.chappie.model.SessionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController extends BaseController{
    @GetMapping("/session")
    public ResponseEntity<SessionResponse> getSession(){
        SessionResponse resp = new SessionResponse();

        return ResponseEntity.ok(resp);
    }
}
