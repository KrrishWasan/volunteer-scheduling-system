package com.vss.web;

import com.vss.domain.Booking;
import com.vss.service.BookingService;
import com.vss.service.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /** Status tracking: by reference (?ref=) or by volunteer e-mail (?email=). */
    @GetMapping("/status")
    public String status(@RequestParam(required = false) String ref,
                         @RequestParam(required = false) String email,
                         Model model) {
        if (ref != null && !ref.isBlank()) {
            try {
                Booking booking = bookingService.requireByReference(ref.trim());
                model.addAttribute("bookings", List.of(booking));
                model.addAttribute("ref", ref.trim());
            } catch (ResourceNotFoundException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("bookings", Collections.emptyList());
                model.addAttribute("ref", ref);
            }
        } else if (email != null && !email.isBlank()) {
            model.addAttribute("bookings", bookingService.findByEmail(email.trim()));
            model.addAttribute("email", email.trim());
        }
        return "status";
    }

    @PostMapping("/bookings/{ref}/cancel")
    public String cancel(@PathVariable String ref, RedirectAttributes redirect) {
        try {
            bookingService.cancel(ref);
            redirect.addFlashAttribute("success", "Booking " + ref + " cancelled. The seat is released.");
        } catch (ResourceNotFoundException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/status?ref=" + ref;
    }
}
