package gwentstone.cards;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.BiConsumer;


public class Minion extends Card {

    private final MinionConfig config;
    @Getter
    @Setter
    private int attackDamage;
    @Getter
    @Setter
    private boolean frozen = false;
    private BiConsumer<Minion, Minion> ability;

    public static Minion create(int mana, int health, int attackDamage, String description, List<String> colors, String name)
            throws IllegalArgumentException {
        MinionConfig config = MinionRegistry.getMinionConfig(name);

        if(config == null) {
            throw new IllegalArgumentException("Unknown minion type " + name);
        }

        return new Minion(config, mana, health, attackDamage, description, colors);
    }

    public static Minion copy(Minion minion) {
        return create(minion.getMana(), minion.getHealth(), minion.getAttackDamage(), minion.getDescription(), minion.getColors(), minion.getName());
    }

    private Minion(MinionConfig config, int mana, int health, int attackDamage, String description, List<String> colors) {
        super(mana, health, description, colors, config.getName());
        this.attackDamage = attackDamage;
        this.config = config;
    }

    public void useAbility(Minion target){
        if(frozen){
            return;
        }

        MinionAbility ability = config.getAbility();
        if(ability != null) {
            ability.accept(this, target);
        }
    }

    public void attack(Card target){
        if(frozen){
            return;
        }
        target.setHealth(target.getHealth() - attackDamage);
    }


}
