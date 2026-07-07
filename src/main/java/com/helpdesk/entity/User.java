package com.helpdesk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.helpdesk.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String username;
    @Column(nullable = false) @JsonIgnore private String password;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String fullName;
    private String phone;
    private String employeeId;
    private String designation;
    private String profilePhoto;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Role role;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Department department;
    private boolean active = true;
    @JsonIgnore private String resetToken;
    @JsonIgnore private LocalDateTime resetTokenExpiry;
    @Column(updatable = false) private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
