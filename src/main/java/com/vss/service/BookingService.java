package com.vss.service;

import com.vss.domain.Booking;
import com.vss.domain.BookingStatus;
import com.vss.domain.Slot;
import com.vss.domain.Volunteer;
import com.vss.repo.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingService {

    private static final Set<BookingStatus> ACTIVE =
            EnumSet.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final BookingRepository bookings;
    private final VolunteerService volunteerService;
    private final SlotService slotService;
    private final SecureRandom random = new SecureRandom();

    public BookingService(BookingRepository bookings,
                          VolunteerService volunteerService,
                          SlotService slotService) {
        this.bookings = bookings;
        this.volunteerService = volunteerService;
        this.slotService = slotService;
    }

    @Transactional
    public Booking requestBooking(String email, Long slotId) {
        Volunteer volunteer = volunteerService.requireByEmail(email);
        Slot slot = slotService.requireById(slotId);

        if (bookings.existsBySlotIdAndVolunteerEmailIgnoreCaseAndStatusIn(
                slotId, volunteer.getEmail(), ACTIVE)) {
            throw new DuplicateResourceException("You already have an active booking for this slot.");
        }
        if (slotService.seatsLeft(slotId) <= 0) {
            throw new IllegalStateException("This slot is full. Please choose another slot.");
        }
        String reference = nextReference();
        return bookings.save(new Booking(reference, volunteer, slot));
    }

    @Transactional
    public Booking confirm(String reference) {
        Booking booking = requireByReference(reference);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled booking " + reference + " cannot be confirmed.");
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return booking;
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookings.save(booking);
    }

    @Transactional
    public Booking cancel(String reference) {
        Booking booking = requireByReference(reference);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking " + reference + " is already cancelled.");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return bookings.save(booking);
    }

    @Transactional(readOnly = true)
    public Booking requireByReference(String reference) {
        return bookings.findByReferenceIgnoreCase(reference == null ? "" : reference.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking reference '" + reference + "' not found."));
    }

    @Transactional(readOnly = true)
    public List<Booking> findByEmail(String email) {
        return bookings.findByVolunteerEmailIgnoreCaseOrderByCreatedAtDesc(email.trim());
    }

    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        return bookings.findAllByOrderByCreatedAtDesc();
    }

    private String nextReference() {
        // Human-friendly unique reference, e.g. VSS-7KQ2XA
        String alphabet = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
        for (int attempt = 0; attempt < 20; attempt++) {
            StringBuilder sb = new StringBuilder("VSS-");
            for (int i = 0; i < 6; i++) {
                sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            if (bookings.findByReferenceIgnoreCase(sb.toString()).isEmpty()) {
                return sb.toString();
            }
        }
        throw new IllegalStateException("Could not generate a booking reference, please retry.");
    }
}
