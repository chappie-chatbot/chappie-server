package com.chg.hackdays.chappie.server.controller;

import com.chg.hackdays.chappie.function.ConsumerThrowsException;
import com.chg.hackdays.chappie.model.Response;
import com.chg.hackdays.chappie.model.ResponseStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("controllerModelMapper")
    protected ModelMapper modelMapper;

    protected <T extends Response> ResponseEntity<T> failure(Exception e, T resp) {
        resp.setStatus(ResponseStatus.error(e));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }

    protected <T extends Response> ResponseEntity<T> respond(T response, ConsumerThrowsException<T> responder){
        try {
            responder.accept(response);
            return ResponseEntity.ok(response);
        }catch(Exception e){
            log.error("Exception occurred processing request "+getRequestUrl(), e);
            return failure(e, response);
        }
    }

    private String getRequestUrl() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}
