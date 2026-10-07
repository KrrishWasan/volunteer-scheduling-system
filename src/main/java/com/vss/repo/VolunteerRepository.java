package com.vss.repo;

import com.vss.domain.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
