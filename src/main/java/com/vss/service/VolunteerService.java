package com.vss.service;

import com.vss.domain.Volunteer;
import com.vss.repo.VolunteerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteers;

    public VolunteerService(VolunteerRepository volunteers) {
        this.volunteers = volunteers;
    }

    @Transactional
    public Volunteer register(String fullName, String email, String phone, String skills) {
        String normalizedEmail = email == null ? null : email.trim();
        if (normalizedEmail != null && volunteers.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateResourceException("Email '" + normalizedEmail + "' is already registered.");
        }
        Volunteer volunteer = new Volunteer(
                fullName == null ? null : fullName.trim(),
                normalizedEmail, phone == null ? null : phone.trim(),
                skills == null ? null : skills.trim());
        return volunteers.save(volunteer);
    }

    @Transactional(readOnly = true)
    public Volunteer requireByEmail(String email) {
        return volunteers.findByEmailIgnoreCase(email == null ? "" : email.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No volunteer registered with email '" + email + "'. Please register first."));
    }

    @Transactional(readOnly = true)
    public List<Volunteer> findAll() {
        return volunteers.findAll();
    }
}
