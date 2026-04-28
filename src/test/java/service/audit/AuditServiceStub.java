package service.audit;

import me.jamie.paymentspractice.service.audit.AuditAction;
import me.jamie.paymentspractice.service.audit.AuditService;
import me.jamie.paymentspractice.service.audit.AuditType;


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
