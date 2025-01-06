package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.merchant.CashbackType;
import org.poo.bank.merchant.MerchantType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class AddMerchant extends BankOperation<Void> {
    @NonNull
    private final String name;
    @NonNull
    private final Integer id;
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final MerchantType type;
    @NonNull
    private final CashbackType cashbackType;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        context.merchantService()
                .createMerchant(name, id, accountIban, type, cashbackType);

        return BankOperationResult.success();
    }
}
