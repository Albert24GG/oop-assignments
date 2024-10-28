package gwentstone.cards;

import java.util.HashMap;
import java.util.Map;

public class MinionRegistry {
    private static final Map<String, MinionConfig> MINION_CONFIGS = new HashMap<String, MinionConfig>();

    static {
        register(new MinionConfig.Builder("Sentinel", MinionPlacement.BACK).build());

        register(new MinionConfig.Builder("Berserker", MinionPlacement.BACK).build());

        register(new MinionConfig.Builder("Goliath", MinionPlacement.FRONT).tank().build());

        register(new MinionConfig.Builder("Warden", MinionPlacement.FRONT).tank().build());

        register(new MinionConfig.Builder("The Ripper", MinionPlacement.FRONT).ability(
                // Weak Knees
                (mAlly, mEnemy) -> {
                    mEnemy.setAttackDamage(mEnemy.getAttackDamage() - 2);
                }
        ).build());

        register(new MinionConfig.Builder("Miraj", MinionPlacement.FRONT).ability(
                // Skyjack
                (mAlly, mEnemy) -> {
                    int allyHealth = mAlly.getHealth();
                    mAlly.setHealth(mEnemy.getHealth());
                    mEnemy.setHealth(allyHealth);
                }
        ).build());

        register(new MinionConfig.Builder("The Cursed One", MinionPlacement.BACK).ability(
                // Shapeshift
                (mAlly, mEnemy) -> {
                    int enemyHealth = mEnemy.getHealth();
                    mEnemy.setHealth(mEnemy.getAttackDamage());
                    mEnemy.setAttackDamage(enemyHealth);
                }
        ).build());

        register(new MinionConfig.Builder("Disciple", MinionPlacement.BACK).ability(
                // God's Plan
                (mAlly, mEnemy) -> {
                    mAlly.setHealth(mAlly.getHealth() + 2);
                }
        ).build());
    }

    private static void register(MinionConfig mConfig) {
        MINION_CONFIGS.put(mConfig.getName(), mConfig);
    }

    public static MinionConfig getMinionConfig(String name) {
        return MINION_CONFIGS.get(name);
    }
}
