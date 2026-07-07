package com.helpdesk.service;
import com.helpdesk.dto.TicketRequest;
import com.helpdesk.entity.*;
import com.helpdesk.enums.*;
import com.helpdesk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service @RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepo;
    private final TicketCommentRepository commentRepo;
    private final AttachmentRepository attachmentRepo;
    private final TicketHistoryRepository historyRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final AuditLogService auditLogService;
    @Value("${file.upload-dir:uploads}") private String uploadDir;
    @Value("${sla.low.hours:72}") private int slaLow;
    @Value("${sla.medium.hours:48}") private int slaMedium;
    @Value("${sla.high.hours:24}") private int slaHigh;
    @Value("${sla.critical.hours:4}") private int slaCritical;

    public Ticket createTicket(TicketRequest req, User creator) {
        LocalDateTime due = calculateDueDate(req.getPriority());
        Ticket ticket = Ticket.builder()
                .ticketNumber("TKT-" + System.currentTimeMillis())
                .title(req.getTitle()).description(req.getDescription())
                .priority(req.getPriority()).category(req.getCategory())
                .status(TicketStatus.OPEN).createdBy(creator).dueDate(due).build();
        ticketRepo.save(ticket);
        saveHistory(ticket, creator, "CREATED", null, TicketStatus.OPEN, "Ticket created");
        userRepo.findByRole(Role.ROLE_TEAM_LEAD).forEach(tl ->
            notificationService.send(tl, "New Ticket", "New ticket: " + ticket.getTitle(), "TICKET"));
        emailService.sendTicketCreated(creator.getEmail(), creator.getFullName(), ticket.getTicketNumber(), ticket.getTitle());
        auditLogService.log(creator.getUsername(), "CREATE_TICKET", "Ticket", ticket.getTicketNumber(), "Priority: " + req.getPriority());
        return ticket;
    }

    public Ticket assignTicket(Long id, Long engineerId, User assignedBy) {
        Ticket ticket = getById(id);
        User engineer = userRepo.findById(engineerId).orElseThrow(() -> new RuntimeException("Engineer not found"));
        TicketStatus old = ticket.getStatus();
        ticket.setAssignedTo(engineer); ticket.setStatus(TicketStatus.ASSIGNED);
        ticketRepo.save(ticket);
        saveHistory(ticket, assignedBy, "ASSIGNED", old, TicketStatus.ASSIGNED, "Assigned to " + engineer.getFullName());
        notificationService.send(engineer, "Ticket Assigned", "Ticket " + ticket.getTicketNumber() + " assigned to you", "TICKET");
        notificationService.send(ticket.getCreatedBy(), "Ticket Assigned", "Your ticket assigned to " + engineer.getFullName(), "TICKET");
        emailService.sendTicketAssigned(ticket.getCreatedBy().getEmail(), ticket.getCreatedBy().getFullName(), ticket.getTicketNumber(), engineer.getFullName());
        auditLogService.log(assignedBy.getUsername(), "ASSIGN_TICKET", "Ticket", ticket.getTicketNumber(), "Assigned to: " + engineer.getUsername());
        return ticket;
    }

    public Ticket updateStatus(Long id, TicketStatus newStatus, String notes, User updatedBy) {
        Ticket ticket = getById(id);
        TicketStatus old = ticket.getStatus();
        ticket.setStatus(newStatus);
        if (newStatus == TicketStatus.RESOLVED) ticket.setResolvedAt(LocalDateTime.now());
        if (newStatus == TicketStatus.CLOSED) ticket.setClosedAt(LocalDateTime.now());
        ticketRepo.save(ticket);
        saveHistory(ticket, updatedBy, "STATUS_CHANGED", old, newStatus, notes);
        notificationService.send(ticket.getCreatedBy(), "Ticket Updated", "Ticket " + ticket.getTicketNumber() + " status: " + newStatus, "TICKET");
        if (newStatus == TicketStatus.RESOLVED)
            emailService.sendTicketResolved(ticket.getCreatedBy().getEmail(), ticket.getCreatedBy().getFullName(), ticket.getTicketNumber(), notes);
        if (newStatus == TicketStatus.CLOSED)
            emailService.sendTicketClosed(ticket.getCreatedBy().getEmail(), ticket.getCreatedBy().getFullName(), ticket.getTicketNumber());
        auditLogService.log(updatedBy.getUsername(), "UPDATE_STATUS", "Ticket", ticket.getTicketNumber(), old + " -> " + newStatus);
        return ticket;
    }

    public Ticket resolveTicket(Long id, String resolution, User engineer) {
        Ticket ticket = getById(id);
        TicketStatus old = ticket.getStatus();
        ticket.setStatus(TicketStatus.RESOLVED); ticket.setResolution(resolution); ticket.setResolvedAt(LocalDateTime.now());
        ticketRepo.save(ticket);
        saveHistory(ticket, engineer, "RESOLVED", old, TicketStatus.RESOLVED, resolution);
        notificationService.send(ticket.getCreatedBy(), "Ticket Resolved", "Your ticket " + ticket.getTicketNumber() + " has been resolved", "TICKET");
        emailService.sendTicketResolved(ticket.getCreatedBy().getEmail(), ticket.getCreatedBy().getFullName(), ticket.getTicketNumber(), resolution);
        auditLogService.log(engineer.getUsername(), "RESOLVE_TICKET", "Ticket", ticket.getTicketNumber(), resolution);
        return ticket;
    }

    public Ticket closeTicket(Long id, User user) {
        Ticket ticket = getById(id);
        TicketStatus old = ticket.getStatus();
        ticket.setStatus(TicketStatus.CLOSED); ticket.setClosedAt(LocalDateTime.now());
        ticketRepo.save(ticket);
        saveHistory(ticket, user, "CLOSED", old, TicketStatus.CLOSED, "Ticket closed");
        emailService.sendTicketClosed(ticket.getCreatedBy().getEmail(), ticket.getCreatedBy().getFullName(), ticket.getTicketNumber());
        auditLogService.log(user.getUsername(), "CLOSE_TICKET", "Ticket", ticket.getTicketNumber(), "Closed by " + user.getUsername());
        return ticket;
    }

    public Ticket reopenTicket(Long id, User user) {
        Ticket ticket = getById(id);
        TicketStatus old = ticket.getStatus();
        ticket.setStatus(TicketStatus.OPEN); ticket.setResolvedAt(null); ticket.setClosedAt(null);
        ticketRepo.save(ticket);
        saveHistory(ticket, user, "REOPENED", old, TicketStatus.OPEN, "Ticket reopened");
        auditLogService.log(user.getUsername(), "REOPEN_TICKET", "Ticket", ticket.getTicketNumber(), "Reopened");
        return ticket;
    }

    public TicketComment addComment(Long ticketId, String content, CommentType type, User author) {
        Ticket ticket = getById(ticketId);
        TicketComment comment = TicketComment.builder().ticket(ticket).author(author).content(content).type(type).build();
        commentRepo.save(comment);
        if (type == CommentType.PUBLIC) {
            User notify = author.getId().equals(ticket.getCreatedBy().getId()) ? ticket.getAssignedTo() : ticket.getCreatedBy();
            if (notify != null) notificationService.send(notify, "New Comment", author.getFullName() + " commented on " + ticket.getTicketNumber(), "COMMENT");
        }
        return comment;
    }

    public Attachment uploadAttachment(Long ticketId, MultipartFile file, User user) throws IOException {
        Ticket ticket = getById(ticketId);
        Path dir = Paths.get(uploadDir, "tickets", ticketId.toString());
        Files.createDirectories(dir);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return attachmentRepo.save(Attachment.builder().ticket(ticket).fileName(file.getOriginalFilename()).filePath("/uploads/tickets/" + ticketId + "/" + filename).fileType(file.getContentType()).fileSize(file.getSize()).uploadedBy(user).build());
    }

    private void saveHistory(Ticket ticket, User user, String action, TicketStatus old, TicketStatus newStatus, String notes) {
        historyRepo.save(TicketHistory.builder().ticket(ticket).changedBy(user).action(action).oldStatus(old).newStatus(newStatus).notes(notes).build());
    }

    private LocalDateTime calculateDueDate(Priority p) {
        return switch (p) {
            case LOW -> LocalDateTime.now().plusHours(slaLow);
            case MEDIUM -> LocalDateTime.now().plusHours(slaMedium);
            case HIGH -> LocalDateTime.now().plusHours(slaHigh);
            case CRITICAL -> LocalDateTime.now().plusHours(slaCritical);
        };
    }

    public Ticket getById(Long id) { return ticketRepo.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found")); }
    public List<Ticket> getMyTickets(User user) { return ticketRepo.findByCreatedByOrderByCreatedAtDesc(user); }
    public List<Ticket> getAssignedTickets(User engineer) { return ticketRepo.findByAssignedToOrderByCreatedAtDesc(engineer); }
    public List<Ticket> getOpenTickets() { return ticketRepo.findByStatusOrderByCreatedAtDesc(TicketStatus.OPEN); }
    @Transactional public List<TicketComment> getComments(Long ticketId) { return commentRepo.findByTicketIdOrderByCreatedAtAsc(ticketId); }
    @Transactional public List<Attachment> getAttachments(Long ticketId) { return attachmentRepo.findByTicketId(ticketId); }
    @Transactional public List<TicketHistory> getHistory(Long ticketId) { return historyRepo.findByTicketIdOrderByCreatedAtDesc(ticketId); }
    public Page<com.helpdesk.dto.TicketSummaryDTO> searchAll(String status, String priority, String category, String keyword, int page, int size) {
        TicketStatus ts = (status != null && !status.isEmpty()) ? TicketStatus.valueOf(status) : null;
        Priority pr = (priority != null && !priority.isEmpty()) ? Priority.valueOf(priority) : null;
        return ticketRepo.searchAll(ts, pr, category, keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))).map(com.helpdesk.dto.TicketSummaryDTO::from);
    }
    public Page<com.helpdesk.dto.TicketSummaryDTO> searchMyTickets(User user, String status, String keyword, int page, int size) {
        TicketStatus ts = (status != null && !status.isEmpty()) ? TicketStatus.valueOf(status) : null;
        return ticketRepo.searchByUser(user, ts, keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))).map(com.helpdesk.dto.TicketSummaryDTO::from);
    }
    public Page<com.helpdesk.dto.TicketSummaryDTO> searchEngineerTickets(User engineer, String status, String keyword, int page, int size) {
        TicketStatus ts = (status != null && !status.isEmpty()) ? TicketStatus.valueOf(status) : null;
        return ticketRepo.searchByEngineer(engineer, ts, keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))).map(com.helpdesk.dto.TicketSummaryDTO::from);
    }
}
