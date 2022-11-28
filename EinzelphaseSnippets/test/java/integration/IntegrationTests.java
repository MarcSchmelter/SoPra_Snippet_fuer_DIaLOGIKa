package integration;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.AttackStrategy;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.spell.Spell;
import de.unisaarland.cs.se.selab.spell.SpellFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * class to test some that use enough other
 * classes to be considered integration
 */
class IntegrationTests {

    @Test
    void archmageTest() {
        final ConnectionWrapper fake = Mockito.mock(ConnectionWrapper.class);
        final Player p = new Player(0, "shining", 0, 0, 0, 0,
                new Dungeon());
        p.addMonster(new Monster(0, 0, 1, 1, AttackStrategy.MULTI));
        p.addMonster(new Monster(1, 0, 0, 1, AttackStrategy.MULTI));
        final Model m = new Model(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), 0, 1, 2, 4, 4, 4, 4, 4);
        m.archmage(p, fake);
        // archmage should remove one of the 2 monsters that were added
        Assertions.assertEquals(1, p.getMonsters().size());
        // archmage should be present
        Assertions.assertTrue(p.isArchmagePresent());

        p.archmageLeaves();
        m.archmage(p, fake);

        // archmage should remove last mosnter now
        Assertions.assertEquals(0, p.getMonsters().size());
        // should be counted as second archmage encounter
        Assertions.assertEquals(2, p.getArchmageCount());

        p.archmageLeaves();
        m.archmage(p, fake);
        // this last call should be possible without errors
    }

    @Test
    void triggerSpellsTest() {
        final ConnectionWrapper fake = Mockito.mock(ConnectionWrapper.class);
        final Player p = new Player(0, "shining", 0, 0, 0, 0,
                new Dungeon());
        final List<Spell> spells = new ArrayList<>();
        spells.add(SpellFactory.createSpell("STRUCTURE", 0, BidType.FOOD, 1,
                0, 0, 0, 0, 0, null, "CONQUER"));
        spells.add(SpellFactory.createSpell("STRUCTURE", 0, BidType.FOOD, 3,
                0, 0, 0, 0, 0, null, "CONQUER"));
        spells.add(SpellFactory.createSpell("STRUCTURE", 0, BidType.FOOD, 1,
                0, 0, 0, 0, 0, null, "CONQUER"));
        final Model m = new Model(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), spells, 0, 1, 2, 4, 4, 4, 4, 4);
        m.drawSpell();
        m.drawSpell();
        m.drawSpell();

        m.triggerSpells(p, BidType.FOOD, 1, fake);
        // should have triggered 2 spells
        Assertions.assertEquals(2, p.getAdventurerSpells().size());

        m.triggerSpells(p, BidType.FOOD, 1, fake);
        // same call should trigger 0 spells now since they should have been removed
        Assertions.assertEquals(2, p.getAdventurerSpells().size());

    }

}
