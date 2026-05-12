package me.jamie.paymentspractice.data.record;

import java.util.List;

public record OrderRecord(String id, int statusOrdinal, String paymentId, List<LineItemRecord> items) {

}
