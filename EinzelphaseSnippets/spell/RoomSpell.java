package de.unisaarland.cs.se.selab.spell;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Player;

public class RoomSpell extends Spell {

    public RoomSpell(final int id, final BidType triggerType,
            final int triggerSlot) {
        super(id, triggerType, triggerSlot);
        this.cost = 3;
    }

    @Override
    public boolean doesNothingLastYear() {
        return true;
    }

    /**
     * blocks roomActivation for player in the availableRound if they are not
     * already blocked there
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
        if (!p.canActivateRoom(getAvailableRound())) {
            use();
            return;
        }
        p.blockRoomActivation(getAvailableRound());
        c.sendRoomsBlocked(p.getId(), getAvailableRound());
        use();
    }
}
