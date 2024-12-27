package org.poo.bank.operation;

import lombok.Getter;

@Getter
public class BankOperationException extends RuntimeException {
    private final BankErrorType errorType;

    /**
     * Creates a new bank operation exception
     *
     * @param errorType the error type
     */
    public BankOperationException(final BankErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    /**
     * Creates a new bank operation exception
     *
     * @param errorType the error type
     * @param message   the error message
     */
    public BankOperationException(final BankErrorType errorType, final String message) {
        super(message);
        this.errorType = errorType;
    }
}
