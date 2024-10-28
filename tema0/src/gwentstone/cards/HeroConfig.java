package gwentstone.cards;

import lombok.Getter;

@Getter
public class HeroConfig {

    private final String name;
    private final HeroAbility ability;

    HeroConfig(String name, HeroAbility ability) {
        this.name = name;
        this.ability = ability;
    }

    public static class Builder{
        private final String name;
        private HeroAbility ability = null;

        Builder(String name){
            this.name = name;
        }

        Builder ability(HeroAbility ability){
            this.ability = ability;
            return this;
        }

        HeroConfig build(){
            return new HeroConfig(name, ability);
        }

    }
}
