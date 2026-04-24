package domain.model.order;

public enum OrderStatus {
    CREATED, //order intially created - has payment attached but not auth yet
    READY, //order ready - user payment has been auth, but stock not yet reserved
    RESERVED, //stock has been reserved - need to capture payment
    PAID, // payment captured - now its on the merchant to fulfil the order
    PROCESSING, // process of fulfilling the order - building, in shipping, etc.
    FULFILLED, // order fulfilled - customer has their order - but it is still refundable

    COMPLETED, // order fully completed - customer can no longer refund payment

    CANCELLED, // order canceled BEFORE PAID state - no stock, auth declined, etc.

    REFUND_REQUESTED, // order reverted AFTER PAYMENT RECEIVED - waiting on confirmation
    REFUND_CONFIRMED, // merchant has confirmed that they have received goods back or are otherwise ready to give customer refund
    REFUNDED, // order refunded - merchant has sent money back to customer - in finished state like CANCELLED and COMPLETED
}
