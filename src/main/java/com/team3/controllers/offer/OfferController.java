package com.team3.controllers.offer;

import com.team3.dtos.offer.OfferCreateDTO;
import com.team3.dtos.offer.OfferEditDTO;
import com.team3.services.CandidateService;
import com.team3.services.OfferService;
import com.team3.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/offers")
public class OfferController {
    @Autowired
    private OfferService offerService;
    @Autowired
    private CandidateService candidateService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String offerList(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String department,
                            @RequestParam(required = false) String status,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        int size = 10;
        var pageable = PageRequest.of(page, size);
        var offerListDTO = offerService.searchAll(search, department, status, pageable);
        model.addAttribute("offers", offerListDTO);
        model.addAttribute("keyword", search);
        model.addAttribute("department", department);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", offerListDTO.getTotalPages());
        model.addAttribute("totalOffers", offerListDTO.getTotalElements());
        return "contents/offer/offer-list";
    }

    @GetMapping("/detail/{id}")
    public String offerDetail(@PathVariable Long id, Model model) {
        model.addAttribute("offer", offerService.findOfferById(id));
        return "contents/offer/offer-detail";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("offer", new OfferCreateDTO());
        // Thêm các danh sách cần thiết cho form
        // model.addAttribute("candidates", candidateService.findAllCandidates());
        // model.addAttribute("managers", userService.getManagers());
        return "contents/offer/offer-create";
    }

    @PostMapping("/create")
    public String createOffer(@Valid @ModelAttribute("offer") OfferCreateDTO offerDto,
                              BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "contents/offer/offer-create";
        }
        offerService.createOffer(offerDto);
        redirectAttributes.addFlashAttribute("message", "Offer created successfully!");
        return "redirect:/offers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("offer", offerService.findOfferForEdit(id));
        // Thêm các danh sách cần thiết cho form
        // model.addAttribute("candidates", candidateService.findAllCandidates());
        // model.addAttribute("managers", userService.getManagers());
        return "contents/offer/offer-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateOffer(@PathVariable Long id, @Valid @ModelAttribute("offer") OfferEditDTO offerDto,
                              BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "contents/offer/offer-edit";
        }
        offerDto.setOfferId(id);
        offerService.updateOffer(offerDto);
        redirectAttributes.addFlashAttribute("message", "Offer updated successfully!");
        return "redirect:/offers/detail/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deleteOffer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            offerService.deleteOfferById(id);
            redirectAttributes.addFlashAttribute("message", "Offer deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting offer: " + e.getMessage());
        }
        return "redirect:/offers";
    }
}