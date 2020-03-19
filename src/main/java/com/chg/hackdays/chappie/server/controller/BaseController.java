package com.chg.hackdays.chappie.server.controller;

import com.chg.hackdays.chappie.function.ConsumerThrowsException;
import com.chg.hackdays.chappie.function.ProducerThrowsException;
import com.chg.hackdays.chappie.model.HasContentType;
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
import javax.servlet.http.HttpServletResponse;

public class BaseController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("controllerModelMapper")
    protected ModelMapper modelMapper;

    protected <T> ResponseEntity<T> failure(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    protected <T extends Response> ResponseEntity<T> failure(Exception e, T resp) {
        resp.setStatus(ResponseStatus.error(e));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }

    protected <T> ResponseEntity<T> respond(ProducerThrowsException<T> responder){
        try {
            return ok(responder.get());
        }catch(Exception e){
            log.error("Exception occurred processing request "+getRequestUrl(), e);
            return failure(e);
        }
    }

    protected <T extends Response> ResponseEntity<T> respond(T response, ConsumerThrowsException<T> responder){
        try {
            responder.accept(response);
            return ok(response);
        }catch(Exception e){
            log.error("Exception occurred processing request "+getRequestUrl(), e);
            return failure(e, response);
        }
    }

    private <T>ResponseEntity<T> ok(T body){
        ResponseEntity<T> responseEntity = ResponseEntity.ok(body);
        if(body instanceof HasContentType){
            responseEntity.getHeaders().set("Content-Type", ((HasContentType) body).getContentType());
        }
        return responseEntity;
    }

    private String getRequestUrl() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}
