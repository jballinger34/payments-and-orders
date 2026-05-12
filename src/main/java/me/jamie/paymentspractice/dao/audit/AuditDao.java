package me.jamie.paymentspractice.dao.audit;

import me.jamie.paymentspractice.data.document.AuditDocument;
import me.jamie.paymentspractice.exception.PersistenceException;

public interface AuditDao {
    void writeEntry(AuditDocument entry) throws PersistenceException;
}
