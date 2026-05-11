package me.jamie.paymentspractice.dao.audit;

import me.jamie.paymentspractice.document.AuditDocument;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.repository.AuditElasticsearchRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class ElasticsearchAuditDao implements AuditDao {

    private final AuditElasticsearchRepository repository;

    public ElasticsearchAuditDao(AuditElasticsearchRepository repository){
        this.repository = repository;
    }

    @Override
    public void writeEntry(AuditDocument entry) throws PersistenceException {
        try{
            repository.save(entry);
        } catch (Exception e){
            throw new PersistenceException("Could not write audit entry");
        }
    }
}
