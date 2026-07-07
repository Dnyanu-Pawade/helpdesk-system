package com.helpdesk.controller;
import com.helpdesk.dto.ApiResponse;
import com.helpdesk.entity.User;
import com.helpdesk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/me") public ResponseEntity<User> getProfile() { return ResponseEntity.ok(userService.currentUser()); }
    @PutMapping("/me") public ResponseEntity<User> updateProfile(@RequestBody User user) { return ResponseEntity.ok(userService.updateProfile(user)); }
    @PostMapping("/me/photo") public ResponseEntity<ApiResponse> uploadPhoto(@RequestParam("file") MultipartFile file) { return ResponseEntity.ok(new ApiResponse(true, userService.uploadProfilePhoto(file))); }
    @PostMapping("/me/change-password") public ResponseEntity<ApiResponse> changePassword(@RequestBody Map<String,String> body) { return ResponseEntity.ok(new ApiResponse(true, userService.changePassword(body.get("currentPassword"), body.get("newPassword")))); }
}
