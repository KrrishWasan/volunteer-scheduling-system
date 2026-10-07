package com.vss.api;

import com.vss.domain.Slot;
import com.vss.service.SlotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/slots")
public class SlotRestController {

    private final SlotService slotService;

    public SlotRestController(SlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping
    public List<Map<String, Object>> all() {
        return slotService.findAllOrdered().stream().map(slot -> {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("id", slot.getId());
            json.put("title", slot.getTitle());
            json.put("eventName", slot.getEventName());
            json.put("location", slot.getLocation());
            json.put("slotDate", slot.getSlotDate().toString());
            json.put("startTime", slot.getStartTime().toString());
            json.put("endTime", slot.getEndTime().toString());
            json.put("capacity", slot.getCapacity());
            json.put("seatsLeft", slotService.seatsLeft(slot.getId()));
            return json;
        }).toList();
    }

    @PostMapping
    public ResponseEntity<Slot> create(@Valid @RequestBody Slot slot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(slotService.create(slot));
    }
}
