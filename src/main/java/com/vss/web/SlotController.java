package com.vss.web;

import com.vss.domain.Slot;
import com.vss.service.BookingService;
import com.vss.service.DuplicateResourceException;
import com.vss.service.ResourceNotFoundException;
import com.vss.service.SlotService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SlotController {

    private final SlotService slotService;
    private final BookingService bookingService;

    public SlotController(SlotService slotService, BookingService bookingService) {
        this.slotService = slotService;
        this.bookingService = bookingService;
    }

    /** Availability / slot view. */
    @GetMapping("/slots")
    public String listSlots(Model model) {
        List<Slot> slots = slotService.findAllOrdered();
        Map<Long, Integer> seatsLeft = new LinkedHashMap<>();
        for (Slot slot : slots) {
            seatsLeft.put(slot.getId(), slotService.seatsLeft(slot.getId()));
        }
        model.addAttribute("slots", slots);
        model.addAttribute("seatsLeft", seatsLeft);
        return "slots";
    }

    @GetMapping("/slots/{id}/book")
    public String bookingForm(@PathVariable Long id, Model model) {
        try {
            Slot slot = slotService.requireById(id);
            model.addAttribute("slot", slot);
            model.addAttribute("seatsLeft", slotService.seatsLeft(id));
            return "book";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/slots";
        }
    }

    @PostMapping("/slots/{id}/book")
    public String requestBooking(@PathVariable Long id,
                                 @RequestParam @NotBlank(message = "Email is required")
                                 @Email(message = "Enter a valid email address") String email,
                                 Model model,
                                 RedirectAttributes redirect) {
        if (email == null || email.isBlank()) {
            model.addAttribute("error", "Email is required.");
            return bookingForm(id, model);
        }
        try {
            var booking = bookingService.requestBooking(email.trim(), id);
            redirect.addFlashAttribute("success",
                    "Booking request placed! Reference: " + booking.getReference()
                            + " (status PENDING). Save it to track your status.");
            redirect.addFlashAttribute("reference", booking.getReference());
            return "redirect:/status?ref=" + booking.getReference();
        } catch (ResourceNotFoundException | DuplicateResourceException
                | IllegalStateException e) {
            Slot slot = slotService.requireById(id);
            model.addAttribute("slot", slot);
            model.addAttribute("seatsLeft", slotService.seatsLeft(id));
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "book";
        }
    }
}
