package org.poo.bank.transaction.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLogType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder(toBuilder = true)
@Getter
public class TransactionLogView {
    private final Integer timestamp;
    @JsonIgnore
    private final TransactionLogType type;
    private final String description;
    private final String error;
}
