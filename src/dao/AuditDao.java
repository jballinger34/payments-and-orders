package dao;

import exception.PersistenceException;

public interface AuditDao {
    //writeEntry should probably be broken into specific methods
    //i,e, writeCreateEntry, writeAuthEntry, etc
    // may get messy quickly if have multiple methods for each action

    // chatgpt suggests a couple of other options:
    // one write entry that takes params to structure message into Action, status, paymentid, reason...
    // or use enums for auditaction auditstatus
    void writeEntry(String entry) throws PersistenceException;
}
