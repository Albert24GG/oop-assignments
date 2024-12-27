package org.poo.bank.operation;


import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Builder(access = lombok.AccessLevel.PRIVATE)
@Getter
public class BankOperationResult<T> {
    private final boolean success;
    private final BankErrorType errorType;
    private final String message;
    @Builder.Default
    private final Optional<T> payload = Optional.empty();

    /**
     * Creates a new bank operation success result
     */
    public static <T> BankOperationResult<T> success() {
        return BankOperationResult.<T>builder()
                .success(true)
                .build();
    }

    /**
     * Creates a new bank operation success result with a payload
     */
    public static <T> BankOperationResult<T> success(final T payload) {
        return BankOperationResult.<T>builder()
                .success(true)
                .payload(Optional.of(payload))
                .build();
    }

    /**
     * Creates a new bank operation error result
     *
     * @param errorType the error type
     */
    public static <T> BankOperationResult<T> error(final BankErrorType errorType) {
        return BankOperationResult.<T>builder()
                .success(false)
                .errorType(errorType)
                .message(errorType.getMessage())
                .build();
    }

    /**
     * Creates a new bank operation error result with a message
     *
     * @param errorType the error type
     * @param message   the error message
     */
    public static <T> BankOperationResult<T> error(final BankErrorType errorType,
                                                   final String message) {
        return BankOperationResult.<T>builder()
                .success(false)
                .errorType(errorType)
                .message(message)
                .build();
    }

}
