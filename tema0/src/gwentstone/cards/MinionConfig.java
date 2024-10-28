package gwentstone.cards;

import lombok.Getter;

import java.util.function.BiConsumer;

@Getter
public class MinionConfig {

    private final String name;
    private final MinionPlacement placement;
    private final MinionAbility ability;
    private final boolean isTank;

    MinionConfig(String name, MinionPlacement placement, MinionAbility ability, boolean isTank) {
        this.name = name;
        this.placement = placement;
        this.ability = ability;
        this.isTank = isTank;
    }

    public static class Builder{
        private final String name;
        private final MinionPlacement placement;
        private MinionAbility ability = null;
        private boolean isTank = false;

        Builder(String name, MinionPlacement placement){
            this.name = name;
            this.placement = placement;
        }

        Builder ability(MinionAbility ability){
            this.ability = ability;
            return this;
        }

        Builder tank(){
            this.isTank = true;
            return this;
        }

        MinionConfig build(){
            return new MinionConfig(name, placement, ability, isTank);
        }

    }
}
