package unit;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void lockBidTest() {
        final Player p = new Player(0, "zenos", 0, 0, 0, 0, null);
        p.blockBid(BidType.FOOD, 1);
        p.blockBid(BidType.MONSTER, 3);
        p.blockBid(BidType.MONSTER, 4);
        p.blockBid(BidType.FOOD, 4);

        // food in r1 should be blocked
        Assertions.assertFalse(p.canBidOn(BidType.FOOD, 1));
        // there is a bid blocked, but it is not monster
        Assertions.assertTrue(p.canBidOn(BidType.MONSTER, 1));
        // there is nothing blocked in that round
        Assertions.assertTrue(p.canBidOn(BidType.FOOD, 2));
        // monster blocked in round 3
        Assertions.assertFalse(p.canBidOn(BidType.MONSTER, 3));

        // two bids blocked for round 4
        Assertions.assertFalse(p.canBidOn(BidType.MONSTER, 4));
        Assertions.assertFalse(p.canBidOn(BidType.FOOD, 4));
        Assertions.assertTrue(p.canBidOn(BidType.IMPS, 4));
    }

    @Test
    void quickRoomActivationTest() {
        final Player p = new Player(0, "emet", 0, 0, 0, 0, null);
        p.blockRoomActivation(2);
        // room not blocked for round one, should be true by default
        Assertions.assertTrue(p.canActivateRoom(1));
        // room should be blocked for round 2
        Assertions.assertFalse(p.canActivateRoom(2));
    }


}
