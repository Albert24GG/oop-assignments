package gwentstone.cards;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Card {
    private final Integer mana;
    private final Integer health;
    private final Integer attackDamage;
    private final String description;
    private final List<String> colors;
    private final String name;
}
