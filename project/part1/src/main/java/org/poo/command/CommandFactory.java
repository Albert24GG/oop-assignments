package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Bank;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.UserView;
import org.poo.bank.card.CardType;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.impl.AddFunds;
import org.poo.bank.operation.impl.CardPaymentRequest;
import org.poo.bank.operation.impl.ChangeInterestRate;
import org.poo.bank.operation.impl.CheckCardStatus;
import org.poo.bank.operation.impl.CollectInterest;
import org.poo.bank.operation.impl.CreateBankAccount;
import org.poo.bank.operation.impl.CreateCard;
import org.poo.bank.operation.impl.DeleteBankAccount;
import org.poo.bank.operation.impl.DeleteCard;
import org.poo.bank.operation.impl.GetAllUsers;
import org.poo.bank.operation.impl.GetUserTransactions;
import org.poo.bank.operation.impl.SetAccountAlias;
import org.poo.bank.operation.impl.SetAccountMinBalance;
import org.poo.bank.operation.impl.SpendingsReportQuery;
import org.poo.bank.operation.impl.SplitPaymentRequest;
import org.poo.bank.operation.impl.TransactionsReportQuery;
import org.poo.bank.operation.impl.TransferRequest;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;
import org.poo.fileio.CommandInput;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class CommandFactory {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, Function<CommandInput, Command>> COMMANDS =
            Map.ofEntries(
                    Map.entry("printUsers", PrintUsersCmd::new),
                    Map.entry("printTransactions", PrintTransactionsCmd::new),
                    Map.entry("addAccount", AddAccountCmd::new),
                    Map.entry("createCard", i -> new CreateCardCmd(i, "DEBIT")),
                    Map.entry("createOneTimeCard", i -> new CreateCardCmd(i, "SINGLE_USE")),
                    Map.entry("addFunds", AddFundsCmd::new),
                    Map.entry("deleteAccount", DeleteAccountCmd::new),
                    Map.entry("deleteCard", DeleteCardCmd::new),
                    Map.entry("setMinimumBalance", SetMinBalanceCmd::new),
                    Map.entry("setAlias", SetAliasCmd::new),
                    Map.entry("payOnline", OnlinePaymentCmd::new),
                    Map.entry("sendMoney", SendMoneyCmd::new),
                    Map.entry("checkCardStatus", CheckCardStatusCmd::new),
                    Map.entry("changeInterestRate", ChangeInterestRateCmd::new),
                    Map.entry("splitPayment", SplitPaymentCmd::new),
                    Map.entry("report", ReportCmd::new),
                    Map.entry("spendingsReport", SpendingsReportCmd::new),
                    Map.entry("addInterest", AddInterestCmd::new)
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

    private static final class PrintUsersCmd extends Command {
        private PrintUsersCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            BankOperationResult<List<UserView>> result = bank.processOperation(new GetAllUsers());
            return Optional.of(result.isSuccess())
                    .map(success -> CommandOutput.builder()
                            .command(getInput().getCommand())
                            .timestamp(getInput().getTimestamp())
                            .output(MAPPER.valueToTree(result.getPayload().get()))
                            .build());
        }
    }

    private static final class PrintTransactionsCmd extends Command {
        private PrintTransactionsCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            var result = bank.processOperation(new GetUserTransactions(Email.of(input.getEmail())));
            return Optional.of(result.isSuccess())
                    .map(success -> CommandOutput.builder()
                            .command(getInput().getCommand())
                            .timestamp(getInput().getTimestamp())
                            .output(MAPPER.valueToTree(result.getPayload().get()))
                            .build());
        }
    }

    private static final class AddAccountCmd extends Command {
        private AddAccountCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            var operation = CreateBankAccount.builder()
                    .ownerEmail(Email.of(input.getEmail()))
                    .currency(Currency.of(input.getCurrency()))
                    .type(BankAccountType.of(input.getAccountType()))
                    .interestRate(input.getInterestRate())
                    .timestamp(input.getTimestamp())
                    .build();
            bank.processOperation(operation);
            return Optional.empty();
        }
    }

    private static final class CreateCardCmd extends Command {
        private final String cardType;

        private CreateCardCmd(final CommandInput input, final String cardType) {
            super(input);
            this.cardType = cardType;
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            CreateCard.builder()
                    .ownerEmail(Email.of(input.getEmail()))
                    .accountIban(IBAN.of(input.getAccount()))
                    .type(CardType.of(cardType))
                    .timestamp(input.getTimestamp())
                    .build()
                    .processBy(bank);
            return Optional.empty();
        }
    }

    private static final class AddFundsCmd extends Command {
        private AddFundsCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            AddFunds.builder()
                    .accountIban(IBAN.of(input.getAccount()))
                    .amount(input.getAmount())
                    .timestamp(input.getTimestamp())
                    .build()
                    .processBy(bank);
            return Optional.empty();
        }
    }

    private static final class DeleteAccountCmd extends Command {
        private DeleteAccountCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            ObjectNode output = MAPPER.createObjectNode();
            var result = DeleteBankAccount.builder()
                    .ownerEmail(Email.of(input.getEmail()))
                    .accountIban(IBAN.of(input.getAccount()))
                    .timestamp(input.getTimestamp())
                    .build()
                    .processBy(bank);
            if (result.isSuccess()) {
                output.put("success", "Account deleted")
                        .put("timestamp", input.getTimestamp());
            } else {
                output.put("error", result.getMessage())
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

    private static final class DeleteCardCmd extends Command {
        private DeleteCardCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            DeleteCard.builder()
                    .cardNumber(CardNumber.of(input.getCardNumber()))
                    .timestamp(input.getTimestamp())
                    .build()
                    .processBy(bank);
            return Optional.empty();
        }
    }

    private static final class SetMinBalanceCmd extends Command {
        private SetMinBalanceCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            var result = SetAccountMinBalance.builder()
                    .accountIban(IBAN.of(input.getAccount()))
                    .minBalance(input.getMinBalance())
                    .build()
                    .processBy(bank);
            return result.isSuccess()
                    ? Optional.empty()
                    : Optional.of(
                    CommandOutput.builder()
                            .command(input.getCommand())
                            .timestamp(input.getTimestamp())
                            .output(MAPPER.createObjectNode()
                                    .put("error", result.getMessage())
                                    .put("timestamp", input.getTimestamp()))
                            .build());
        }

    }

    private static final class OnlinePaymentCmd extends Command {
        private OnlinePaymentCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            var result = CardPaymentRequest.builder()
                    .cardNumber(CardNumber.of(getInput().getCardNumber()))
                    .ownerEmail(Email.of(getInput().getEmail()))
                    .amount(getInput().getAmount())
                    .description(getInput().getDescription())
                    .currency(Currency.of(getInput().getCurrency()))
                    .merchant(getInput().getCommerciant())
                    .timestamp(getInput().getTimestamp())
                    .build()
                    .processBy(bank);

            return result.isSuccess()
                    ? Optional.empty()
                    : Optional.of(
                    CommandOutput.builder()
                            .command(getInput().getCommand())
                            .timestamp(getInput().getTimestamp())
                            .output(MAPPER.createObjectNode()
                                    .put("description", result.getMessage())
                                    .put("timestamp", getInput().getTimestamp()))
                            .build());

        }
    }

    private static final class SendMoneyCmd extends Command {
        private SendMoneyCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            TransferRequest.builder()
                    .senderAccount(IBAN.of(getInput().getAccount()))
                    .receiverAccount(getInput().getReceiver())
                    .description(getInput().getDescription())
                    .amount(getInput().getAmount())
                    .timestamp(getInput().getTimestamp())
                    .build()
                    .processBy(bank);

            return Optional.empty();
        }
    }

    private static final class SetAliasCmd extends Command {
        private SetAliasCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            SetAccountAlias.builder()
                    .ownerEmail(Email.of(input.getEmail()))
                    .accountIban(IBAN.of(input.getAccount()))
                    .alias(input.getAlias())
                    .build()
                    .processBy(bank);
            return Optional.empty();
        }
    }

    private static final class CheckCardStatusCmd extends Command {
        private CheckCardStatusCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();

            var result = CheckCardStatus.builder()
                    .cardNumber(CardNumber.of(input.getCardNumber()))
                    .timestamp(input.getTimestamp())
                    .build()
                    .processBy(bank);

            return result.isSuccess()
                    ? Optional.empty()
                    : Optional.of(
                    CommandOutput.builder()
                            .command(input.getCommand())
                            .timestamp(input.getTimestamp())
                            .output(MAPPER.createObjectNode()
                                    .put("description", result.getMessage())
                                    .put("timestamp", input.getTimestamp()))
                            .build());
        }
    }

    private static final class ChangeInterestRateCmd extends Command {
        private ChangeInterestRateCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            var result = ChangeInterestRate.builder()
                    .accountIban(IBAN.of(input.getAccount()))
                    .newInterestRate(input.getInterestRate())
                    .timestamp(input.getTimestamp())
                    .build()
                    .processBy(bank);
            return result.isSuccess()
                    ? Optional.empty()
                    : Optional.of(
                    CommandOutput.builder()
                            .command(input.getCommand())
                            .timestamp(input.getTimestamp())
                            .output(MAPPER.createObjectNode()
                                    .put("description", result.getMessage())
                                    .put("timestamp", input.getTimestamp()))
                            .build());
        }
    }

    private static final class SplitPaymentCmd extends Command {
        private SplitPaymentCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();

            SplitPaymentRequest.builder()
                    .involvedAccounts(
                            input.getAccounts().stream().map(IBAN::of).toList())
                    .currency(Currency.of(input.getCurrency()))
                    .amount(input.getAmount())
                    .timestamp(input.getTimestamp())
                    .build()
                    .processBy(bank);
            return Optional.empty();
        }
    }

    private static final class ReportCmd extends Command {
        private ReportCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            var result = TransactionsReportQuery.builder()
                    .accountIban(IBAN.of(input.getAccount()))
                    .startTimestamp(input.getStartTimestamp())
                    .endTimestamp(input.getEndTimestamp())
                    .build()
                    .processBy(bank);
            return Optional.of(
                    Optional.ofNullable(result.isSuccess() && result.getPayload().isPresent() ?
                                    result.getPayload().get() : null)
                            .map(payload -> CommandOutput.builder()
                                    .command(getInput().getCommand())
                                    .timestamp(getInput().getTimestamp())
                                    .output(MAPPER.valueToTree(payload))
                                    .build())
                            .orElseGet(() ->
                                    CommandOutput.builder()
                                            .command(getInput().getCommand())
                                            .timestamp(getInput().getTimestamp())
                                            .output(MAPPER.createObjectNode()
                                                    .put("description", result.getMessage())
                                                    .put("timestamp", getInput().getTimestamp()))
                                            .build()));
        }
    }

    private static final class SpendingsReportCmd extends Command {
        private SpendingsReportCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            var result = SpendingsReportQuery.builder()
                    .accountIban(IBAN.of(input.getAccount()))
                    .startTimestamp(input.getStartTimestamp())
                    .endTimestamp(input.getEndTimestamp())
                    .build()
                    .processBy(bank);
            return Optional.of(
                    Optional.ofNullable(result.isSuccess() && result.getPayload().isPresent() ?
                                    result.getPayload().get() : null)
                            .map(success -> CommandOutput.builder()
                                    .command(getInput().getCommand())
                                    .timestamp(getInput().getTimestamp())
                                    .output(MAPPER.valueToTree(result.getPayload().get()))
                                    .build())
                            .orElseGet(() ->
                                    CommandOutput.builder()
                                            .command(getInput().getCommand())
                                            .timestamp(getInput().getTimestamp())
                                            .output(MAPPER.createObjectNode()
                                                    .put("description", result.getMessage())
                                                    .put("timestamp", getInput().getTimestamp()))
                                            .build()));
        }
    }

    private static final class AddInterestCmd extends Command {
        private AddInterestCmd(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            var result = new CollectInterest(IBAN.of(input.getAccount())).processBy(bank);

            return result.isSuccess()
                    ? Optional.empty()
                    : Optional.of(
                    CommandOutput.builder()
                            .command(input.getCommand())
                            .timestamp(input.getTimestamp())
                            .output(MAPPER.createObjectNode()
                                    .put("description", result.getMessage())
                                    .put("timestamp", input.getTimestamp()))
                            .build());
        }
    }

}
