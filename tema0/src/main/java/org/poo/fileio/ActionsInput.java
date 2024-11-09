package org.poo.fileio;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public final class ActionsInput {
    private String command;
    private Integer handIdx;
    private Coordinates cardAttacker;
    private Coordinates cardAttacked;
    private Integer affectedRow;
    private Integer playerIdx;
    private Integer x;
    private Integer y;

    public ActionsInput() {
    }

    @Override
    public String toString() {
        return "ActionsInput{"
                + "command='"
                + command + '\''
                + ", handIdx="
                + handIdx
                + ", cardAttacker="
                + cardAttacker
                + ", cardAttacked="
                + cardAttacked
                + ", affectedRow="
                + affectedRow
                + ", playerIdx="
                + playerIdx
                + ", x="
                + x
                + ", y="
                + y
                + '}';
    }
}
