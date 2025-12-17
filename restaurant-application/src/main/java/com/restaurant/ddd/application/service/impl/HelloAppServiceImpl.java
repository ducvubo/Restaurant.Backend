package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.service.HelloAppService;
import com.restaurant.ddd.domain.service.HelloDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HelloAppServiceImpl implements HelloAppService {

    @Autowired
    private HelloDomainService helloDomainService;

    @Override
    public String sayHello() {
        log.info("Application Service: sayHello");
        return helloDomainService.getHelloMessage();
    }
}

