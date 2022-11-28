package de.unisaarland.cs.se.selab.spell;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Player;

public abstract class Spell {

    private final int id;
    protected int cost;
    private final BidType triggerType;
    private final int triggerSlot;
    private int availableRound;
    private boolean used;
    protected boolean ready;

    protected Spell(final int id, final BidType triggerType, final int triggerSlot) {
        this.id = id;
        this.triggerType = triggerType;
        this.triggerSlot = triggerSlot;
        this.cost = 0;
    }

    /**
     * checks the preconditions for casting the spell in the given round
     * on the given player, returns true if the spell can cast.
     * the cast method does not do this check by itself, so it is used from outside
     * when not in a test case to ensure correct behaviour
     *
     * @param round the round to try to cast in
     * @param p the player to try to cast on
     * @return whether the spell can be cast
     */
    public boolean canCast(final int round, final Player p) {
        return ((this.availableRound == round) && (!used)) && checkMP(p);
    }

    public boolean trigger(final BidType bt, final int slot) {
        return ((bt == triggerType) && (slot == triggerSlot));
    }

    public boolean isUsed() {
        return used;
    }

    public void use() {
        used = true;
    }

    public int getId() {
        return this.id;
    }

    public int getAvailableRound() {
        return this.availableRound;
    }

    /**
     * makes a spell cast, applying its effects. this method is called twice: once before waiting
     * for a counterspell, where it only send out an event and sets ready before returning, and once
     * in case no counterspell was cast, where it is now able to actually apply its effects since
     * the ready boolean is set
     *
     * @param p player
     * @param c connection
     */
    public abstract void cast(Player p, ConnectionWrapper c);

    public void setAvailableRound(final int availableRound) {
        this.availableRound = availableRound;
    }

    protected void readyUp() {
        ready = true;
    }

    protected boolean checkMP(final Player p) {
        return p.getDungeon().getTotalMagicPoints() >= this.cost;
    }

    public boolean doesNothingLastYear() {
        return false;
    }
}
