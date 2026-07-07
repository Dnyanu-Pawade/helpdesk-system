package com.helpdesk.dto;
import com.helpdesk.enums.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
@Data public class TicketRequest {
    @NotBlank private String title;
    private String description;
    @NotNull private Priority priority;
    @NotNull private TicketCategory category;
}
