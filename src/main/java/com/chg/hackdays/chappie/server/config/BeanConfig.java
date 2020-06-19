package com.chg.hackdays.chappie.server.config;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.util.DateUtil;
import com.chg.hackdays.chappie.util.StringUtil;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Map;

@Configuration
public class BeanConfig {
    private final ModelMapper controllerModelMapper = new ModelMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Bean
    public ModelMapper controllerModelMapper(){
        return controllerModelMapper;
    }

    @Bean
    public RestTemplate restTemplate(){
        return restTemplate;
    }
}
