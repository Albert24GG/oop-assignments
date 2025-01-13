package org.poo.bank.log;

public enum AuditLogType {
    // Account operations
    ACCOUNT_CREATION,
    ACCOUNT_DELETION,
    ACCOUNT_PLAN_UPDATE,

    // Card operations
    CARD_CREATION,
    CARD_DELETION,
    CARD_STATUS_CHECK,

    // Transaction operations
    CARD_PAYMENT,
    TRANSFER,
    CASH_WITHDRAWAL,
    SPLIT_PAYMENT,
    DEPOSIT,


    // Interest related operations
    INTEREST_RATE_UPDATE,
    INTEREST_CLAIM,

    // Savings operations
    SAVINGS_WITHDRAWAL,
}
