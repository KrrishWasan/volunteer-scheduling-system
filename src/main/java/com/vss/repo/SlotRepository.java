package com.vss.repo;

import com.vss.domain.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findAllByOrderBySlotDateAscStartTimeAsc();
}
