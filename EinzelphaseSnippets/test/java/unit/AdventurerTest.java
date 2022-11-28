package unit;

import de.unisaarland.cs.se.selab.model.Adventurer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AdventurerTest {

    /**
     * Testing the buff method in adventurer for general usability
     */
    @Test
    void completeBuffTest() {

        final Adventurer saria = new Adventurer(1, 10, 10, 2, 2, false, 0);
        saria.buff(0, 2, 2);

        Assertions.assertEquals(10, saria.getCurrentHealthPoints());

        Assertions.assertEquals(4, saria.getHealValue());
        Assertions.assertEquals(4, saria.getDefuseValue());
        // this time, those two buffs should be applied

        saria.endBuff();

        Assertions.assertEquals(10, saria.getCurrentHealthPoints());

        Assertions.assertEquals(2, saria.getHealValue());
        Assertions.assertEquals(2, saria.getDefuseValue());
        // these buffs should be removed properly as well

        final Adventurer siege = new Adventurer(0, 0, 10, 2, 2, true, 0);
        siege.buff(2, 2, 2);
        siege.buff(5, 5, 5);

        // making sure stacked buffs are applied properly
        Assertions.assertEquals(17, siege.getCurrentHealthPoints());
        Assertions.assertEquals(9, siege.getHealValue());
        Assertions.assertEquals(9, siege.getDefuseValue());

        siege.endBuff();

        // making sure they end properly as well
        Assertions.assertEquals(10, siege.getCurrentHealthPoints());
        Assertions.assertEquals(2, siege.getHealValue());
        Assertions.assertEquals(2, siege.getDefuseValue());
    }

}
