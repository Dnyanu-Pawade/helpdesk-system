package com.helpdesk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "refresh_tokens")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id") private User user;
    @Column(nullable = false, unique = true) private String token;
    private LocalDateTime expiryDate;
    public boolean isExpired() { return LocalDateTime.now().isAfter(expiryDate); }
}
