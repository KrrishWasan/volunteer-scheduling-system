package com.vss;

import com.vss.domain.Booking;
import com.vss.domain.BookingStatus;
import com.vss.domain.Slot;
import com.vss.repo.SlotRepository;
import com.vss.service.BookingService;
import com.vss.service.DuplicateResourceException;
import com.vss.service.ResourceNotFoundException;
import com.vss.service.VolunteerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookings;

    @Autowired
    private VolunteerService volunteers;

    @Autowired
    private SlotRepository slots;

    private Slot saveSlot(int capacity) {
        return slots.save(new Slot("Test Shift", "Test Event", "Hall",
                LocalDate.now().plusDays(5), LocalTime.of(9, 0), LocalTime.of(11, 0),
                capacity, "desc"));
    }

    @Test
    void fullBookingLifecycle() {
        volunteers.register("Asha", "asha@example.com", "9876543210", "");
        Slot slot = saveSlot(5);

        Booking booking = bookings.requestBooking("asha@example.com", slot.getId());
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getReference()).startsWith("VSS-");

        assertThat(bookings.requireByReference(booking.getReference()).getStatus())
                .isEqualTo(BookingStatus.PENDING);

        bookings.confirm(booking.getReference());
        assertThat(bookings.requireByReference(booking.getReference()).getStatus())
                .isEqualTo(BookingStatus.CONFIRMED);

        bookings.cancel(booking.getReference());
        assertThat(bookings.requireByReference(booking.getReference()).getStatus())
                .isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void duplicateActiveBookingIsRejected() {
        volunteers.register("Dev", "dev@example.com", "9876543211", "");
        Slot slot = saveSlot(5);
        bookings.requestBooking("dev@example.com", slot.getId());

        assertThatThrownBy(() -> bookings.requestBooking("dev@example.com", slot.getId()))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void capacityIsNeverExceeded() {
        Slot slot = saveSlot(1);
        volunteers.register("One", "one@example.com", "9000000001", "");
        volunteers.register("Two", "two@example.com", "9000000002", "");
        bookings.requestBooking("one@example.com", slot.getId());

        assertThatThrownBy(() -> bookings.requestBooking("two@example.com", slot.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("full");
    }

    @Test
    void unregisteredEmailCannotBook() {
        Slot slot = saveSlot(5);
        assertThatThrownBy(() -> bookings.requestBooking("ghost@example.com", slot.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void cancelledBookingCannotBeConfirmed() {
        volunteers.register("Mia", "mia@example.com", "9000000003", "");
        Slot slot = saveSlot(5);
        Booking booking = bookings.requestBooking("mia@example.com", slot.getId());
        bookings.cancel(booking.getReference());

        assertThatThrownBy(() -> bookings.confirm(booking.getReference()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void doubleCancelIsRejected() {
        volunteers.register("Raj", "raj@example.com", "9000000004", "");
        Slot slot = saveSlot(5);
        Booking booking = bookings.requestBooking("raj@example.com", slot.getId());
        bookings.cancel(booking.getReference());

        assertThatThrownBy(() -> bookings.cancel(booking.getReference()))
                .isInstanceOf(IllegalStateException.class);
    }
}
