package com.helpdesk.repository;
import com.helpdesk.entity.Notification;
import com.helpdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user=:user AND n.isRead=false")
    long countUnread(@Param("user") User user);
}
