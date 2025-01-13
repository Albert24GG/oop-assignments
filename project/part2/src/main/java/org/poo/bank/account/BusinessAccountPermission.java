package org.poo.bank.account;

public enum BusinessAccountPermission {
    DEPOSIT,
    CARD_PAYMENT,
    TRANSFER,
    CARD_CREATION,
    CARD_DELETION_SAME_OWNER,
    CARD_DELETION_DIFFERENT_OWNER,
    SET_SPENDING_LIMIT,
    SET_DEPOSIT_LIMIT,
    SET_MINIMUM_BALANCE,
}

