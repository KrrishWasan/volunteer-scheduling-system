package com.vss.repo;

import com.vss.domain.Booking;
import com.vss.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByReferenceIgnoreCase(String reference);

    List<Booking> findByVolunteerEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countBySlotIdAndStatusIn(Long slotId, Collection<BookingStatus> statuses);

    boolean existsBySlotIdAndVolunteerEmailIgnoreCaseAndStatusIn(
            Long slotId, String email, Collection<BookingStatus> statuses);
}
