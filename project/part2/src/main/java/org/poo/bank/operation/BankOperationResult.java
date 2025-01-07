package org.poo.bank.operation;


import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Builder(access = lombok.AccessLevel.PRIVATE)
@Getter
public class BankOperationResult<T> {
    private final boolean success;
    private final BankErrorType errorType;
    private final boolean silentError;
    private final String message;
    @Builder.Default
    private final T payload;

    public Optional<T> getPayload() {
        return Optional.ofNullable(payload);
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

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
                .payload(payload)
                .build();
    }

    /**
     * Creates a new bank operation error result
     * The error in this case is not silent
     *
     * @param errorType the error type
     */
    public static <T> BankOperationResult<T> error(final BankErrorType errorType) {
        return BankOperationResult.<T>builder()
                .success(false)
                .errorType(errorType)
                .message(errorType.getMessage())
                .silentError(false)
                .build();
    }

    /**
     * Creates a new bank operation error result with a message
     * The error in this case is not silent
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
                .silentError(false)
                .build();
    }

    /**
     * Creates a new bank operation error result
     * The error in this case is silent
     *
     * @param errorType the error type
     */
    public static <T> BankOperationResult<T> silentError(final BankErrorType errorType) {
        return BankOperationResult.<T>builder()
                .success(false)
                .errorType(errorType)
                .message(errorType.getMessage())
                .silentError(true)
                .build();
    }

    /**
     * Creates a new bank operation error result with a message
     * The error in this case is silent
     *
     * @param errorType the error type
     * @param message   the error message
     */
    public static <T> BankOperationResult<T> silentError(final BankErrorType errorType,
                                                         final String message) {
        return BankOperationResult.<T>builder()
                .success(false)
                .errorType(errorType)
                .message(message)
                .silentError(true)
                .build();
    }

}
