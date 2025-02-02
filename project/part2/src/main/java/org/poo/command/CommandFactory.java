package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Bank;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.BusinessAccountRole;
import org.poo.bank.account.UserView;
import org.poo.bank.card.CardType;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.impl.AcceptSplitPayment;
import org.poo.bank.operation.impl.AddBusinessAssociate;
import org.poo.bank.operation.impl.AddFunds;
import org.poo.bank.operation.impl.BusinessReportQuery;
import org.poo.bank.operation.impl.CardPaymentRequest;
import org.poo.bank.operation.impl.CashWithdraw;
import org.poo.bank.operation.impl.ChangeDepositLimit;
import org.poo.bank.operation.impl.ChangeInterestRate;
import org.poo.bank.operation.impl.ChangeSpendingLimit;
import org.poo.bank.operation.impl.CheckCardStatus;
import org.poo.bank.operation.impl.CollectInterest;
import org.poo.bank.operation.impl.CreateBankAccount;
import org.poo.bank.operation.impl.CreateCard;
import org.poo.bank.operation.impl.DeleteBankAccount;
import org.poo.bank.operation.impl.DeleteCard;
import org.poo.bank.operation.impl.GetAllUsers;
import org.poo.bank.operation.impl.GetUserTransactions;
import org.poo.bank.operation.impl.RejectSplitPayment;
import org.poo.bank.operation.impl.SetAccountAlias;
import org.poo.bank.operation.impl.SetAccountMinBalance;
import org.poo.bank.operation.impl.SpendingsReportQuery;
import org.poo.bank.operation.impl.SplitPaymentRequest;
import org.poo.bank.operation.impl.TransactionsReportQuery;
import org.poo.bank.operation.impl.TransferRequest;
import org.poo.bank.operation.impl.UpgradeServicePlan;
import org.poo.bank.operation.impl.WithdrawSavings;
import org.poo.bank.report.SpendingsReport;
import org.poo.bank.report.TransactionsReport;
import org.poo.bank.report.business.BusinessReport;
import org.poo.bank.report.business.BusinessReportType;
import org.poo.bank.splitPayment.SplitPaymentType;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;
import org.poo.bank.type.Location;
import org.poo.fileio.CommandInput;
import org.poo.bank.servicePlan.ServicePlanType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class CommandFactory {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, Function<CommandInput, Command>> COMMANDS =
            Map.ofEntries(
                    Map.entry("printUsers", input -> {
                        BankOperation<List<UserView>> operation = new GetAllUsers();
                        return new CommandWithResult<>(input, operation);
                    }),

                    Map.entry("printTransactions", input -> {
                        BankOperation<List<AuditLogView>> operation =
                                new GetUserTransactions(Email.of(input.getEmail()));
                        return new CommandWithResult<>(input, operation);
                    }),

                    Map.entry("addAccount", input -> {
                        BankOperation<Void> operation = CreateBankAccount.builder()
                                .ownerEmail(Email.of(input.getEmail()))
                                .currency(Currency.of(input.getCurrency()))
                                .type(BankAccountType.of(input.getAccountType()))
                                .interestRate(input.getInterestRate())
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWithouResultOrError<>(input, operation);
                    }),

                    Map.entry("createCard", input -> {
                        BankOperation<Void> operation = CreateCard.builder()
                                .userEmail(Email.of(input.getEmail()))
                                .accountIban(IBAN.of(input.getAccount()))
                                .type(CardType.DEBIT)
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWithouResultOrError<>(input, operation);
                    }),

                    Map.entry("createOneTimeCard", input -> {
                        BankOperation<Void> operation = CreateCard.builder()
                                .userEmail(Email.of(input.getEmail()))
                                .accountIban(IBAN.of(input.getAccount()))
                                .type(CardType.SINGLE_USE)
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWithouResultOrError<>(input, operation);
                    }),

                    Map.entry("addFunds", input -> {
                        BankOperation<Void> operation = new AddFunds(IBAN.of(input.getAccount()),
                                Email.of(input.getEmail()),
                                input.getAmount(), input.getTimestamp());
                        return new CommandWithouResultOrError<>(input, operation);
                    }),

                    Map.entry("deleteAccount", DeleteAccountCmd::new),

                    Map.entry("deleteCard", input -> {
                        BankOperation<Void> operation =
                                new DeleteCard(CardNumber.of(input.getCardNumber()),
                                        Email.of(input.getEmail()),
                                        input.getTimestamp());
                        return new CommandWithouResultOrError<>(input, operation);
                    }),

                    Map.entry("setMinimumBalance", input -> {
                        BankOperation<Void> operation =
                                new SetAccountMinBalance(IBAN.of(input.getAccount()),
                                        input.getMinBalance());
                        return new CommandWitError<>(input, operation, "error");
                    }),

                    Map.entry("setAlias", input -> {
                        BankOperation<Void> operation =
                                new SetAccountAlias(Email.of(input.getEmail()),
                                        IBAN.of(input.getAccount()), input.getAlias(),
                                        input.getTimestamp());
                        return new CommandWithouResultOrError<>(input, operation);
                    }),

                    Map.entry("payOnline", input -> {
                        BankOperation<Void> operation = CardPaymentRequest.builder()
                                .cardNumber(CardNumber.of(input.getCardNumber()))
                                .userEmail(Email.of(input.getEmail()))
                                .amount(input.getAmount())
                                .description(input.getDescription())
                                .currency(Currency.of(input.getCurrency()))
                                .merchantName(input.getCommerciant())
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("sendMoney", input -> {
                        BankOperation<Void> operation = TransferRequest.builder()
                                .senderIdentifier(input.getAccount())
                                .userEmail(Email.of(input.getEmail()))
                                .receiverIdentifier(input.getReceiver())
                                .description(input.getDescription())
                                .amount(input.getAmount())
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("checkCardStatus", input -> {
                        BankOperation<Void> operation =
                                new CheckCardStatus(CardNumber.of(input.getCardNumber()),
                                        input.getTimestamp());
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("changeInterestRate", input -> {
                        BankOperation<Void> operation =
                                new ChangeInterestRate(IBAN.of(input.getAccount()),
                                        input.getInterestRate(), input.getTimestamp());
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("splitPayment", input -> {
                        SplitPaymentType type = SplitPaymentType.of(input.getSplitPaymentType());
                        List<Double> amountPerAccount = switch (type) {
                            case EQUAL -> Collections.nCopies(input.getAccounts().size(),
                                    input.getAmount() / input.getAccounts().size());
                            case CUSTOM -> input.getAmountForUsers();
                        };


                        BankOperation<Void> operation = SplitPaymentRequest.builder()
                                .involvedAccounts(
                                        input.getAccounts().stream().map(IBAN::of).toList())
                                .currency(Currency.of(input.getCurrency()))
                                .type(type)
                                .amountPerAccount(amountPerAccount)
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWithouResultOrError<>(input, operation);
                    }),

                    Map.entry("acceptSplitPayment", input -> {
                        BankOperation<Void> operation = AcceptSplitPayment.builder()
                                .ownerEmail(Email.of(input.getEmail()))
                                .timestamp(input.getTimestamp())
                                .splitPaymentType(SplitPaymentType.of(input.getSplitPaymentType()))
                                .build();
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("rejectSplitPayment", input -> {
                        BankOperation<Void> operation = RejectSplitPayment.builder()
                                .ownerEmail(Email.of(input.getEmail()))
                                .timestamp(input.getTimestamp())
                                .splitPaymentType(SplitPaymentType.of(input.getSplitPaymentType()))
                                .build();
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("report", input -> {
                        BankOperation<TransactionsReport> operation =
                                TransactionsReportQuery.builder()
                                        .accountIban(IBAN.of(input.getAccount()))
                                        .startTimestamp(input.getStartTimestamp())
                                        .endTimestamp(input.getEndTimestamp())
                                        .build();

                        return new CommandWithResult<>(input, operation);
                    }),

                    Map.entry("spendingsReport", SpendingsReportCmd::new),

                    Map.entry("addInterest", input -> {
                        BankOperation<Void> operation =
                                new CollectInterest(IBAN.of(input.getAccount()),
                                        input.getTimestamp());
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("withdrawSavings", input -> {
                        BankOperation<Void> operation = WithdrawSavings.builder()
                                .accountIban(IBAN.of(input.getAccount()))
                                .amount(input.getAmount())
                                .currency(Currency.of(input.getCurrency()))
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("upgradePlan", input -> {
                        BankOperation<Void> operation =
                                new UpgradeServicePlan(ServicePlanType.of(input.getNewPlanType()),
                                        IBAN.of(input.getAccount()), input.getTimestamp());
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("cashWithdrawal", input -> {
                        BankOperation<Void> operation = CashWithdraw.builder()
                                .cardNumber(CardNumber.of(input.getCardNumber()))
                                .amount(input.getAmount())
                                .ownerEmail(Email.of(input.getEmail()))
                                .location(Location.of(input.getLocation()))
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("addNewBusinessAssociate", input -> {
                        BankOperation<Void> operation = AddBusinessAssociate.builder()
                                .accountIban(IBAN.of(input.getAccount()))
                                .role(BusinessAccountRole.of(input.getRole()))
                                .userEmail(Email.of(input.getEmail()))
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("changeSpendingLimit", input -> {
                        BankOperation<Void> operation = ChangeSpendingLimit.builder()
                                .accountIban(IBAN.of(input.getAccount()))
                                .newLimit(input.getAmount())
                                .userEmail(Email.of(input.getEmail()))
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("changeDepositLimit", input -> {
                        BankOperation<Void> operation = ChangeDepositLimit.builder()
                                .accountIban(IBAN.of(input.getAccount()))
                                .newLimit(input.getAmount())
                                .userEmail(Email.of(input.getEmail()))
                                .timestamp(input.getTimestamp())
                                .build();
                        return new CommandWitError<>(input, operation, "description");
                    }),

                    Map.entry("businessReport", input -> {
                        BankOperation<BusinessReport> operation = BusinessReportQuery.builder()
                                .accountIban(IBAN.of(input.getAccount()))
                                .type(BusinessReportType.of(input.getType()))
                                .startTimestamp(input.getStartTimestamp())
                                .endTimestamp(input.getEndTimestamp())
                                .build();
                        return new CommandWithResult<>(input, operation);
                    })
            );

    private CommandFactory() {
    }

    /**
     * Get a command.
     *
     * @param command the command
     * @param input   the input
     * @return the command, or {@code null} if the command does not exist
     */
    public static Command getCommand(final String command, final CommandInput input) {
        return COMMANDS.getOrDefault(command, i -> null).apply(input);
    }

    /**
     * Command without any result, but with the possibility of an error.
     *
     * @param <R> the type of the operation result
     */
    private static final class CommandWitError<R> extends Command {
        private final String errorFieldName;
        private final BankOperation<R> operation;

        private CommandWitError(final CommandInput input, final BankOperation<R> operation,
                                final String errorFieldName) {
            super(input);
            this.errorFieldName = errorFieldName;
            this.operation = operation;
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            BankOperationResult<R> result = bank.processOperation(operation);
            return result.isSuccess() || result.isSilentError()
                    ? Optional.empty()
                    : Optional.of(
                    CommandOutput.builder()
                            .command(getInput().getCommand())
                            .timestamp(getInput().getTimestamp())
                            .output(MAPPER.createObjectNode()
                                    .put(errorFieldName, result.getMessage()
                                            .orElseGet(() -> "Encountered an error"))
                                    .put("timestamp", getInput().getTimestamp()))
                            .build());
        }
    }

    /**
     * Command that can output either a result or an error.
     *
     * @param <R> the type of the operation result
     */
    private static final class CommandWithouResultOrError<R> extends Command {
        private final BankOperation<R> operation;

        private CommandWithouResultOrError(final CommandInput input,
                                           final BankOperation<R> operation) {
            super(input);
            this.operation = operation;
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            bank.processOperation(operation);
            return Optional.empty();
        }
    }

    /**
     * Command that outputs a result, without the possibility of an error.
     *
     * @param <R> the type of the operation result
     */
    private static final class CommandWithResult<R> extends Command {
        private final BankOperation<R> operation;

        private CommandWithResult(final CommandInput input, final BankOperation<R> operation) {
            super(input);
            this.operation = operation;
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            BankOperationResult<R> result = bank.processOperation(operation);
            return result.getPayload()
                    .map(payload -> CommandOutput.builder()
                            .command(getInput().getCommand())
                            .timestamp(getInput().getTimestamp())
                            .output(MAPPER.valueToTree(payload))
                            .build());
        }
    }

    //--------------------------------------------------------------------------------
    // Commands with specific handling
    //--------------------------------------------------------------------------------

    private static final class DeleteAccountCmd extends Command {
        private DeleteAccountCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            ObjectNode output = MAPPER.createObjectNode();
            BankOperationResult<Void> result = DeleteBankAccount.builder()
                    .ownerEmail(Email.of(input.getEmail()))
                    .accountIban(IBAN.of(input.getAccount()))
                    .timestamp(input.getTimestamp())
                    .build()
                    .processBy(bank);
            if (result.isSuccess()) {
                output.put("success", "Account deleted")
                        .put("timestamp", input.getTimestamp());
            } else {
                output.put("error", result.getMessage().orElse("Encountered an error"))
                        .put("timestamp", input.getTimestamp());
            }
            return Optional.of(
                    CommandOutput.builder()
                            .command("deleteAccount")
                            .timestamp(input.getTimestamp())
                            .output(output)
                            .build());
        }
    }

    private static final class SpendingsReportCmd extends Command {
        private SpendingsReportCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            BankOperationResult<SpendingsReport> result = SpendingsReportQuery.builder()
                    .accountIban(IBAN.of(input.getAccount()))
                    .startTimestamp(input.getStartTimestamp())
                    .endTimestamp(input.getEndTimestamp())
                    .build()
                    .processBy(bank);

            var commandOutputBuilder =
                    CommandOutput.builder()
                            .command(getInput().getCommand())
                            .timestamp(getInput().getTimestamp());

            return Optional.of(result.getPayload()
                    .map(payload -> commandOutputBuilder.output(MAPPER.valueToTree(payload)))
                    .orElseGet(() -> {
                        if (result.getErrorType() == BankErrorType.INVALID_OPERATION) {
                            return commandOutputBuilder.output(MAPPER.createObjectNode()
                                    .put("error", result.getMessage().get()));
                        } else {
                            return commandOutputBuilder.output(MAPPER.createObjectNode()
                                    .put("description", result.getMessage().get())
                                    .put("timestamp", getInput().getTimestamp()));
                        }
                    })
                    .build());
        }
    }
}
