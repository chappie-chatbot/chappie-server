package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.ChatbotProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class KerasChatbotProvider implements ChatbotProvider {
    private static final String MESSAGE_TOPIC = "chat";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${chatbot.keras.url.base}/${chatbot.keras.url.message}")
    String postMessageUrl;
    @Value("chappie:${chatbot.id}")
    private String messageSource;

    @Override
    public List<Message> exchange(Collection<Message> messages) {
        try {
            List<Message> replies = new LinkedList<>();
            for (Message message : messages) {
                replies.addAll(exchange(message));
            }
            return replies;
        } catch (Exception e) {
            log.error("Failed to query bot", e);
            return Collections.emptyList();
        }
    }

    private List<Message> exchange(Message requestMessage) {
        RestTemplate restTemplate = new RestTemplate();
        MessageRequest request = mapMessageRequest(requestMessage);
        MultiValueMap<String, String> headers = new HttpHeaders();
        HttpEntity<MessageRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(postMessageUrl, HttpMethod.POST, requestEntity, MessageResponse.class);
        return Collections.singletonList(mapMessageResponse(requestMessage, response.getBody()));
    }

    private MessageRequest mapMessageRequest(Message message) {
        MessageRequest result = new MessageRequest();
        result.sender = message.getSource();
        // TODO: Decode if necessary
        result.message = message.getText();
        return result;
    }

    private Message mapMessageResponse(Message request, MessageResponse response) {
        Message result = new Message();

        StringBuilder textBuilder = new StringBuilder();
        Pattern attrPattern = Pattern.compile("\\[[^\\]]*\\]");
        Matcher attrMatcher = attrPattern.matcher(response.getResponse());
        int nextCh = 0;
        while (attrMatcher.find()) {
            String group = attrMatcher.group();
            String attrFullStr = group.substring(1, group.length() - 1);
            String[] attrParts = attrFullStr.split(":", 2);
            result.getAttributes().put(attrParts[0], attrParts[1]);
            textBuilder.append(response.getResponse().substring(nextCh, attrMatcher.start()));
            nextCh = attrMatcher.end();
        }
        textBuilder.append(response.getResponse().substring(nextCh));

        result.setTopic(MESSAGE_TOPIC);
        result.setSource(messageSource);
        result.setTarget(request.getSource());
        result.setText(textBuilder.toString().trim());
        result.setReplyTo(request.getId());
        result.setType("text");
        result.setConversation(request.getConversation());
        return result;
    }

    public static class MessageRequest {
        String sender;
        String message;

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class MessageResponse {
        String intent;
        String probability;
        String response;

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public String getProbability() {
            return probability;
        }

        public void setProbability(String probability) {
            this.probability = probability;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }
}
