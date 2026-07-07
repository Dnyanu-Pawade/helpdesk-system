package com.helpdesk.controller;
import com.helpdesk.dto.*;
import com.helpdesk.entity.*;
import com.helpdesk.enums.*;
import com.helpdesk.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController @RequestMapping("/api/team-lead") @RequiredArgsConstructor
public class TeamLeadController {
    private final TicketService ticketService;
    private final UserService userService;

    @GetMapping("/open") public ResponseEntity<List<TicketSummaryDTO>> open() { return ResponseEntity.ok(ticketService.getOpenTickets().stream().map(TicketSummaryDTO::from).collect(Collectors.toList())); }
    @GetMapping("/all/search") public ResponseEntity<Page<TicketSummaryDTO>> search(@RequestParam(required=false) String status, @RequestParam(required=false) String priority, @RequestParam(required=false) String category, @RequestParam(required=false) String keyword, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) { return ResponseEntity.ok(ticketService.searchAll(status, priority, category, keyword, page, size)); }
    @PostMapping("/tickets/{id}/assign") public ResponseEntity<TicketSummaryDTO> assign(@PathVariable Long id, @RequestBody Map<String,Long> body) { return ResponseEntity.ok(TicketSummaryDTO.from(ticketService.assignTicket(id, body.get("engineerId"), userService.currentUser()))); }
    @PutMapping("/tickets/{id}/priority") public ResponseEntity<TicketSummaryDTO> updatePriority(@PathVariable Long id, @RequestBody Map<String,String> body) {
        var ticket = ticketService.getById(id);
        ticket.setPriority(Priority.valueOf(body.get("priority")));
        return ResponseEntity.ok(TicketSummaryDTO.from(ticketService.updateStatus(id, ticket.getStatus(), "Priority updated", userService.currentUser())));
    }
    @GetMapping("/engineers") public ResponseEntity<List<User>> getEngineers() { return ResponseEntity.ok(userService.getByRole(Role.ROLE_SUPPORT_ENGINEER)); }
}
