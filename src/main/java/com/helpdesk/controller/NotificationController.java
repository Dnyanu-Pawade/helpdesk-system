package com.helpdesk.controller;
import com.helpdesk.entity.Notification;
import com.helpdesk.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/notifications") @RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;
    @GetMapping public ResponseEntity<List<Notification>> getAll() { return ResponseEntity.ok(notificationService.getMyNotifications(userService.currentUser())); }
    @GetMapping("/unread-count") public ResponseEntity<Map<String,Long>> unreadCount() { return ResponseEntity.ok(Map.of("count", notificationService.countUnread(userService.currentUser()))); }
    @PatchMapping("/mark-read") public ResponseEntity<Void> markRead() { notificationService.markAllRead(userService.currentUser()); return ResponseEntity.ok().build(); }
}
