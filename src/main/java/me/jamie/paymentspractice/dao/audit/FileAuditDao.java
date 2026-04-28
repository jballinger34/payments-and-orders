package me.jamie.paymentspractice.dao.audit;

import me.jamie.paymentspractice.exception.PersistenceException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class FileAuditDao implements AuditDao {
    private static final String AUDIT_FILE = "audit.txt";

    @Override
    public void writeEntry(String entry) throws PersistenceException {
        try(PrintWriter out = new PrintWriter(new FileWriter(AUDIT_FILE,true))){
            out.println(LocalDateTime.now() + " " + entry);
        } catch (IOException e) {
            throw new PersistenceException("Could not write audit entry",e);
        }
    }

}
