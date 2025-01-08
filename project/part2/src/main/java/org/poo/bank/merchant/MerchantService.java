package org.poo.bank.merchant;

import org.poo.bank.account.BankAccount;
import org.poo.bank.type.IBAN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MerchantService {
    private final CashbackService cashbackService = new CashbackService();
    private final Map<IBAN, Merchant> ibanMapping = new HashMap<>();
    private final Map<String, Merchant> nameMapping = new HashMap<>();
    private final Map<MerchantType, List<Merchant>> typeMapping = new HashMap<>();

    /**
     * Creates a new merchant and adds it to the merchant service.
     *
     * @param name         the name of the merchant
     * @param id           the id of the merchant
     * @param accountIban  the IBAN of the merchant
     * @param type         the type of the merchant
     * @param cashbackType the cashback type used by the merchant
     */
    public void createMerchant(final String name, final int id, final IBAN accountIban,
                               final MerchantType type, final CashbackType cashbackType) {
        if (nameMapping.containsKey(name) || ibanMapping.containsKey(accountIban)) {
            throw new IllegalArgumentException("Merchant already exists");
        }

        Merchant merchant = Merchant.builder()
                .name(name)
                .id(id)
                .accountIban(accountIban)
                .type(type)
                .cashbackType(cashbackType)
                .build();

        ibanMapping.put(accountIban, merchant);
        nameMapping.put(name, merchant);
        typeMapping.computeIfAbsent(type, k -> new ArrayList<>()).add(merchant);
    }

    /**
     * Returns the merchant corresponding to the given IBAN.
     *
     * @param iban the IBAN of the merchant
     * @return an {@link Optional} containing the merchant corresponding to the given IBAN or an
     * {@link Optional#empty()} if no merchant corresponds to the given IBAN
     */
    public Optional<Merchant> getMerchant(final IBAN iban) {
        return Optional.ofNullable(ibanMapping.get(iban));
    }

    /**
     * Returns the merchant corresponding to the given name.
     *
     * @param name the name of the merchant
     * @return the merchant corresponding to the given name or an empty Optional if no merchant
     * corresponds to the given name
     */
    public Optional<Merchant> getMerchant(final String name) {
        return Optional.ofNullable(nameMapping.get(name));
    }

    /**
     * Registers a transaction for the given merchant and bank account.
     *
     * @param merchant    the merchant for which the transaction is registered
     * @param bankAccount the bank account for which the transaction is registered
     * @param amount      the amount of the transaction in RON
     * @return the discount that can be applied to the transaction
     */
    public Discount registerTransaction(final Merchant merchant,
                                        final BankAccount bankAccount,
                                        final double amount) {
        return cashbackService.registerTransaction(merchant, bankAccount, amount);
    }
}
