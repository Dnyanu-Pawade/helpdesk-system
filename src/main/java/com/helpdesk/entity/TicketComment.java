package com.helpdesk.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.helpdesk.enums.CommentType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "ticket_comments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Ticket ticket;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnoreProperties({"password","resetToken","resetTokenExpiry","department"})
    private User author;
    @Column(nullable = false, length = 2000) private String content;
    @Enumerated(EnumType.STRING) @Builder.Default
    private CommentType type = CommentType.PUBLIC;
    @Column(updatable = false) private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
