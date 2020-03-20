package com.chg.hackdays.chappie.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProviderInfoService {

  @Value("${timeentry.url.base}/${timeentry.url.providerInfo}")
  String providerInfoUrl;


  public String getProviderInfo(String providerNumber) {

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(providerInfoUrl)
        .queryParam("providerNumber", providerNumber);
    HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
    ResponseEntity<String> responseEntity = new RestTemplate()
        .exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, String.class);

    return responseEntity.getBody();

  }
}
