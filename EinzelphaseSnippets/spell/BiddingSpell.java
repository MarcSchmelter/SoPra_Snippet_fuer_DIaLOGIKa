package de.unisaarland.cs.se.selab.spell;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Player;

public class BiddingSpell extends Spell {

    private final BidType blockedType;

    public BiddingSpell(final int id, final BidType triggerType,
            final int triggerSlot, final BidType blockedType) {
        super(id, triggerType, triggerSlot);
        this.blockedType = blockedType;
        this.cost = 4;
    }

    @Override
    public boolean doesNothingLastYear() {
        return true;
    }

    /**
     * locks a bidtype for the player if it is not already locked
     *
     * @param p player player to cast spell on
     * @param c connection
     */
    @Override
    public void cast(final Player p, final ConnectionWrapper c) {
        if (!ready) {
            c.sendSpellCast(getId(), p.getId());
            readyUp();
            return;
        }
        if (!p.canBidOn(blockedType, getAvailableRound())) {
            use();
            return;
        }
        p.blockBid(blockedType, getAvailableRound());
        c.sendBidTypeBlocked(p.getId(), blockedType, getAvailableRound());
        use();
    }
}
