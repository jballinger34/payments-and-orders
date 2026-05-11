package me.jamie.paymentspractice.repository;

import me.jamie.paymentspractice.document.AuditDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AuditElasticsearchRepository extends ElasticsearchRepository<AuditDocument, String> {
}
