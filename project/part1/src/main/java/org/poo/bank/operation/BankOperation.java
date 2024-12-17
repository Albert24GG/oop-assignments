package org.poo.bank.operation;

import lombok.NonNull;
import org.poo.bank.Bank;

public abstract class BankOperation<T> {
    /**
     * Execute the operation
     *
     * @param context the bank operation context used for performing the operation
     * @return the result of the operation
     * @throws BankOperationException with a specific error type if the operation fails
     */
    public final BankOperationResult<T> execute(final BankOperationContext context)
            throws BankOperationException {
        try {
            BankOperationRegistry.validateOperation(this);
            return internalExecute(context);
        } catch (BankOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new BankOperationException(BankErrorType.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * Convenience method to process the operation by a bank, useful for chaining
     * This is equivalent to calling {@code bank.processOperation(this)}
     *
     * @param bank the bank to process the operation
     * @return the result of the operation
     * @throws BankOperationException with a specific error type if the operation fails
     */
    public final BankOperationResult<T> processBy(@NonNull final Bank bank)
            throws BankOperationException {
        return bank.processOperation(this);
    }

    protected abstract BankOperationResult<T> internalExecute(BankOperationContext context)
            throws BankOperationException;
}
