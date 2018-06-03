package pl.jakubmichalowski.kainos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Jakub Michałowski on 30.05.2018.
 * All rights reserved.
 */
@Controller
public class MainController {
    @RequestMapping("/")
    public String main(){
        return "index";
    }
}
