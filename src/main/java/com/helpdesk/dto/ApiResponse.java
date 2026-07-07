package com.helpdesk.dto;
import lombok.*;
@Data @AllArgsConstructor @NoArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
}
