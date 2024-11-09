package org.poo.fileio;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Coordinates {
    private int x, y;

    public Coordinates() {
    }

    @Override
    public String toString() {
        return "Coordinates{"
                + "x="
                + x
                + ", y="
                + y
                + '}';
    }
}
