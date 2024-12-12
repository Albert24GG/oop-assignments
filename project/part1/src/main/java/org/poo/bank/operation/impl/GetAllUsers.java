package org.poo.bank.operation.impl;

import org.poo.bank.account.UserView;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;

import java.util.List;

public final class GetAllUsers extends BankOperation<List<UserView>> {
    @Override
    protected BankOperationResult<List<UserView>> internalExecute(
            final BankOperationContext context)
            throws BankOperationException {

        return BankOperationResult.success(
                context.userService().getUsers().stream().map(UserView::from).toList());
    }
}
