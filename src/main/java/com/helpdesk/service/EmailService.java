package com.helpdesk.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor @Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    @Async
    public void send(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to); msg.setSubject(subject); msg.setText(body); msg.setFrom("noreply@helpdesk.com");
            mailSender.send(msg); log.info("Email sent to {}", to);
        } catch (Exception e) { log.error("Email failed: {}", e.getMessage()); }
    }
    public void sendTicketCreated(String to, String name, String ticketNo, String title) {
        send(to, "Ticket Created - " + ticketNo, "Dear " + name + ",\n\nYour ticket '" + title + "' (" + ticketNo + ") has been created.\n\nHelp Desk System");
    }
    public void sendTicketAssigned(String to, String name, String ticketNo, String engineerName) {
        send(to, "Ticket Assigned - " + ticketNo, "Dear " + name + ",\n\nYour ticket " + ticketNo + " has been assigned to " + engineerName + ".\n\nHelp Desk System");
    }
    public void sendTicketResolved(String to, String name, String ticketNo, String resolution) {
        send(to, "Ticket Resolved - " + ticketNo, "Dear " + name + ",\n\nYour ticket " + ticketNo + " has been resolved.\nResolution: " + resolution + "\n\nHelp Desk System");
    }
    public void sendTicketClosed(String to, String name, String ticketNo) {
        send(to, "Ticket Closed - " + ticketNo, "Dear " + name + ",\n\nYour ticket " + ticketNo + " has been closed.\n\nHelp Desk System");
    }
    public void sendPasswordReset(String to, String token) {
        send(to, "Password Reset - Help Desk", "Your password reset token: " + token + "\n\nExpires in 1 hour.\n\nHelp Desk System");
    }
    public void sendSlaBreachAlert(String to, String name, String ticketNo) {
        send(to, "SLA Breach Alert - " + ticketNo, "Dear " + name + ",\n\nTicket " + ticketNo + " has breached SLA. Please take immediate action.\n\nHelp Desk System");
    }
}
