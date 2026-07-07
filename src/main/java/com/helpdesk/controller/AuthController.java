package com.helpdesk.controller;
import com.helpdesk.dto.*;
import com.helpdesk.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login") public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest req) { return ResponseEntity.ok(authService.login(req)); }
    @PostMapping("/register") public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest req) { return ResponseEntity.ok(new ApiResponse(true, authService.register(req))); }
    @PostMapping("/forgot-password") public ResponseEntity<ApiResponse> forgotPassword(@RequestBody Map<String,String> body) { return ResponseEntity.ok(new ApiResponse(true, authService.forgotPassword(body.get("email")))); }
    @PostMapping("/reset-password") public ResponseEntity<ApiResponse> resetPassword(@RequestBody Map<String,String> body) { return ResponseEntity.ok(new ApiResponse(true, authService.resetPassword(body.get("token"), body.get("newPassword")))); }
    @PostMapping("/refresh-token") public ResponseEntity<JwtResponse> refreshToken(@RequestBody Map<String,String> body) { return ResponseEntity.ok(authService.refreshToken(body.get("refreshToken"))); }
}
