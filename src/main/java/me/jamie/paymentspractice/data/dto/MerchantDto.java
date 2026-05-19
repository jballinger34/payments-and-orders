package me.jamie.paymentspractice.data.dto;

import me.jamie.paymentspractice.domain.model.Merchant;

public record MerchantDto(String id, String name, String email) {

    public static MerchantDto from(Merchant merchant){
        return new MerchantDto(merchant.getId(), merchant.getName(), merchant.getEmail());
    }

}
