package de.unisaarland.cs.se.selab.spell;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.Player;
import java.util.List;

public class BuffSpell extends Spell {

    private final int health;
    private final int heal;
    private final int defuse;

    public BuffSpell(final int id, final BidType triggerType, final int triggerSlot,
            final int health, final int heal, final int defuse) {
        super(id, triggerType, triggerSlot);
        this.health = health;
        this.heal = heal;
        this.defuse = defuse;
        this.cost = 2;

    }

    /**
     * buffs a players adventurers, adjusting values for correct calls to buff
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
        final List<Adventurer> advs = p.getDungeon().getAllAdventurers();
        for (final Adventurer adv :
                advs) {
            final int accHeal = (adv.getHealValue() > 0) ? heal : 0; // only buff heal on priests
            final int accDefuse = (adv.getDefuseValue() > 0) ? defuse : 0;
            adv.buff(health, accHeal, accDefuse);
        }
        use();
    }
}
