package gwentstone.cards;
import java.util.List;

public class Hero extends Card {
    private static final int INITIAL_HEALTH = 30;

    private final HeroConfig config;

    public static Hero create(int mana, String description, List<String> colors, String name)
            throws IllegalArgumentException {
        HeroConfig config = HeroRegistry.getHeroConfig(name);

        if(config == null) {
            throw new IllegalArgumentException("Unknown hero: " + name);
        }
       return new Hero(config, mana, description, colors);
    }

    public static Hero copy(Hero hero) {
        return create(hero.getMana(), hero.getDescription(), hero.getColors(), hero.getName());
    }

    private Hero(HeroConfig config, int mana, String description, List<String> colors) {
        super(mana, INITIAL_HEALTH, description, colors, config.getName());
        this.config = config;
    }

    public void useAbility(List<Minion> row){
       HeroAbility ability = config.getAbility();
       if(ability != null) {
           ability.accept(row);
       }
    }

}
