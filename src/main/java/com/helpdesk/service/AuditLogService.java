package com.helpdesk.service;
import com.helpdesk.entity.AuditLog;
import com.helpdesk.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository repo;
    public void log(String by, String action, String entity, String entityId, String details) {
        repo.save(AuditLog.builder().performedBy(by).action(action).entity(entity).entityId(entityId).details(details).build());
    }
    public List<AuditLog> getRecent(int limit) { return repo.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit)); }
}
