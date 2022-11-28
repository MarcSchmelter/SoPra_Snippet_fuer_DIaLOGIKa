package de.unisaarland.cs.se.selab.spell;

import de.unisaarland.cs.se.selab.comm.BidType;
import java.util.List;

public class SpellFactory {


    /**
     * takes all of the possible parameters a spell could have and returns corresponding
     * spell if given parameters fit with the spellType, meaning all necessary ones are given
     * and no other values differ from default values
     *
     * @return spell or error
     */
    public static Spell createSpell(final String type, final int id, final BidType triggerType,
            final int triggerSlot, final int food, final int gold,
            final int health, final int heal,
            final int defuse, final BidType blocked,
            final String structureEffect) {

        switch (type) {
            case "RESOURCE" -> {
                final Spell s = createResourceSpell(id, triggerType, triggerSlot, food, gold);
                checkInts(List.of(health, heal, defuse));
                checkString(structureEffect);
                checkBidType(blocked);
                return s;

            }
            case "BUFF" -> {
                final Spell s = createBuffSpell(id, triggerType, triggerSlot,
                        health, heal, defuse);
                checkInts(List.of(food, gold));
                checkString(structureEffect);
                checkBidType(blocked);
                return s;

            }
            case "STRUCTURE" -> {
                final Spell s = createStructureSpell(id, triggerType,
                        triggerSlot, structureEffect);
                checkInts(List.of(food, gold, health, heal, defuse));
                checkBidType(blocked);
                return s;

            }
            case "BIDDING" -> {
                final Spell s = createBiddingSpell(id, triggerType, triggerSlot, blocked);
                checkInts(List.of(food, gold, health, heal, defuse));
                checkString(structureEffect);
                return s;


            }
            case "ROOM" -> {
                final Spell s = createRoomSpell(id, triggerType, triggerSlot);
                checkInts(List.of(food, gold, health, heal, defuse));
                checkBidType(blocked);
                checkString(structureEffect);
                return s;


            }
            default -> {
                throw new IllegalArgumentException();
            }
        }

    }

    // these methods are used to check that all other parameters are default values

    private static void checkInts(final Iterable<Integer> l) {
        for (final int i :
                l) {
            if (i != 0) {
                throw new IllegalArgumentException();
            }
        }
    }

    private static void checkString(final String s) {
        if (!"".equals(s)) {
            throw new IllegalArgumentException();
        }
    }

    private static void checkBidType(final BidType bt) {
        if (bt != null) {
            throw new IllegalArgumentException();
        }
    }


    private static Spell createRoomSpell(final int id, final BidType triggerType,
            final int triggerSlot) {
        return new RoomSpell(id, triggerType, triggerSlot);
    }

    private static Spell createBiddingSpell(final int id, final BidType triggerType,
            final int triggerSlot, final BidType blocked) {
        if (blocked == null) {
            throw new IllegalArgumentException();
        }

        return new BiddingSpell(id, triggerType, triggerSlot, blocked);
    }

    private static Spell createStructureSpell(final int id, final BidType triggerType,
            final int triggerSlot, final String structureEffect) {
        switch (structureEffect) {
            case "CONQUER" -> {
                return new StructureSpell(id, triggerType, triggerSlot, true);
            }
            case "DESTROY" -> {
                return new StructureSpell(id, triggerType, triggerSlot, false);
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
    }

    protected static Spell createBuffSpell(final int id, final BidType triggerType,
            final int triggerSlot, final int health,
            final int heal, final int defuse) {
        if (health == 0 && heal == 0 && defuse == 0) {
            throw new IllegalArgumentException();
        }
        return new BuffSpell(id, triggerType, triggerSlot, health, heal, defuse);
    }

    protected static Spell createResourceSpell(final int id, final BidType triggerType,
            final int triggerSlot, final int food, final int gold) {
        if (food == 0 && gold == 0) {
            throw new IllegalArgumentException();
        }
        return new ResourceSpell(id, triggerType, triggerSlot, food, gold);

    }
}
