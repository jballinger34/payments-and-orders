package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.data.dto.MerchantDto;
import me.jamie.paymentspractice.data.request.MerchantRequest;
import me.jamie.paymentspractice.service.MerchantService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping("/signup")
    public MerchantDto createMerchant(@RequestBody MerchantRequest request){
        return MerchantDto.from(merchantService.createMerchant(request.name(), request.email()));
    }


}
