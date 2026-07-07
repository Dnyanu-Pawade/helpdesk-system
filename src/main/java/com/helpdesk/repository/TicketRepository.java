package com.helpdesk.repository;
import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.User;
import com.helpdesk.enums.Priority;
import com.helpdesk.enums.TicketStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreatedByOrderByCreatedAtDesc(User user);
    List<Ticket> findByAssignedToOrderByCreatedAtDesc(User user);
    List<Ticket> findByStatusOrderByCreatedAtDesc(TicketStatus status);
    List<Ticket> findByStatusInOrderByCreatedAtDesc(List<TicketStatus> statuses);
    long countByStatus(TicketStatus status);
    long countByPriority(Priority priority);
    long countByCreatedBy(User user);
    long countBySlaBreached(boolean breached);
    @Query("SELECT t FROM Ticket t WHERE (:status IS NULL OR t.status=:status) AND (:priority IS NULL OR t.priority=:priority) AND (:category IS NULL OR CAST(t.category AS string)=:category) AND (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:keyword,'%')) OR LOWER(t.ticketNumber) LIKE LOWER(CONCAT('%',:keyword,'%')))")
    Page<Ticket> searchAll(@Param("status") TicketStatus status, @Param("priority") Priority priority, @Param("category") String category, @Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT t FROM Ticket t WHERE t.createdBy=:user AND (:status IS NULL OR t.status=:status) AND (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:keyword,'%')))")
    Page<Ticket> searchByUser(@Param("user") User user, @Param("status") TicketStatus status, @Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT t FROM Ticket t WHERE t.assignedTo=:engineer AND (:status IS NULL OR t.status=:status) AND (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:keyword,'%')))")
    Page<Ticket> searchByEngineer(@Param("engineer") User engineer, @Param("status") TicketStatus status, @Param("keyword") String keyword, Pageable pageable);
    List<Ticket> findBySlaBreachedFalseAndDueDateBeforeAndStatusNotIn(LocalDateTime now, List<TicketStatus> excludedStatuses);
    @Query("SELECT t.category, COUNT(t) FROM Ticket t GROUP BY t.category")
    List<Object[]> countByCategory();
    @Query("SELECT t.priority, COUNT(t) FROM Ticket t GROUP BY t.priority")
    List<Object[]> countByPriorityGroup();
    @Query("SELECT MONTH(t.createdAt), COUNT(t) FROM Ticket t WHERE YEAR(t.createdAt)=:year GROUP BY MONTH(t.createdAt)")
    List<Object[]> monthlyStats(@Param("year") int year);
    @Query("SELECT t.assignedTo, COUNT(t), SUM(CASE WHEN t.status='RESOLVED' OR t.status='CLOSED' THEN 1 ELSE 0 END) FROM Ticket t WHERE t.assignedTo IS NOT NULL GROUP BY t.assignedTo")
    List<Object[]> engineerPerformance();
}
