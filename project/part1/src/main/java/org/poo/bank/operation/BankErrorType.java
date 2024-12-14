package org.poo.bank.operation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BankErrorType {
    INTERNAL_ERROR("Internal error"),
    ACCOUNT_NOT_FOUND("Account not found"),
    USER_NOT_FOUND("User not found"),
    CARD_NOT_FOUND("Card not found"),
    USER_NOT_ACCOUNT_OWNER("User is not the owner of the account"),
    USER_NOT_CARD_OWNER("User is not the owner of the card"),
    CARD_CREATION_FAILED("Card creation failed"),
    ACCOUNT_DELETION_FAILED("Account couldn't be deleted - see org.poo.transactions for details"),
    UNAUTHORIZED_OPERATION("Unauthorized operation"),
    INVALID_ARGUMENT("Invalid argument"),
    INVALID_OPERATION("Invalid operation"),
    CARD_FROZEN("Card is frozen");

    private final String message;
}
