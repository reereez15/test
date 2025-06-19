package com.example.backend.conttoller;

import com.example.backend.dto.UserDtos;
import com.example.backend.service.GuardianService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guardian")
@RequiredArgsConstructor
public class GuardianController {

    private final UserService userService;
    private final GuardianService guardianService; // 추가 예정
    // Guardian 조회
    @GetMapping
    public ResponseEntity<List<UserDtos.SeniorResponseDto>> getMyGuardian(
            @AuthenticationPrincipal Jwt principal){
        return ResponseEntity.ok(userService.findMySenior(principal.getClaimAsString("email")));
    }
    // Guardian 추가
    @PostMapping
    public ResponseEntity<UserDtos.SeniorResponseDto> createGuardian(
            @RequestBody UserDtos.SeniorCreateRequestDto requestDto,
            @AuthenticationPrincipal Jwt principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(
                requestDto,
                principal.getClaimAsString("email")

        ));
    }
    // senior 약 복용 완료 / 미완료 토글
    @PatchMapping("/{id}")
    public ResponseEntity<UserDtos.SeniorResponseDto> toggleSenior(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt principal){
        return ResponseEntity.ok(userService.toggleSenior(id, principal.getClaimAsString("email")));
    }
    // Guardian 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuardian(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt principal) {
        userService.deleteGuardian(id, principal.getClaimAsString("email"));
        return ResponseEntity.noContent().build();
    }
}
