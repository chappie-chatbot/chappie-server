package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.ListResponse;
import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.MessagingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${chappie.db.url.base}/${chappie.db.url.message}")
    String messageUrl;

    @Override
    public void post(Message msg) {
        kafkaMessagingProvider.post(msg);
    }

    @Override
    public List<Message> get(String topic, long first, int count) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new HttpHeaders();
        HttpEntity<RasaChatbotProvider.RasaRequest> requestEntity = new HttpEntity<>(headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().uri(URI.create(messageUrl))
                .queryParam("topic", topic)
                .queryParam("start", first)
                .queryParam("count", count);
        ResponseEntity<ListResponse> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, ListResponse.class);
        return (List<Message>) response.getBody().getItems();
    }
}
