package org.poo.gwentstone.actions;

import lombok.Getter;
import org.poo.gwentstone.GameErrorType;

@Getter
public class ActionException extends Exception {
    private final GameErrorType errorType;

    public ActionException(final GameErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ActionException(final GameErrorType errorType, final String message) {
        super(message);
        this.errorType = errorType;
    }
}
