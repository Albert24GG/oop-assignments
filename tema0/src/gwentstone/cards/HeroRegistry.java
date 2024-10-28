package gwentstone.cards;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class HeroRegistry {
    private static final Map<String, HeroConfig> HERO_CONFIGS = new HashMap<String, HeroConfig>();

    static {
        register(new HeroConfig.Builder("Lord Royce").ability(
                // Sub-Zero
                (mRow) -> {
                    mRow.forEach((m) -> m.setFrozen(true));
                }
        ).build());

        register(new HeroConfig.Builder("Empress Thorina").ability(
                // Low Blow
                (mRow) -> {
                    Collections.max(mRow, Comparator.comparingInt(Card::getHealth)).setHealth(0);
                }
        ).build());

        register(new HeroConfig.Builder("King Mudface").ability(
                // Earth Born
                (mRow) -> {
                    mRow.forEach((m) -> m.setHealth(m.getHealth() + 1));
                }
        ).build());

        register(new HeroConfig.Builder("General Kocioraw").ability(
                // Blood Thirst
                (mRow) -> {
                    mRow.forEach((m) -> m.setAttackDamage(m.getAttackDamage() + 1));
                }
        ).build());
    }

    private static void register(HeroConfig hConfig) {
        HERO_CONFIGS.put(hConfig.getName(), hConfig);
    }

    public static HeroConfig getHeroConfig(String name) {
        return HERO_CONFIGS.get(name);
    }
}
