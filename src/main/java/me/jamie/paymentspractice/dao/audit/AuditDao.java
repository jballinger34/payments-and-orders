package me.jamie.paymentspractice.dao.audit;

import me.jamie.paymentspractice.exception.PersistenceException;

public interface AuditDao {
    void writeEntry(String entry) throws PersistenceException;
}
