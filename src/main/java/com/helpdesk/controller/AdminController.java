package com.helpdesk.controller;
import com.helpdesk.entity.*;
import com.helpdesk.enums.*;
import com.helpdesk.repository.*;
import com.helpdesk.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
@RestController @RequestMapping("/api/admin") @RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final TicketService ticketService;
    private final AuditLogService auditLogService;
    private final DepartmentRepository deptRepo;
    private final TicketRepository ticketRepo;
    private final NotificationService notificationService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String,Object>> dashboard() {
        Map<String,Object> stats = new HashMap<>();
        stats.put("totalTickets", ticketRepo.count());
        stats.put("openTickets", ticketRepo.countByStatus(TicketStatus.OPEN));
        stats.put("assignedTickets", ticketRepo.countByStatus(TicketStatus.ASSIGNED));
        stats.put("inProgressTickets", ticketRepo.countByStatus(TicketStatus.IN_PROGRESS));
        stats.put("resolvedTickets", ticketRepo.countByStatus(TicketStatus.RESOLVED));
        stats.put("closedTickets", ticketRepo.countByStatus(TicketStatus.CLOSED));
        stats.put("criticalTickets", ticketRepo.countByPriority(Priority.CRITICAL));
        stats.put("highTickets", ticketRepo.countByPriority(Priority.HIGH));
        stats.put("slaBreached", ticketRepo.countBySlaBreached(true));
        stats.put("totalUsers", userService.getAll().size());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users") public ResponseEntity<List<User>> getUsers() { return ResponseEntity.ok(userService.getAll()); }
    @PatchMapping("/users/{id}/role") public ResponseEntity<User> updateRole(@PathVariable Long id, @RequestParam Role role) { return ResponseEntity.ok(userService.updateRole(id, role)); }
    @PatchMapping("/users/{id}/toggle") public ResponseEntity<User> toggleStatus(@PathVariable Long id) { return ResponseEntity.ok(userService.toggleStatus(id)); }

    @GetMapping("/departments") public ResponseEntity<List<Department>> getDepts() { return ResponseEntity.ok(deptRepo.findAll()); }
    @PostMapping("/departments") public ResponseEntity<Department> createDept(@RequestBody Department dept) { return ResponseEntity.ok(deptRepo.save(dept)); }

    @GetMapping("/audit-logs") public ResponseEntity<List<AuditLog>> getAuditLogs(@RequestParam(defaultValue="100") int limit) { return ResponseEntity.ok(auditLogService.getRecent(limit)); }

    @GetMapping("/charts/category")
    public ResponseEntity<Map<String,Object>> categoryChart() {
        List<Object[]> raw = ticketRepo.countByCategory();
        List<String> labels = new ArrayList<>(); List<Long> values = new ArrayList<>();
        raw.forEach(r -> { labels.add(r[0].toString()); values.add(((Number)r[1]).longValue()); });
        return ResponseEntity.ok(Map.of("labels", labels, "values", values));
    }

    @GetMapping("/charts/priority")
    public ResponseEntity<Map<String,Object>> priorityChart() {
        List<Object[]> raw = ticketRepo.countByPriorityGroup();
        List<String> labels = new ArrayList<>(); List<Long> values = new ArrayList<>();
        raw.forEach(r -> { labels.add(r[0].toString()); values.add(((Number)r[1]).longValue()); });
        return ResponseEntity.ok(Map.of("labels", labels, "values", values));
    }

    @GetMapping("/charts/monthly")
    public ResponseEntity<Map<String,Object>> monthlyChart() {
        int year = LocalDateTime.now().getYear();
        List<Object[]> raw = ticketRepo.monthlyStats(year);
        Map<Integer,Long> monthMap = new LinkedHashMap<>();
        for (int i=1;i<=12;i++) monthMap.put(i, 0L);
        raw.forEach(r -> monthMap.put(((Number)r[0]).intValue(), ((Number)r[1]).longValue()));
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        return ResponseEntity.ok(Map.of("labels", Arrays.asList(months), "values", new ArrayList<>(monthMap.values())));
    }

    @GetMapping("/charts/engineer-performance")
    public ResponseEntity<List<Map<String,Object>>> engineerPerformance() {
        List<Object[]> raw = ticketRepo.engineerPerformance();
        List<Map<String,Object>> result = new ArrayList<>();
        raw.forEach(r -> {
            User u = (User) r[0];
            result.add(Map.of("name", u.getFullName(), "total", r[1], "resolved", r[2]));
        });
        return ResponseEntity.ok(result);
    }
}
