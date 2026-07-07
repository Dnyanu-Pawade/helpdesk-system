package com.helpdesk.service;
import com.helpdesk.dto.*;
import com.helpdesk.entity.*;
import com.helpdesk.enums.Role;
import com.helpdesk.repository.*;
import com.helpdesk.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final AuditLogService auditLogService;

    @Transactional
    public JwtResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        String token = jwtTokenProvider.generateToken(auth);
        String refresh = createRefreshToken(user.getId());
        User u = userRepository.findById(user.getId()).orElseThrow();
        String dept = u.getDepartment() != null ? u.getDepartment().getName() : null;
        auditLogService.log(user.getUsername(), "LOGIN", "User", user.getId().toString(), "Login successful");
        return new JwtResponse(token, refresh, user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getRole(), dept);
    }

    public String register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) throw new RuntimeException("Username already taken");
        if (userRepository.existsByEmail(req.getEmail())) throw new RuntimeException("Email already registered");
        User.UserBuilder builder = User.builder()
                .username(req.getUsername()).password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail()).fullName(req.getFullName()).phone(req.getPhone())
                .employeeId(req.getEmployeeId()).designation(req.getDesignation())
                .role(Role.ROLE_EMPLOYEE).active(true);
        if (req.getDepartmentId() != null)
            departmentRepository.findById(req.getDepartmentId()).ifPresent(builder::department);
        userRepository.save(builder.build());
        auditLogService.log(req.getUsername(), "REGISTER", "User", null, "New employee registered");
        return "Registered successfully";
    }

    public String forgotPassword(String email) {
        User u = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("No account found with this email"));
        String token = UUID.randomUUID().toString();
        u.setResetToken(token); u.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(u);
        emailService.sendPasswordReset(email, token);
        return "RESET_TOKEN:" + token;
    }

    public String resetPassword(String token, String newPassword) {
        User u = userRepository.findByResetToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
        if (u.getResetTokenExpiry().isBefore(LocalDateTime.now())) throw new RuntimeException("Token expired");
        u.setPassword(passwordEncoder.encode(newPassword));
        u.setResetToken(null); u.setResetTokenExpiry(null);
        userRepository.save(u);
        return "Password reset successfully";
    }

    @Transactional
    public JwtResponse refreshToken(String refreshToken) {
        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (rt.isExpired()) { refreshTokenRepository.delete(rt); throw new RuntimeException("Refresh token expired"); }
        User u = rt.getUser();
        String newToken = jwtTokenProvider.generateTokenFromUsername(u.getUsername());
        String dept = u.getDepartment() != null ? u.getDepartment().getName() : null;
        return new JwtResponse(newToken, refreshToken, u.getId(), u.getUsername(), u.getEmail(), u.getFullName(), u.getRole().name(), dept);
    }

    @Transactional
    public String createRefreshToken(Long userId) {
        User u = userRepository.findById(userId).orElseThrow();
        refreshTokenRepository.deleteByUser(u);
        return refreshTokenRepository.save(RefreshToken.builder().user(u).token(UUID.randomUUID().toString()).expiryDate(LocalDateTime.now().plusDays(7)).build()).getToken();
    }
}
