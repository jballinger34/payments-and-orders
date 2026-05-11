package me.jamie.paymentspractice.service.audit;


import me.jamie.paymentspractice.dao.audit.AuditDao;
import me.jamie.paymentspractice.document.AuditDocument;
import me.jamie.paymentspractice.document.AuditDocumentBuilder;
import me.jamie.paymentspractice.exception.PersistenceException;;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuditService {

    AuditDao auditDao;

    public AuditService(AuditDao auditDao){
        this.auditDao = auditDao;
    }
    public void logAttempt(AuditType type, AuditAction action, String id) throws PersistenceException {
        logAttempt(type,action,id,null);
    }
    public void logSuccess(AuditType type, AuditAction action, String id) throws PersistenceException {
        logSuccess(type,action,id,null);
    }
    public void logFailure(AuditType type, AuditAction action, String id) throws PersistenceException {
        logFailure(type,action,id,(Map<String, Object>) null);
    }
    public void logFailure(AuditType type, AuditAction action, String id, String reason) throws PersistenceException {
        logFailure(type,action,id,Map.of("reason", reason));
    }

    public void logAttempt(AuditType type, AuditAction action, String id, Map<String, Object> metadata) throws PersistenceException {
        AuditDocument doc = new AuditDocumentBuilder(type,action,id)
                .status(AuditStatus.ATTEMPT)
                .metadata(metadata)
                .build();
        auditDao.writeEntry(doc);
    }
    public void logSuccess(AuditType type, AuditAction action, String id,  Map<String, Object> metadata) throws PersistenceException {
        AuditDocument doc = new AuditDocumentBuilder(type,action,id)
                .status(AuditStatus.SUCCESS)
                .metadata(metadata)
                .build();
        auditDao.writeEntry(doc);
    }
    public void logFailure(AuditType type, AuditAction action, String id, Map<String, Object> metadata) throws PersistenceException {
        AuditDocument doc = new AuditDocumentBuilder(type,action,id)
                .status(AuditStatus.FAILURE)
                .metadata(metadata)
                .build();
        auditDao.writeEntry(doc);

    }



}
