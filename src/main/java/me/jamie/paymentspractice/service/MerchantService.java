package me.jamie.paymentspractice.service;

import me.jamie.paymentspractice.domain.model.Merchant;
import me.jamie.paymentspractice.exception.MerchantNotFoundException;
import me.jamie.paymentspractice.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public Merchant createMerchant(String name, String email){
        Merchant merchant = new Merchant(UUID.randomUUID().toString(), name, email);
        return merchantRepository.save(merchant);
    }
    public Merchant getMerchant(String merchantId) throws MerchantNotFoundException {
        return merchantRepository.findById(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException(merchantId));
    }
    public List<Merchant> getAllMerchants(){
        return merchantRepository.findAll();
    }
    public void updateMerchantName(String merchantId, String newName){
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException(merchantId));
        merchant.setName(newName);
        merchantRepository.save(merchant);
    }
    public void deleteMerchant(String merchantId) {
        if (!merchantRepository.existsById(merchantId)) {
            throw new MerchantNotFoundException(merchantId);
        }
        merchantRepository.deleteById(merchantId);
    }
}
