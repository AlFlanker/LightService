package com.vvvTeam.yuglightservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MainController {
    /**
    *@return стартовая страничка
    */
    @GetMapping("/")
    public String greeting(Model model) {
        return "greeting";
    }

}
