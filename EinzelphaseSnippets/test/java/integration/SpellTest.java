package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.spell.BiddingSpell;
import de.unisaarland.cs.se.selab.spell.BuffSpell;
import de.unisaarland.cs.se.selab.spell.ResourceSpell;
import de.unisaarland.cs.se.selab.spell.RoomSpell;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * class containing simple tests for some spell effects
 */
class SpellTest {

    @Test
    void resourceSpellTest() {
        final ConnectionWrapper fake = Mockito.mock(ConnectionWrapper.class);
        final Player p = new Player(0, "magallan", 5, 5, 5, 5, new Dungeon());
        final ResourceSpell spell1 = new ResourceSpell(0, null, 0, 2, 0);
        spell1.cast(p, fake);
        // spell should not apply effects after first cast
        assertEquals(5, p.getFood());
        assertEquals(5, p.getGold());

        spell1.cast(p, fake);
        // should remove 2 food and not touch gold
        assertEquals(3, p.getFood());
        assertEquals(5, p.getGold());

        final ResourceSpell spell2 = new ResourceSpell(1, null, 0, 0, 2);
        spell2.cast(p, fake);
        spell2.cast(p, fake);
        // now both should be at 3
        assertEquals(3, p.getGold());
        assertEquals(3, p.getFood());

        final ResourceSpell spell3 = new ResourceSpell(2, null, 0, 5, 5);
        spell3.cast(p, fake);
        spell3.cast(p, fake);
        // the resources should not go below 0

        assertEquals(0, p.getFood());
        assertEquals(0, p.getGold());

    }

    @Test
    void buffSpellTest() {
        final ConnectionWrapper fake = Mockito.mock(ConnectionWrapper.class);
        final Player p = new Player(0, "magallan", 5, 5, 5, 5, new Dungeon());

        final Adventurer a1 = new Adventurer(0, 0, 5, 0, 1, false, 0);
        final Adventurer a2 = new Adventurer(1, 0, 5, 1, 0, false, 0);
        final Adventurer a3 = new Adventurer(2, 0, 5, 1, 1, false, 0);

        p.getDungeon().addAdventurer(a1);
        p.getDungeon().addAdventurer(a2);
        p.getDungeon().addAdventurer(a3);

        final BuffSpell buff1 = new BuffSpell(0, null, 0, 2, 2, 2);
        buff1.cast(p, fake);
        buff1.cast(p, fake);
        // checking if buff was applied correctly

        assertEquals(7, a1.getCurrentHealthPoints());
        assertEquals(0, a1.getHealValue());
        assertEquals(3, a1.getDefuseValue());

        assertEquals(7, a2.getCurrentHealthPoints());
        assertEquals(3, a2.getHealValue());
        assertEquals(0, a2.getDefuseValue());

        assertEquals(7, a3.getCurrentHealthPoints());
        assertEquals(3, a3.getHealValue());
        assertEquals(3, a3.getDefuseValue());

        // casting same spell again to make sure stacking is handled correctly

        buff1.cast(p, fake);

        assertEquals(9, a1.getCurrentHealthPoints());
        assertEquals(0, a1.getHealValue());
        assertEquals(5, a1.getDefuseValue());

        assertEquals(9, a3.getCurrentHealthPoints());
        assertEquals(5, a3.getHealValue());
        assertEquals(5, a3.getDefuseValue());
    }

    @Test
    void roomSpellTest() {
        final ConnectionWrapper fake = Mockito.mock(ConnectionWrapper.class);
        final Player p = new Player(0, "magallan", 5, 5, 5, 5, new Dungeon());
        final RoomSpell spell = new RoomSpell(0, null, 0);
        spell.setAvailableRound(3);
        spell.cast(p, fake);
        spell.cast(p, fake);

        assertTrue(p.canActivateRoom(1));
        assertTrue(p.canActivateRoom(2));
        assertFalse(p.canActivateRoom(3));
        assertTrue(p.canActivateRoom(4));
        p.clearBlocks();
        // should be possible to activate after clearing blocks
        assertTrue(p.canActivateRoom(3));
        // this one should still be true after clearing
        assertTrue(p.canActivateRoom(4));
    }

    @Test
    void biddingSpellTest() {
        final ConnectionWrapper fake = Mockito.mock(ConnectionWrapper.class);
        final Player p = new Player(0, "magallan", 5, 5, 5, 5, new Dungeon());
        final BiddingSpell spell = new BiddingSpell(0, null, 0, BidType.FOOD);
        spell.setAvailableRound(3);
        assertTrue(p.canBidOn(BidType.FOOD, 3));
        spell.cast(p, fake);
        spell.cast(p, fake);
        // only round 3 should have food blocked, but still allow other stuff like monster
        assertTrue(p.canBidOn(BidType.FOOD, 1));
        assertTrue(p.canBidOn(BidType.FOOD, 2));
        assertFalse(p.canBidOn(BidType.FOOD, 3));
        assertTrue(p.canBidOn(BidType.MONSTER, 3));
        assertTrue(p.canBidOn(BidType.FOOD, 4));

        final BiddingSpell spell2 = new BiddingSpell(0, null, 0, BidType.MONSTER);
        spell2.setAvailableRound(3);
        spell2.cast(p, fake);
        spell2.cast(p, fake);

        // this sould still be blocked
        assertFalse(p.canBidOn(BidType.FOOD, 3));
        // this should be blocked as well now
        assertFalse(p.canBidOn(BidType.MONSTER, 3));

        p.clearBlocks();

        assertTrue(p.canBidOn(BidType.FOOD, 3));
        assertTrue(p.canBidOn(BidType.MONSTER, 3));
    }

    /*
    Structure spells are not tested here as the algorithm for searching rooms already has
    unit tests and conquer behaviour is tested in a systemTest
     */


}
