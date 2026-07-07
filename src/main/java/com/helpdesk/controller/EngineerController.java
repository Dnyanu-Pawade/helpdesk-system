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
@RestController @RequestMapping("/api/engineer") @RequiredArgsConstructor
public class EngineerController {
    private final TicketService ticketService;
    private final UserService userService;

    @GetMapping("/assigned") public ResponseEntity<List<TicketSummaryDTO>> assigned() { return ResponseEntity.ok(ticketService.getAssignedTickets(userService.currentUser()).stream().map(TicketSummaryDTO::from).collect(Collectors.toList())); }
    @GetMapping("/assigned/search") public ResponseEntity<Page<TicketSummaryDTO>> search(@RequestParam(required=false) String status, @RequestParam(required=false) String keyword, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) { return ResponseEntity.ok(ticketService.searchEngineerTickets(userService.currentUser(), status, keyword, page, size)); }
    @PutMapping("/tickets/{id}/status") public ResponseEntity<TicketSummaryDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String,String> body) { return ResponseEntity.ok(TicketSummaryDTO.from(ticketService.updateStatus(id, TicketStatus.valueOf(body.get("status")), body.get("notes"), userService.currentUser()))); }
    @PutMapping("/tickets/{id}/resolve") public ResponseEntity<TicketSummaryDTO> resolve(@PathVariable Long id, @RequestBody Map<String,String> body) { return ResponseEntity.ok(TicketSummaryDTO.from(ticketService.resolveTicket(id, body.get("resolution"), userService.currentUser()))); }
    @PostMapping("/tickets/{id}/comments") public ResponseEntity<TicketComment> addComment(@PathVariable Long id, @RequestBody Map<String,String> body) {
        CommentType type = "INTERNAL".equals(body.get("type")) ? CommentType.INTERNAL_NOTE : CommentType.PUBLIC;
        return ResponseEntity.ok(ticketService.addComment(id, body.get("content"), type, userService.currentUser()));
    }
}
