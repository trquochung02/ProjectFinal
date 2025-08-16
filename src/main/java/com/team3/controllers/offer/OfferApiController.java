package com.team3.controllers.offer;

import com.team3.dtos.offer.OfferCreateDTO;
import com.team3.dtos.offer.OfferEditDTO;
import com.team3.services.OfferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/offers")
public class OfferApiController {

    @Autowired
    private OfferService offerService;

    @PostMapping("/create")
    public ResponseEntity<?> createOffer(@Valid @ModelAttribute OfferCreateDTO offerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errors);
        }
        offerService.createOffer(offerDto);
        return ResponseEntity.ok("Offer created successfully!");
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateOffer(@Valid @ModelAttribute OfferEditDTO offerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errors);
        }
        offerService.updateOffer(offerDto);
        return ResponseEntity.ok("Offer updated successfully!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable Long id) {
        try {
            offerService.deleteOfferById(id);
            return ResponseEntity.ok("Offer deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}