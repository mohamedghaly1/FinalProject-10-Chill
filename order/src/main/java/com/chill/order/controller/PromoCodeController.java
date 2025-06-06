package com.chill.order.controller;

import com.chill.order.model.PromoCode;
import com.chill.order.service.PromoCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/promos")
public class PromoCodeController {
    @Autowired
    PromoCodeService promoCodeService;

    @GetMapping("/{code}")
    public int getDiscountByPromoCode(@PathVariable String code){
        try {
            return promoCodeService.getDiscountByPromoCode(code);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping
    public ResponseEntity<PromoCode> addPromoCode(@RequestBody PromoCode promoCode) {
        if (promoCode.getCode() == null || promoCode.getDiscount() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid PromoCode data!");
        }
        PromoCode savedPromoCode = promoCodeService.addPromoCode(promoCode);
        return new ResponseEntity<>(savedPromoCode, HttpStatus.CREATED);
    }

    @PutMapping
    public PromoCode updatePromoCode (@RequestBody PromoCode promoCode){
        return promoCodeService.updatePromoCode(promoCode);
    }

    @DeleteMapping
    public void deletePromoCode (@RequestBody PromoCode promoCode){
        promoCodeService.deletePromoCode(promoCode.getCode());
    }




}
