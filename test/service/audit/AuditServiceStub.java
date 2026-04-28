package service.audit;

import dao.audit.AuditDao;
import exception.PersistenceException;

public class AuditServiceStub extends AuditService {

    public AuditServiceStub() {
        super();
    }

    @Override
    public void logAttempt(AuditType type, AuditAction action, String id) {}

    @Override
    public void logSuccess(AuditType type, AuditAction action, String id) {}

    @Override
    public void logFailure(AuditType type, AuditAction action, String id, String reason) {}
}
