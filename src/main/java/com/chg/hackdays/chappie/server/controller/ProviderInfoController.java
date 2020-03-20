package com.chg.hackdays.chappie.server.controller;

import com.chg.hackdays.chappie.server.service.ProviderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProviderInfoController {

  @Autowired
  ProviderInfoService providerInfoService;

  @CrossOrigin
  @GetMapping(value = "/getProviderInfo")
  public ResponseEntity<String> getProviderInfo(
      @RequestParam("providerNumber") String providerNumber) throws Exception {

    System.out.println("provider number " + providerNumber);

    String providerInfo = providerInfoService.getProviderInfo(providerNumber);
    return new ResponseEntity<>(providerInfo, HttpStatus.OK);
  }
}
