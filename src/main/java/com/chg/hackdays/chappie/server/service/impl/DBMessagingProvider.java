package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.ListResponse;
import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.MessagingProvider;
import com.chg.hackdays.chappie.util.DateUtil;
import com.chg.hackdays.chappie.util.StringUtil;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class DBMessagingProvider implements MessagingProvider {
    @Autowired
    KafkaMessagingProvider kafkaMessagingProvider;
    @Autowired
    ModelMapper modelMapper;

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
        return mapMessages(response.getBody().getItems());
    }

    private List<Message> mapMessages(List items) {
        List<Message> result = new LinkedList<>();
        for (Object item : items) {
            result.add(mapMessage((Map<?,?>)item));
        }
        return result;
    }

    private Message mapMessage(Map<?, ?> map) {
        Message message = modelMapper.map(map,Message.class);

        // TODO: Fix ModelMapper so the following mappings are not required

        message.setId(StringUtil.toString(map.get("id")));
        message.setTimestamp(ZonedDateTime.parse(StringUtil.toString(map.get("timestamp")), DateUtil.FORMAT));
        message.addAttributes((Map<String,String>) map.get("attributes"));

        return message;
    }
}
