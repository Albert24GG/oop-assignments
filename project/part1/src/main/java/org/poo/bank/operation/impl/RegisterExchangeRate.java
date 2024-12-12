package org.poo.bank.operation.impl;

import lombok.Builder;
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
    private final Currency from;
    private final Currency to;
    private final double rate;

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
