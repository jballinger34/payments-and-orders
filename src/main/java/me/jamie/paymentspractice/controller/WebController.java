package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.data.dto.ProductDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebController {

    @GetMapping("/browse")
    public String browse(){
        return "browse";
    }

}
