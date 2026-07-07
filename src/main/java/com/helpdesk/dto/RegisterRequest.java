package com.helpdesk.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class RegisterRequest {
    @NotBlank private String username;
    @NotBlank @Size(min=6) private String password;
    @NotBlank @Email private String email;
    @NotBlank private String fullName;
    private String phone;
    private String employeeId;
    private String designation;
    private Long departmentId;
}
