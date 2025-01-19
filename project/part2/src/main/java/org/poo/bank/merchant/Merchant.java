package org.poo.bank.merchant;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.servicePlan.ServicePlanType;
import org.poo.bank.type.IBAN;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Merchant {
    @Getter
    private final String name;
    @Getter
    private final int id;
    @Getter
    private final IBAN accountIban;
    @Getter
    private final MerchantType type;
    private CashbackStrategy cashbackStrategy;
    private final Map<BankAccount, BankAccountData> bankAccountData = new HashMap<>();


    @Builder
    private Merchant(final String name, final int id, final IBAN accountIban,
                     final MerchantType type,
                     final CashbackStrategy cashbackStrategy) {
        this.name = name;
        this.id = id;
        this.accountIban = accountIban;
        this.type = type;
        this.cashbackStrategy = cashbackStrategy;
    }

    /**
     * Creates a new builder with the given cashback type.
     * This is currently used for the transaction-based cashback strategy.
     *
     * @param cashbackType the cashback type to be used by the builder
     * @return a new builder with the given cashback type
     */
    static MerchantBuilder builderWithCashbackType(final CashbackType cashbackType) {
        return new MerchantBuilder() {
            @Override
            public Merchant build() {
                Merchant merchant = super.build();
                merchant.cashbackStrategy = cashbackType.createStrategy(merchant);
                return merchant;
            }
        };
    }

    /**
     * Creates a new builder with the given cashback strategy.
     * This is currently used for the spending-based cashback strategy.
     *
     * @param cashbackStrategy the cashback strategy to be used by the builder
     * @return a new builder with the given cashback strategy
     */
    static MerchantBuilder builderWithCashbackStrategy(
            final CashbackStrategy cashbackStrategy) {
        return new MerchantBuilder() {
            @Override
            public Merchant build() {
                Merchant merchant = super.build();
                merchant.cashbackStrategy = cashbackStrategy;
                return merchant;
            }
        };
    }

    private BankAccountData getBankAccountData(final BankAccount bankAccount) {
        return bankAccountData.computeIfAbsent(bankAccount, k -> new BankAccountData());
    }

    Optional<Cashback> registerTransaction(final BankAccount bankAccount, final double amount) {
        return cashbackStrategy.registerTransaction(bankAccount, amount);
    }

    private static final class BankAccountData {
        private int totalTransactions;
        private double totalAmount;

        public void registerTransaction(final double amount) {
            totalTransactions++;
            totalAmount += amount;
        }
    }

    @RequiredArgsConstructor
    final class TransactionBasedCashback implements CashbackStrategy {
        private static final Map<Integer, TransactionCashback> DISCOUNTS =
                Map.of(2, new TransactionCashback(0.02, MerchantType.FOOD),
                        5, new TransactionCashback(0.05, MerchantType.CLOTHES),
                        10, new TransactionCashback(0.1, MerchantType.TECH));

        /**
         * Creates a new instance of the TransactionBasedCashback class.
         *
         * @param merchant the merchant for which the cashback strategy is created
         * @return a new instance of the TransactionBasedCashback class
         */
        static TransactionBasedCashback create(final Merchant merchant) {
            return merchant.new TransactionBasedCashback();
        }

        @EqualsAndHashCode(callSuper = true)
        static final class TransactionCashback extends Cashback {
            TransactionCashback(final double percentage, final MerchantType applicableType) {
                super(percentage, applicableType);
            }

            @Override
            boolean isApplicableNow() {
                return false;
            }

            @Override
            boolean isApplicableOneTime() {
                return true;
            }
        }


        @Override
        public Optional<Cashback> registerTransaction(final BankAccount bankAccount,
                                                      final double amount) {
            BankAccountData data = getBankAccountData(bankAccount);
            data.registerTransaction(amount);

            // Return the discount if the user has reached the required number of transactions
            return Optional.ofNullable(DISCOUNTS.get(data.totalTransactions));
        }
    }

    @RequiredArgsConstructor
    static final class SpendingBasedCashback implements CashbackStrategy {
        // The thresholds for the spending-based cashback in RON
        private static final List<Double> THRESHOLDS = List.of(100.0, 300.0, 500.0);
        // The cashback rules for each threshold and service plan
        private static final Map<Double, Map<ServicePlanType, Double>> CASHBACK_RULES = Map.of(
                100.0, Map.of(
                        ServicePlanType.STUDENT, 0.001,
                        ServicePlanType.STANDARD, 0.001,
                        ServicePlanType.SILVER, 0.003,
                        ServicePlanType.GOLD, 0.005
                ),
                300.0, Map.of(
                        ServicePlanType.STUDENT, 0.002,
                        ServicePlanType.STANDARD, 0.002,
                        ServicePlanType.SILVER, 0.004,
                        ServicePlanType.GOLD, 0.0055
                ),
                500.0, Map.of(
                        ServicePlanType.STUDENT, 0.0025,
                        ServicePlanType.STANDARD, 0.0025,
                        ServicePlanType.SILVER, 0.005,
                        ServicePlanType.GOLD, 0.007
                )
        );

        /**
         * The spending data for each bank account.
         * We store this data inside the strategy since the spending-based cashback is common for
         * all merchants of this type.
         */
        private final Map<BankAccount, Double> accountSpending = new HashMap<>();

        @EqualsAndHashCode(callSuper = true)
        static final class SpendingCashback extends Cashback {
            SpendingCashback(final double percentage) {
                super(percentage, null);
            }

            @Override
            boolean isApplicableNow() {
                return true;
            }

            @Override
            boolean isApplicableOneTime() {
                return false;
            }
        }

        @Override
        public Optional<Cashback> registerTransaction(final BankAccount bankAccount,
                                                      final double amount) {
            accountSpending.put(bankAccount,
                    accountSpending.getOrDefault(bankAccount, 0.0) + amount);

            double discountPercentage = THRESHOLDS.stream()
                    .filter(threshold -> accountSpending.get(bankAccount) >= threshold)
                    .reduce((first, second) -> second)
                    .map(threshold ->
                            CASHBACK_RULES.get(threshold)
                                    .get(bankAccount.getOwner().getServicePlan()
                                            .getServicePlanType())
                    )
                    .orElse(0.0);


            if (discountPercentage > 0) {
                return Optional.of(new SpendingCashback(discountPercentage));
            }

            return Optional.empty();
        }
    }

}
