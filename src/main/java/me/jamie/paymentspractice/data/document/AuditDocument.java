package me.jamie.paymentspractice.data.document;

import me.jamie.paymentspractice.service.audit.AuditAction;
import me.jamie.paymentspractice.service.audit.AuditStatus;
import me.jamie.paymentspractice.service.audit.AuditType;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Document(indexName = "audit-logs")
public class AuditDocument {

    @Id
    private String id;

    @Field(type = FieldType.Date)
    private LocalDateTime timestamp;

    @Field(type = FieldType.Keyword)
    private AuditType type;

    @Field(type = FieldType.Keyword)
    private AuditAction action;

    //orderId, productId, etc.
    @Field(type = FieldType.Keyword)
    private String entityId;

    @Field(type = FieldType.Keyword)
    private AuditStatus status;

    //any additonal data
    private Map<String, Object> metadata = new HashMap<>();
    // could add actor field: who did the action - not implemented yet but can still log "merchant" or "admin"

    public AuditDocument(){};

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(AuditType type) {
        this.type = type;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public void setMetadata(Map<String, Object> metadata){
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        //for old file impl, doesnt use metadata
        return "AuditDocument{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", action=" + action +
                ", entityId='" + entityId + '\'' +
                ", status=" + status +
                '}';
    }
}
