package me.jamie.paymentspractice.data.document;

import me.jamie.paymentspractice.service.audit.AuditAction;
import me.jamie.paymentspractice.service.audit.AuditStatus;
import me.jamie.paymentspractice.service.audit.AuditType;

import java.time.LocalDateTime;
import java.util.Map;

public class AuditDocumentBuilder {
    private final AuditDocument document;

    public AuditDocumentBuilder(AuditType auditType, AuditAction action, String entityId){
        document = new AuditDocument();
        //set default/provided fields
        document.setTimestamp(LocalDateTime.now());
        this.type(auditType).action(action).entityId(entityId);
    }
    public AuditDocumentBuilder(){
        document = new AuditDocument();
        document.setTimestamp(LocalDateTime.now());
    }
    public AuditDocumentBuilder type(AuditType type) {
        document.setType(type);
        return this;
    }

    public AuditDocumentBuilder action(AuditAction action) {
        document.setAction(action);
        return this;
    }

    public AuditDocumentBuilder entityId(String entityId) {
        document.setEntityId(entityId);
        return this;
    }

    public AuditDocumentBuilder status(AuditStatus status) {
        document.setStatus(status);
        return this;
    }


    public AuditDocumentBuilder metadata(Map<String, Object> md) {
        document.setMetadata(md);
        return this;
    }


    public AuditDocument build() {
        return document;
    }

}
