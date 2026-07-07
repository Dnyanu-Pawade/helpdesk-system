package com.helpdesk.dto;
import com.helpdesk.entity.Ticket;
import com.helpdesk.enums.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data public class TicketSummaryDTO {
    private Long id;
    private String ticketNumber;
    private String title;
    private TicketStatus status;
    private Priority priority;
    private TicketCategory category;
    private String createdByName;
    private String assignedToName;
    private boolean slaBreached;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;

    public static TicketSummaryDTO from(Ticket t) {
        TicketSummaryDTO dto = new TicketSummaryDTO();
        dto.setId(t.getId());
        dto.setTicketNumber(t.getTicketNumber());
        dto.setTitle(t.getTitle());
        dto.setStatus(t.getStatus());
        dto.setPriority(t.getPriority());
        dto.setCategory(t.getCategory());
        dto.setSlaBreached(t.isSlaBreached());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setDueDate(t.getDueDate());
        if (t.getCreatedBy() != null) dto.setCreatedByName(t.getCreatedBy().getFullName());
        if (t.getAssignedTo() != null) dto.setAssignedToName(t.getAssignedTo().getFullName());
        return dto;
    }
}
