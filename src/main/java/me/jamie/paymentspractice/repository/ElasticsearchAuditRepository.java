package me.jamie.paymentspractice.repository;

import me.jamie.paymentspractice.document.AuditDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticsearchAuditRepository extends ElasticsearchRepository<AuditDocument, String> {
}
