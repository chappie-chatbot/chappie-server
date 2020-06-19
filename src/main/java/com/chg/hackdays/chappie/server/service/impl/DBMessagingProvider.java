package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Conversation;
import com.chg.hackdays.chappie.model.ItemResponse;
import com.chg.hackdays.chappie.model.ListResponse;
import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.MessagingProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Repository
public class DBMessagingProvider implements MessagingProvider {
    @Autowired
    KafkaMessagingProvider kafkaMessagingProvider;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    RestTemplate restTemplate;

    @Value("${chappie.db.url.base}/${chappie.db.url.message}")
    String messageUrl;
    @Value("${chappie.db.url.base}/${chappie.db.url.conversation}")
    String conversationUrl;

    @Override
    public void post(Message msg) {
        kafkaMessagingProvider.post(msg);
    }

    @Override
    public List<Message> get(String topic, long first, int count) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        HttpEntity<RasaChatbotProvider.RasaRequest> requestEntity = new HttpEntity<>(headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().uri(URI.create(messageUrl))
                .queryParam("topic", topic)
                .queryParam("start", first)
                .queryParam("count", count);
        ResponseEntity<ListResponse<Message>> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<ListResponse<Message>>() {
        });
        return response.getBody().getItems();
    }

    @Override
    public List<Message> getByConversation(String topic, long conversationId, long first, int count) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        HttpEntity<RasaChatbotProvider.RasaRequest> requestEntity = new HttpEntity<>(headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().uri(URI.create(messageUrl))
                .queryParam("topic", topic)
                .queryParam("conversation", conversationId)
                .queryParam("start", first)
                .queryParam("count", count);
        ResponseEntity<ListResponse<Message>> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<ListResponse<Message>>() {
        });
        return response.getBody().getItems();
    }

    @Override
    public List<Conversation> getConversations(Long id, String participant) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        HttpEntity<RasaChatbotProvider.RasaRequest> requestEntity = new HttpEntity<>(headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().uri(URI.create(conversationUrl));
        if (id != null)
            uriBuilder.queryParam("id", id);
        if (participant != null)
            uriBuilder.queryParam("participant", participant);
        ResponseEntity<ListResponse<Conversation>> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<ListResponse<Conversation>>() {
        });
        return response.getBody().getItems();
    }

    @Override
    public Conversation createConversation() {
        MultiValueMap<String, String> headers = new HttpHeaders();
        HttpEntity<RasaChatbotProvider.RasaRequest> requestEntity = new HttpEntity<>(headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().uri(URI.create(conversationUrl));
        ResponseEntity<ItemResponse<Conversation>> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, requestEntity, new ParameterizedTypeReference<ItemResponse<Conversation>>() {
        });
        return response.getBody().getItem();
    }
}
