package com.example.backend.domain.repository;

import com.example.backend.domain.DB.Guardian;
import com.example.backend.domain.DB.Senior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    Optional<Guardian> findByEmail(String email);
    Optional<Guardian> findByName(String name);
    // Optional사용을 안해서 null반환할 수 있는 방식 만약 userName과 일치하는 Guardian이 DB에 없다면
    // NullPointerException 발생
    Optional<Guardian> findById(Long id);
}