package org.poo.bank.operation;

import org.poo.bank.operation.impl.AddFunds;
import org.poo.bank.operation.impl.AddMerchant;
import org.poo.bank.operation.impl.CardPaymentRequest;
import org.poo.bank.operation.impl.CashWithdraw;
import org.poo.bank.operation.impl.ChangeInterestRate;
import org.poo.bank.operation.impl.CheckCardStatus;
import org.poo.bank.operation.impl.CollectInterest;
import org.poo.bank.operation.impl.CreateBankAccount;
import org.poo.bank.operation.impl.CreateCard;
import org.poo.bank.operation.impl.CreateUserAccount;
import org.poo.bank.operation.impl.DeleteBankAccount;
import org.poo.bank.operation.impl.DeleteCard;
import org.poo.bank.operation.impl.GetAllUsers;
import org.poo.bank.operation.impl.GetUserTransactions;
import org.poo.bank.operation.impl.RegisterExchangeRate;
import org.poo.bank.operation.impl.SetAccountAlias;
import org.poo.bank.operation.impl.SetAccountMinBalance;
import org.poo.bank.operation.impl.SpendingsReportQuery;
import org.poo.bank.operation.impl.SplitPaymentRequest;
import org.poo.bank.operation.impl.TransactionsReportQuery;
import org.poo.bank.operation.impl.TransferRequest;
import org.poo.bank.operation.impl.UpgradeServicePlan;
import org.poo.bank.operation.impl.WithdrawSavings;

import java.util.Arrays;
import java.util.Set;

final class BankOperationRegistry {
    private static final Set<Class<? extends BankOperation<?>>> ALLOWED_OPERATIONS = Set.copyOf(
            Arrays.asList(
                    GetUserTransactions.class,
                    GetAllUsers.class,
                    RegisterExchangeRate.class,
                    CreateUserAccount.class,
                    CreateBankAccount.class,
                    CreateCard.class,
                    DeleteBankAccount.class,
                    DeleteCard.class,
                    RegisterExchangeRate.class,
                    AddFunds.class,
                    CardPaymentRequest.class,
                    TransferRequest.class,
                    SplitPaymentRequest.class,
                    SetAccountMinBalance.class,
                    SetAccountAlias.class,
                    CheckCardStatus.class,
                    ChangeInterestRate.class,
                    TransactionsReportQuery.class,
                    SpendingsReportQuery.class,
                    CollectInterest.class,
                    WithdrawSavings.class,
                    UpgradeServicePlan.class,
                    CashWithdraw.class,
                    AddMerchant.class
            )
    );

    private BankOperationRegistry() {
    }

    static void validateOperation(final BankOperation<?> operation) throws BankOperationException {
        if (!ALLOWED_OPERATIONS.contains(operation.getClass())) {
            throw new BankOperationException(BankErrorType.UNAUTHORIZED_OPERATION);
        }
    }
}
