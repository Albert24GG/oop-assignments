package org.poo.bank.log.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.log.AuditLogType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder(toBuilder = true)
@Getter
public class AuditLogView {
    private final Integer timestamp;
    @JsonIgnore
    private final AuditLogType logType;
    @JsonIgnore
    private final AuditLogStatus logStatus;
    private final String description;
    private final String error;

    /**
     * Create a new object derived from AuditLogView, using a builder of the derived class.
     *
     * @param base    the base AuditLogView object to copy from
     * @param builder the builder of the derived class
     * @param <T>     the type of the derived class
     * @param <R>     the type of the builder of the derived class
     * @return a new object of the derived class
     */
    public static <T extends AuditLogView, R extends AuditLogViewBuilder<? extends T, ?>>
    T fromBase(final AuditLogView base, final R builder) {
        return builder
                .timestamp(base.getTimestamp())
                .logType(base.getLogType())
                .logStatus(base.getLogStatus())
                .description(base.getDescription())
                .error(base.getError())
                .build();
    }
}
