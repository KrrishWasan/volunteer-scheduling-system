package com.vss.api;

import com.vss.domain.Volunteer;
import com.vss.service.DuplicateResourceException;
import com.vss.service.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerRestController {

    private final VolunteerService volunteerService;

    public VolunteerRestController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping
    public List<Map<String, Object>> all() {
        return volunteerService.findAll().stream().map(this::toJson).toList();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody VolunteerRequest request) {
        try {
            Volunteer saved = volunteerService.register(
                    request.fullName(), request.email(), request.phone(), request.skills());
            return ResponseEntity.status(HttpStatus.CREATED).body(toJson(saved));
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> toJson(Volunteer v) {
        return Map.of(
                "id", v.getId(),
                "fullName", v.getFullName(),
                "email", v.getEmail(),
                "phone", v.getPhone(),
                "skills", v.getSkills() == null ? "" : v.getSkills());
    }

    public record VolunteerRequest(
            @jakarta.validation.constraints.NotBlank String fullName,
            @jakarta.validation.constraints.Email @jakarta.validation.constraints.NotBlank String email,
            @jakarta.validation.constraints.NotBlank String phone,
            String skills) {
    }
}
