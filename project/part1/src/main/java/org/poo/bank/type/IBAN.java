package org.poo.bank.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;
import org.poo.utils.Utils;

public record IBAN(String value) {
    /**
     * Generates a random IBAN.
     *
     * @return the IBAN
     */
    public static IBAN generate() {
        return new IBAN(Utils.generateIBAN());
    }

    /**
     * Constructs an IBAN object.
     *
     * @param value the IBAN
     * @return the IBAN object
     */
    public static IBAN of(@NonNull final String value) {
        return new IBAN(value);
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
