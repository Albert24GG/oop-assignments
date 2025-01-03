package org.poo.bank.transaction;

public enum TransactionLogType {
    GENERIC,
    FAILED,
    ACCOUNT_CREATION,
    CARD_OPERATION,
    INTEREST_OPERATION,
    CARD_PAYMENT,
    SPLIT_PAYMENT,
    TRANSFER,
    SAVINGS_WITHDRAW,
    UPGRADE_PLAN
}
