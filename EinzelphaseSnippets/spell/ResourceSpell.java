package de.unisaarland.cs.se.selab.spell;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Player;

public class ResourceSpell extends Spell {

    private final int food;
    private final int gold;

    public ResourceSpell(final int id, final BidType triggerType,
            final int triggerSlot, final int food, final int gold) {
        super(id, triggerType, triggerSlot);
        this.food = food;
        this.gold = gold;
        this.cost = 1;

    }

    /**
     * reduces player food and gold by given values, adjusting for correct calls
     *
     * @param p player
     * @param c connection
     */
    @Override
    public void cast(final Player p, final ConnectionWrapper c) {
        if (!ready) {
            c.sendSpellCast(getId(), p.getId());
            readyUp();
            return;
        }
        if (food > 0) {
            final int accFood = (p.getFood() - food >= 0) ? food : p.getFood();
            if (accFood > 0) {
                ConnectionUtils.changeFood(p, -accFood, c);
            }
        }

        if (gold > 0) {
            final int accGold = (p.getGold() - gold >= 0) ? gold : p.getGold();
            if (accGold > 0) {
                ConnectionUtils.changeGold(p, -accGold, c);
            }
        }
        use();
    }
}
