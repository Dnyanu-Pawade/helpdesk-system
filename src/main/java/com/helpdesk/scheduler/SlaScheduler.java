package com.helpdesk.scheduler;
import com.helpdesk.entity.Ticket;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.repository.*;
import com.helpdesk.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
@Component @RequiredArgsConstructor @Slf4j
public class SlaScheduler {
    private final TicketRepository ticketRepo;
    private final EmailService emailService;

    @Scheduled(fixedRate = 3600000)
    public void checkSlaBreaches() {
        List<Ticket> overdue = ticketRepo.findBySlaBreachedFalseAndDueDateBeforeAndStatusNotIn(
                LocalDateTime.now(), List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED, TicketStatus.REJECTED));
        overdue.forEach(t -> {
            t.setSlaBreached(true);
            ticketRepo.save(t);
            if (t.getAssignedTo() != null)
                emailService.sendSlaBreachAlert(t.getAssignedTo().getEmail(), t.getAssignedTo().getFullName(), t.getTicketNumber());
            log.warn("SLA breached for ticket: {}", t.getTicketNumber());
        });
        if (!overdue.isEmpty()) log.info("SLA check: {} tickets breached", overdue.size());
    }
}
