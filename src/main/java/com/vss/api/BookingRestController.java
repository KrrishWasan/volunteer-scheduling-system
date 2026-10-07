package com.vss.api;

import com.vss.domain.Booking;
import com.vss.service.BookingService;
import com.vss.service.DuplicateResourceException;
import com.vss.service.ResourceNotFoundException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingRestController {

    private final BookingService bookingService;

    public BookingRestController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Map<String, Object>> all(@RequestParam(required = false) String email) {
        List<Booking> bookings = (email == null || email.isBlank())
                ? bookingService.findAll() : bookingService.findByEmail(email);
        return bookings.stream().map(this::toJson).toList();
    }

    @GetMapping("/{ref}")
    public ResponseEntity<?> byReference(@PathVariable("ref") String ref) {
        try {
            return ResponseEntity.ok(toJson(bookingService.requireByReference(ref)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookingRequest request) {
        if (request.email() == null || request.email().isBlank()
                || request.slotId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Both 'email' and 'slotId' are required."));
        }
        try {
            Booking saved = bookingService.requestBooking(request.email().trim(), request.slotId());
            return ResponseEntity.status(HttpStatus.CREATED).body(toJson(saved));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (DuplicateResourceException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{ref}/confirm")
    public ResponseEntity<?> confirm(@PathVariable("ref") String ref) {
        try {
            return ResponseEntity.ok(toJson(bookingService.confirm(ref)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{ref}/cancel")
    public ResponseEntity<?> cancel(@PathVariable("ref") String ref) {
        try {
            return ResponseEntity.ok(toJson(bookingService.cancel(ref)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> toJson(Booking b) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("reference", b.getReference());
        json.put("status", b.getStatus().name());
        json.put("volunteerEmail", b.getVolunteer().getEmail());
        json.put("volunteerName", b.getVolunteer().getFullName());
        json.put("slotId", b.getSlot().getId());
        json.put("slotTitle", b.getSlot().getTitle());
        return json;
    }

    public record BookingRequest(String email, Long slotId) {
    }
}
