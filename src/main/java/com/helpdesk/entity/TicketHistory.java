package com.helpdesk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.helpdesk.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "ticket_history")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    @JsonIgnore
    private Ticket ticket;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "changed_by")
    @JsonIgnoreProperties({"password","resetToken","resetTokenExpiry","department"})
    private User changedBy;
    private String action;
    @Enumerated(EnumType.STRING) private TicketStatus oldStatus;
    @Enumerated(EnumType.STRING) private TicketStatus newStatus;
    private String notes;
    @Column(updatable = false) private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
