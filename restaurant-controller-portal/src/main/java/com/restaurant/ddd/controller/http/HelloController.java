package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.service.HelloAppService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portal/hello")
@Tag(name = "Hello (Portal)", description = "Portal test API endpoint")
public class HelloController {

    @Autowired
    private HelloAppService helloAppService;

    @Operation(summary = "Hello endpoint", description = "A simple test endpoint to verify the portal API is working")
    @GetMapping
    public ResultMessage<String> hello() {
        return ResultUtil.data(helloAppService.sayHello());
    }
}

