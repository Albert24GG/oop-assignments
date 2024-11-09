package org.poo.fileio;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public final class CardInput {
    private Integer mana;
    private Integer attackDamage;
    private Integer health;
    private String description;
    private ArrayList<String> colors;
    private String name;

    public CardInput() {
    }

    @Override
    public String toString() {
        return "CardInput{"
                + "mana="
                + mana
                + ", attackDamage="
                + attackDamage
                + ", health="
                + health
                + ", description='"
                + description
                + '\''
                + ", colors="
                + colors
                + ", name='"
                + name
                + '\''
                + '}';
    }
}
