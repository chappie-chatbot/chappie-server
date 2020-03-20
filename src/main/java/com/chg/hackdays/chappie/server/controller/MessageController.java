package com.chg.hackdays.chappie.server.controller;

import com.chg.hackdays.chappie.model.ListRequest;
import com.chg.hackdays.chappie.model.ListResponse;
import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.model.MessageId;
import com.chg.hackdays.chappie.server.service.ChatbotService;
import com.chg.hackdays.chappie.server.service.MessageService;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.stream.Collectors;

@RestController
public class MessageController extends BaseController {
    @Autowired
    MessageService messageService;

    @GetMapping("/message")
    public ResponseEntity<ListResponse> getMessages(
            @RequestParam("topic") String topic,
            @RequestParam(name = "start", required = false) int start) {
        return respond(new ListResponse(), resp -> {
            resp.getItems().addAll(messageService.getMessages(topic, start));
        });
    }

    @GetMapping("/message/{ids}")
    public ResponseEntity<ListResponse> getMessagesById(@PathVariable("ids") String idsStr) {
        return respond(new ListResponse(), resp -> {
            List<String> ids = Arrays.asList(idsStr.split("[,;\\s]+")).stream().collect(Collectors.toList());
            if(ids.size() > 1)
                throw new UnsupportedOperationException("TODO: Support multiple IDs");
            resp.getItems().add(messageService.getMessage(new MessageId(ids.iterator().next())));
        });
    }

    @GetMapping("/message/{id}/content")
    public void getMessageContent(HttpServletResponse response, @PathVariable("id") String idStr) {
        Message message = messageService.getMessage(new MessageId(idStr));
        response.setContentType(message.getMime());
        String type = message.getType();
        if(type==null)
            type= "text";
        try(OutputStream os = response.getOutputStream()){
            switch (type.toLowerCase()) {
                case "base64":
                    os.write(Base64.getDecoder().decode(message.getText()));
                    break;
                case "text":
                    os.write(message.getText().getBytes());
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported message type: "+type);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
