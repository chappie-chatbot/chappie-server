package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.ChatbotProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
public class RasaChatbotProvider implements ChatbotProvider {
    private static final String MESSAGE_TOPIC = "chat";

    @Value("${chatbot.rasa.url.base}/${chatbot.rasa.url.webhook}")
    String webhookUrl;
    @Value("chappie:${chatbot.id}")
    private String messageSource;

    @Override
    public List<Message> exchange(Collection<Message> messages) {
        List<Message> replies = new LinkedList<>();
        for (Message message : messages) {
            replies.addAll(exchange(message));
        }
        return replies;
    }

    private List<Message> exchange(Message requestMessage) {
        RestTemplate restTemplate = new RestTemplate();
        RasaRequest request = mapRasaRequest(requestMessage);
        MultiValueMap<String, String> headers = new HttpHeaders();
        HttpEntity<RasaRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<RasaResponse[]> response = restTemplate.exchange(webhookUrl, HttpMethod.POST, requestEntity, RasaResponse[].class);
        return mapRasaResponse(requestMessage, Arrays.asList(response.getBody()));
    }

    private List<Message> mapRasaResponse(Message request, List<RasaResponse> response) {
        return response.stream().map(resp->mapRasaResponse(request,resp)).collect(Collectors.toList());
    }

    private Message mapRasaResponse(Message request, RasaResponse response) {
        Message result = new Message();

        StringBuilder textBuilder = new StringBuilder();
        Pattern attrPattern = Pattern.compile("\\[[^\\]]*\\]");
        Matcher attrMatcher = attrPattern.matcher(response.getText());
        int nextCh = 0;
        while(attrMatcher.find()){
            String group = attrMatcher.group();
            String attrFullStr = group.substring(1, group.length()-1);
            String[] attrParts = attrFullStr.split(":",2);
            result.getAttributes().put(attrParts[0],attrParts[1]);
            textBuilder.append(response.getText().substring(nextCh,attrMatcher.start()));
            nextCh = attrMatcher.end();
        }
        textBuilder.append(response.getText().substring(nextCh));

        result.setTopic(MESSAGE_TOPIC);
        result.setSource(messageSource);
        result.setTarget(request.getSource());
        result.setText(textBuilder.toString().trim());
        result.setReplyTo(request.getId());
        result.setType("text");
        result.setConversation(request.getConversation());
        return result;
    }

    private RasaRequest mapRasaRequest(Message message) {
        RasaRequest result = new RasaRequest();
        result.sender = message.getSource();
        // TODO: Decode if necessary
        result.message = message.getText();
        return result;
    }

    public static class RasaRequest{
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

    public static class RasaResponse{
        String recipient_id;
        String text;
        String image;

        public String getRecipient_id() {
            return recipient_id;
        }

        public void setRecipient_id(String recipient_id) {
            this.recipient_id = recipient_id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
