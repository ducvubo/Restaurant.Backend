package com.restaurant.ddd.domain.service.impl;

import com.restaurant.ddd.domain.service.HelloDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HelloDomainServiceImpl implements HelloDomainService {

    @Override
    public String getHelloMessage() {
        log.info("Domain Service: getHelloMessage");
        return "Hello from Restaurant Backend!";
    }
}

