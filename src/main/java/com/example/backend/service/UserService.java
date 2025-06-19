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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final GuardianRepository guardianRepository;
    private final SeniorRepository seniorRepository;

    // 사용자 조회 및 생성
    @Transactional
    public UserDtos.UserResponseDto createUser(UserDtos.UserCreateRequestDto dto, String userEmail) {
        Guardian guardian = guardianRepository.findByEmail(userEmail)
                .orElseGet(() -> guardianRepository.save(Guardian.builder()
                        .name(userEmail.split("@")[0])
                        .guardianEmail(userEmail)
                        .relationship("보호자")   //임시값
                        .role(Role.USER)
                        .build()));
        // Senior는 Guardian의 ID를 공유하므로 guardian 먼저 저장된 상태여야 함
        Senior senior = Senior.builder()
                .guardian(guardian) // 핵심: 여기서 연결
                .name(dto.name())
                .seniorEmail(userEmail)
                .birthdate(dto.birthdate())
                .gender(dto.gender())
                .address(dto.address())
                .emergency_contact(dto.emergency_contact())
                .illness(dto.illness())
                .medication(dto.medication())
                .notes(dto.notes())
                .build();

        seniorRepository.save(senior);

        return new UserDtos.UserResponseDto(senior); // 생성된 Senior를 Dto로 리턴
    }
    // 사용자 정보 목록 조회 서비스
    public List<UserDtos.UserResponseDto> findMySenior(String userName) {
        Guardian guardian = guardianRepository.findByName(userName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보호자"));
        return guardian.getSeniors().stream()
                .map(UserDtos.UserResponseDto::new)
                .collect(Collectors.toList());
    }
    // 약물 복용 여부 확인 토글 서비스
    public UserDtos.UserResponseDto toggleSenior(Long id, String userEmail) {
        Senior senior = findByIdAndUserEmail2(id, userEmail);
        senior.setCompleted(!senior.isCompleted());
        return new UserDtos.UserResponseDto(senior);
    }
    // 사용자 정보 삭제 서비스
    // senior 삭제
    public void deleteSenior(Long id, String userEmail) {
        Senior senior = findByIdAndUserEmail2(id, userEmail);
        seniorRepository.delete(senior);
    }
    // guardian 삭제
    public void deleteGuardian(Long id, String userEmail) {
        Guardian guardian = findByIdAndUserEmail(id, userEmail);
        guardianRepository.delete(guardian);
    }
    // 사용자 소유 확인 서비스
    // Senior 확인
    private Senior findByIdAndUserEmail2(Long id, String userEmail) {
        return seniorRepository.findById(id)
                .filter(senior -> senior.getGuardian().getGuardianEmail().equals(userEmail))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 권한이 없는 사용자입니다"));
    }
    // guardian 확인
    private Guardian findByIdAndUserEmail(Long id, String userEmail) {
        return guardianRepository.findById(id)
                .filter(guardian -> guardian.getGuardianEmail().equals(userEmail))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 권한이 없는 사용자입니다"));
    }
}
