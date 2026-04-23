package service.audit;

import dao.AuditDao;
import domain.model.payment.AuthFailureReason;
import domain.model.payment.PaymentStatus;
import exception.PersistenceException;

public class AuditService {

    AuditDao auditDao;

    public AuditService(AuditDao auditDao){
        this.auditDao = auditDao;
    }


    public void logAttempt(AuditType type, AuditAction action, String id) throws PersistenceException{
        String attempt = "ATTEMPT:";
        auditDao.writeEntry(type.toString() + action + attempt + id);
    }
    public void logSuccess(AuditType type, AuditAction action, String id) throws PersistenceException {
        String success = "SUCCESS:";
        auditDao.writeEntry(type.toString() + action + success + id);
    }
    public void logFailure(AuditType type, AuditAction action, String id, String reason) throws PersistenceException {
        String failure = "FAILURE:";
        auditDao.writeEntry(type.toString() + action + failure + reason + ":" + id);
    }



}
