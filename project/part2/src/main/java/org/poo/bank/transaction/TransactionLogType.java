package org.poo.bank.transaction;

public enum TransactionLogType {
    GENERIC,
    FAILED,
    ACCOUNT_CREATION,
    CARD_OPERATION,
    INTEREST_CHANGE,
    INTEREST_INCOME,
    CARD_PAYMENT,
    SPLIT_PAYMENT,
    TRANSFER,
    SAVINGS_WITHDRAWAL,
    UPGRADE_PLAN,
    CASH_WITHDRAWAL,
}
