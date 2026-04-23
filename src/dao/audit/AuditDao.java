package dao.audit;

import exception.PersistenceException;

public interface AuditDao {
    void writeEntry(String entry) throws PersistenceException;
}
