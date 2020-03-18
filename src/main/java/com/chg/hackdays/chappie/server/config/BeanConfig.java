package com.chg.hackdays.chappie.server.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public ModelMapper controllerModelMapper(){
        return new ModelMapper();
    }
}
