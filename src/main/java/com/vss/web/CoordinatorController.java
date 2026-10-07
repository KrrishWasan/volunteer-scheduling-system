package com.vss.web;

import com.vss.domain.Slot;
import com.vss.service.BookingService;
import com.vss.service.ResourceNotFoundException;
import com.vss.service.SlotService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Minimal coordinator console (demo: no login in MVP scope). */
@Controller
public class CoordinatorController {

    private final BookingService bookingService;
    private final SlotService slotService;

    public CoordinatorController(BookingService bookingService, SlotService slotService) {
        this.bookingService = bookingService;
        this.slotService = slotService;
    }

    @GetMapping("/coordinator")
    public String dashboard(Model model) {
        model.addAttribute("bookings", bookingService.findAll());
        if (!model.containsAttribute("slot")) {
            model.addAttribute("slot", new Slot());
        }
        model.addAttribute("slots", slotService.findAllOrdered());
        return "coordinator";
    }

    @PostMapping("/coordinator/bookings/{ref}/confirm")
    public String confirm(@PathVariable String ref, RedirectAttributes redirect) {
        try {
            bookingService.confirm(ref);
            redirect.addFlashAttribute("success", "Booking " + ref + " confirmed.");
        } catch (ResourceNotFoundException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinator";
    }

    @PostMapping("/coordinator/bookings/{ref}/cancel")
    public String cancel(@PathVariable String ref, RedirectAttributes redirect) {
        try {
            bookingService.cancel(ref);
            redirect.addFlashAttribute("success", "Booking " + ref + " cancelled.");
        } catch (ResourceNotFoundException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinator";
    }

    @PostMapping("/coordinator/slots")
    public String createSlot(@Valid @ModelAttribute("slot") Slot slot,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookings", bookingService.findAll());
            model.addAttribute("slots", slotService.findAllOrdered());
            return "coordinator";
        }
        slotService.create(slot);
        redirect.addFlashAttribute("success", "Slot '" + slot.getTitle() + "' created.");
        return "redirect:/coordinator";
    }
}
