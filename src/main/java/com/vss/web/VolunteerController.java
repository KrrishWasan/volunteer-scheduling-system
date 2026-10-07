package com.vss.web;

import com.vss.domain.Volunteer;
import com.vss.service.DuplicateResourceException;
import com.vss.service.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("volunteer", new VolunteerForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("volunteer") VolunteerForm form,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            Volunteer saved = volunteerService.register(
                    form.getFullName(), form.getEmail(), form.getPhone(), form.getSkills());
            redirect.addFlashAttribute("success",
                    "Registration successful for " + saved.getEmail() + ". Now pick a slot to book.");
            return "redirect:/slots";
        } catch (DuplicateResourceException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
