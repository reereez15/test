package com.example.backend.domain.service;

import com.example.backend.domain.DB.Guardian;
import com.example.backend.domain.DB.Role;
import com.example.backend.domain.DB.Senior;
import com.example.backend.domain.dto.UserDtos;
import com.example.backend.domain.repository.GuardianRepository;
import com.example.backend.domain.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final GuardianRepository guardianRepository;
    private final SeniorRepository seniorRepository;

    // 사용자 조회 및 생성
    @Transactional
    public UserDtos.UserResponseDto createUser(UserDtos.UserResponseDto userResponseDto, String userEmail) {
        Guardian guardian = guardianRepository.findByEmail(userEmail)
                .orElseGet(() -> guardianRepository.save(Guardian.builder()
                        .name(userEmail.split("@")[0])
                        .email(userEmail)
                        .relationship("보호자")   //임시값
                        .build()));
        // Senior는 Guardian의 ID를 공유하므로 guardian 먼저 저장된 상태여야 함
        Senior senior = Senior.builder()
                .guardian(guardian) // 핵심: 여기서 연결
                .name(userResponseDto.name())
                .seniorEmail(userEmail)
                .birthdate(userResponseDto.birthdate())
                .gender(userResponseDto.gender())
                .address(userResponseDto.address())
                .emergency_contact(userResponseDto.emergency_contact())
                .illness(userResponseDto.illness())
                .medication(userResponseDto.medication())
                .notes(userResponseDto.notes())
                .build();

        seniorRepository.save(senior);

        return new UserDtos.UserResponseDto(senior); // 생성된 Senior를 Dto로 리턴
    }
    // 사용자 정보 목록 조회 서비스

    // 약물 복용 여부 확인 토글 서비스

    // 사용자 정보 삭제 서비스

    // 사용자 소유 확인 서비스

}
