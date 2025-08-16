package com.team3.services;

import com.team3.dtos.offer.OfferCreateDTO;
import com.team3.dtos.offer.OfferDetailDTO;
import com.team3.dtos.offer.OfferEditDTO;
import com.team3.dtos.offer.OfferListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OfferService {
    Page<OfferListDTO> searchAll(String search, String department, String status, Pageable pageable);

    OfferDetailDTO findOfferById(Long id);

    OfferEditDTO findOfferForEdit(Long id);

    void createOffer(OfferCreateDTO offerDto);

    void updateOffer(OfferEditDTO offerDto);

    void deleteOfferById(Long id);
}