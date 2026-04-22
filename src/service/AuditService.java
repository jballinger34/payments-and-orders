package service;

import dao.AuditDao;
import domain.model.AuthFailureReason;
import exception.PersistenceException;

public class AuditService {

    AuditDao auditDao;

    public AuditService(AuditDao auditDao){
        this.auditDao = auditDao;
    }
    // should probably be done with ENUMS...
    static final String PAYMENT_STR = "PAYMENT:";
    static final String ORDER_STR = "ORDER:";

    static final String CREATE_STR = "CREATE:";
    static final String AUTH_STR = "AUTH:";
    static final String CAPTURE_STR = "CAPTURE:";

    static final String ATTEMPT_STR = "ATTEMPT:";
    static final String SUCCESS_STR = "ATTEMPT:";
    static final String FAILURE_STR = "FAILURE:";
    // PAYMENT related

    // CREATE
    public void logPaymentCreateAttempt(String paymentId) throws PersistenceException {
        auditDao.writeEntry(PAYMENT_STR + CREATE_STR + ATTEMPT_STR + paymentId);
    }
    public void logPaymentCreateSuccess(String paymentId) throws PersistenceException {
        auditDao.writeEntry(PAYMENT_STR + CREATE_STR + SUCCESS_STR + paymentId);
    }

    // AUTH
    public void logPaymentAuthAttempt(String paymentId) throws PersistenceException {
        auditDao.writeEntry(PAYMENT_STR + AUTH_STR + ATTEMPT_STR + paymentId);
    }

    public void logPaymentAuthSuccess(String paymentId) throws  PersistenceException{
        auditDao.writeEntry(PAYMENT_STR + AUTH_STR + SUCCESS_STR + paymentId);
    }

    public void logPaymentAuthFailure(String paymentId, AuthFailureReason failureReason) throws PersistenceException {
        String reason = failureReason + ":";
        auditDao.writeEntry(PAYMENT_STR + AUTH_STR + FAILURE_STR + reason + paymentId);
    }

    // CAPTURE
    public void logPaymentCaptureAttempt(String paymentId) throws PersistenceException {
        auditDao.writeEntry(PAYMENT_STR + CAPTURE_STR + ATTEMPT_STR + paymentId);
    }
    public void logPaymentCaptureSuccess(String paymentId) throws PersistenceException {
        auditDao.writeEntry(PAYMENT_STR + CAPTURE_STR + SUCCESS_STR + paymentId);
    }
    public void logPaymentCaptureFailure(String paymentId) throws PersistenceException {
        auditDao.writeEntry(PAYMENT_STR + CAPTURE_STR + FAILURE_STR + paymentId);
    }



    // ORDER related

    public void logOrderCreateAttempt(String orderId) throws PersistenceException {
        auditDao.writeEntry(ORDER_STR + CREATE_STR + ATTEMPT_STR + orderId);
    }



    // INVENTORY related

}
