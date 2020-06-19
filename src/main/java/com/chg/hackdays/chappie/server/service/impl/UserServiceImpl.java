package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.ListResponse;
import com.chg.hackdays.chappie.model.User;
import com.chg.hackdays.chappie.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    RestTemplate restTemplate;

    @Value("${chappie.db.url.base}/${chappie.db.url.user}")
    String userUrl;

    @Override
    public User getUser(String userName) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        HttpEntity<RasaChatbotProvider.RasaRequest> requestEntity = new HttpEntity<>(headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().uri(URI.create(userUrl))
                .queryParam("name", userName);
        ResponseEntity<ListResponse<User>> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET,
                requestEntity, new ParameterizedTypeReference<ListResponse<User>>() {
                });
        List<User> items = response.getBody().getItems();
        if (items.isEmpty()) {
            return null;
        }
        return items.get(0);
    }
}
