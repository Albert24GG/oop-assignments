package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class SetAccountMinBalance extends BankOperation<Void> {
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final Double minBalance;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        context.bankAccService().setMinBalance(bankAccount, minBalance);
        return BankOperationResult.success();
    }
}
