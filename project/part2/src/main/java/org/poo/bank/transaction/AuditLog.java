package org.poo.bank.transaction;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.view.AuditLogView;

@SuperBuilder(toBuilder = true)
@Getter
public class AuditLog {
    @NonNull
    private final Integer timestamp;
    @NonNull
    private final AuditLogType logType;
    @NonNull
    private final AuditLogStatus logStatus;
    private final String description;
    private final String error;

    /**
     * Convert the transaction log to a view
     *
     * @return the transaction log view
     */
    public AuditLogView toView() {
        return AuditLogView.builder()
                .timestamp(timestamp)
                .logStatus(logStatus)
                .logType(logType)
                .description(description)
                .error(error)
                .build();
    }
}

