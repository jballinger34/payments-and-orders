package me.jamie.paymentspractice.dao.audit;

import me.jamie.paymentspractice.document.AuditDocument;
import me.jamie.paymentspractice.exception.PersistenceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@Repository
public class FileAuditDao implements AuditDao {
    private final String auditFile;

    public FileAuditDao(@Value("${dao.audit.file}") String auditFile){
        this.auditFile = auditFile;
    }


    @Override
    public void writeEntry(AuditDocument entry) throws PersistenceException {
        try(PrintWriter out = new PrintWriter(new FileWriter(auditFile,true))){
            out.println(entry.toString());
        } catch (IOException e) {
            throw new PersistenceException("Could not write audit entry",e);
        }
    }

}
