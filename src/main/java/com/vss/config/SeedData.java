package com.vss.config;

import com.vss.domain.Slot;
import com.vss.repo.SlotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class SeedData {

    @Bean
    CommandLineRunner seedSlots(SlotRepository slots,
                                @Value("${app.seed-data:true}") boolean seed) {
        return args -> {
            if (!seed || slots.count() > 0) {
                return;
            }
            LocalDate base = LocalDate.now().plusDays(1);
            slots.save(new Slot("Morning Food Drive", "City Food Bank", "Community Hall A",
                    base, LocalTime.of(9, 0), LocalTime.of(12, 0), 10,
                    "Help sort and pack food parcels for weekend distribution."));
            slots.save(new Slot("Beach Clean-up", "Green Earth NGO", "Juhu Beach Gate 3",
                    base.plusDays(1), LocalTime.of(7, 0), LocalTime.of(10, 0), 25,
                    "Bring gloves; bags and water are provided."));
            slots.save(new Slot("Evening Teaching Support", "LearnForAll", "Municipal School, Room 4",
                    base.plusDays(2), LocalTime.of(16, 0), LocalTime.of(18, 0), 6,
                    "Assist primary-school students with reading and maths."));
            slots.save(new Slot("Blood Donation Camp Helpdesk", "Red Cross Chapter", "City Hospital Lobby",
                    base.plusDays(3), LocalTime.of(10, 0), LocalTime.of(14, 0), 8,
                    "Manage registrations and donor queue at the helpdesk."));
        };
    }
}
