package com.helpdesk.controller;
import com.helpdesk.dto.*;
import com.helpdesk.entity.*;
import com.helpdesk.enums.CommentType;
import com.helpdesk.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController @RequestMapping("/api/tickets") @RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final UserService userService;

    @PostMapping public ResponseEntity<TicketSummaryDTO> create(@Valid @RequestBody TicketRequest req) { return ResponseEntity.ok(TicketSummaryDTO.from(ticketService.createTicket(req, userService.currentUser()))); }
    @GetMapping("/my") public ResponseEntity<List<TicketSummaryDTO>> myTickets() { return ResponseEntity.ok(ticketService.getMyTickets(userService.currentUser()).stream().map(TicketSummaryDTO::from).collect(Collectors.toList())); }
    @GetMapping("/my/search") public ResponseEntity<Page<TicketSummaryDTO>> searchMyTickets(@RequestParam(required=false) String status, @RequestParam(required=false) String keyword, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) { return ResponseEntity.ok(ticketService.searchMyTickets(userService.currentUser(), status, keyword, page, size)); }
    @GetMapping("/{id}") public ResponseEntity<Ticket> getById(@PathVariable Long id) { return ResponseEntity.ok(ticketService.getById(id)); }
    @PostMapping("/{id}/close") public ResponseEntity<TicketSummaryDTO> close(@PathVariable Long id) { return ResponseEntity.ok(TicketSummaryDTO.from(ticketService.closeTicket(id, userService.currentUser()))); }
    @PostMapping("/{id}/reopen") public ResponseEntity<TicketSummaryDTO> reopen(@PathVariable Long id) { return ResponseEntity.ok(TicketSummaryDTO.from(ticketService.reopenTicket(id, userService.currentUser()))); }
    @GetMapping("/{id}/comments") public ResponseEntity<List<TicketComment>> getComments(@PathVariable Long id) { return ResponseEntity.ok(ticketService.getComments(id)); }
    @PostMapping("/{id}/comments") public ResponseEntity<TicketComment> addComment(@PathVariable Long id, @RequestBody Map<String,String> body) { return ResponseEntity.ok(ticketService.addComment(id, body.get("content"), CommentType.PUBLIC, userService.currentUser())); }
    @GetMapping("/{id}/attachments") public ResponseEntity<List<Attachment>> getAttachments(@PathVariable Long id) { return ResponseEntity.ok(ticketService.getAttachments(id)); }
    @PostMapping("/{id}/attachments") public ResponseEntity<Attachment> upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException { return ResponseEntity.ok(ticketService.uploadAttachment(id, file, userService.currentUser())); }
    @GetMapping("/{id}/history") public ResponseEntity<List<TicketHistory>> getHistory(@PathVariable Long id) { return ResponseEntity.ok(ticketService.getHistory(id)); }
}
