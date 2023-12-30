package com.auth.MainPage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class PageController {

    @GetMapping()
    public String sayHello() {
        System.out.println("was here");
        return "index";
    }
}
