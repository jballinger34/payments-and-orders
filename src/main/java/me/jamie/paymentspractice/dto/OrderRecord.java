package me.jamie.paymentspractice.dto;

import java.util.List;

public record OrderRecord(String id, int statusOrdinal, String paymentId, List<LineItemRecord> items) {

}
