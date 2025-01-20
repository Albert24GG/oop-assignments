package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.type.Currency;

@Builder
@RequiredArgsConstructor
public final class RegisterExchangeRate extends BankOperation<Void> {
    @NonNull
    private final Currency from;
    @NonNull
    private final Currency to;
    @NonNull
    private final Double rate;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        if (rate <= 0) {
            throw new BankOperationException(BankErrorType.INVALID_ARGUMENT,
                    "Exchange rate must be positive");
        }

        context.currencyExchangeService().updateExchangeRate(from, to, rate);
        return BankOperationResult.success();
    }
}
