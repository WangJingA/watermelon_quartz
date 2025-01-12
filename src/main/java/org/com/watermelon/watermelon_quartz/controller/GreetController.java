package org.com.watermelon.watermelon_quartz.controller;

import org.com.watermelon.watermelon_quartz.response.ServerResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quartz")
public class GreetController {
    @GetMapping("/greet")
    public ServerResponseEntity greet(){
        return ServerResponseEntity.success("watermelon quartz is running!");
    }
}
