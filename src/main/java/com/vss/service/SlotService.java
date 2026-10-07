package com.vss.service;

import com.vss.domain.BookingStatus;
import com.vss.domain.Slot;
import com.vss.repo.BookingRepository;
import com.vss.repo.SlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@Service
public class SlotService {

    private final SlotRepository slots;
    private final BookingRepository bookings;

    public SlotService(SlotRepository slots, BookingRepository bookings) {
        this.slots = slots;
        this.bookings = bookings;
    }

    @Transactional
    public Slot create(Slot slot) {
        return slots.save(slot);
    }

    @Transactional(readOnly = true)
    public List<Slot> findAllOrdered() {
        return slots.findAllByOrderBySlotDateAscStartTimeAsc();
    }

    @Transactional(readOnly = true)
    public Slot requireById(Long id) {
        return slots.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot #" + id + " not found."));
    }

    /** Seats still free = capacity minus PENDING and CONFIRMED bookings. Never negative. */
    @Transactional(readOnly = true)
    public int seatsLeft(Long slotId) {
        Slot slot = requireById(slotId);
        long taken = bookings.countBySlotIdAndStatusIn(slotId,
                Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED));
        return Math.max(0, slot.getCapacity() - (int) taken);
    }

    @Transactional(readOnly = true)
    public boolean isFull(Long slotId) {
        return seatsLeft(slotId) <= 0;
    }
}
