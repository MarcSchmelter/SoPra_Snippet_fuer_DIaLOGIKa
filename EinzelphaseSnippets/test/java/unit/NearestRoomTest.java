package unit;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.model.dungeon.Room.BuildingRestriction;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.model.dungeon.TunnelGraph;
import de.unisaarland.cs.se.selab.spell.StructureSpell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * TestClass to check for correctness of the findNearestRoom method in the structureSpell.
 * as that method turned out to be rather complex it warranted some tests
 * on different scenarios to ensure correctness
 */
class NearestRoomTest {

    @Test
    void simpleRoomTest() {
        // requires the method to find a room thats not in the first tile looked at
        final Dungeon dun = new Dungeon();
        final TunnelGraph graph = dun.getGraph();
        final Tunnel t01 = new Tunnel(new Coordinate(0, 1), false);
        graph.addTunnel(t01);
        t01.buildRoom(new Room(0, 0, BuildingRestriction.UPPER_HALF, null));
        final StructureSpell tSpell = new StructureSpell(0, BidType.FOOD, 1, false);
        Assertions.assertSame(tSpell.findNearestRoom(
                        graph.getTunnel(new Coordinate(0, 0)).get(), graph),
                t01);

    }

    @Test
    void closerRoomTest() {
        final Dungeon dun = new Dungeon();
        final TunnelGraph graph = dun.getGraph();
        final Tunnel t01 = new Tunnel(new Coordinate(0, 1), false);
        final Tunnel t02 = new Tunnel(new Coordinate(0, 2), false);
        final Tunnel t10 = new Tunnel(new Coordinate(1, 0), false);
        final Tunnel t20 = new Tunnel(new Coordinate(2, 0), false);
        graph.addTunnel(t01);
        graph.addTunnel(t02);
        graph.addTunnel(t10);
        graph.addTunnel(t20);
        final Room r1 = new Room(0, 0, BuildingRestriction.OUTER_RING, null);
        final Room r2 = new Room(1, 0, BuildingRestriction.OUTER_RING, null);
        t02.buildRoom(r1);
        t10.buildRoom(r2);
        final Tunnel start = graph.getTunnel(new Coordinate(0, 0)).get();
        final StructureSpell spell = new StructureSpell(0, null, 1, false);
        // expecting t10 here since its closer, which is heavier than id
        Assertions.assertSame(t10, spell.findNearestRoom(start, graph));
    }

    @Test
    void twoRoomsTest() {
        final Dungeon dun = new Dungeon();
        final TunnelGraph graph = dun.getGraph();
        final Tunnel t01 = new Tunnel(new Coordinate(0, 1), false);
        final Tunnel t02 = new Tunnel(new Coordinate(0, 2), false);
        final Tunnel t10 = new Tunnel(new Coordinate(1, 0), false);
        final Tunnel t20 = new Tunnel(new Coordinate(2, 0), false);
        graph.addTunnel(t01);
        graph.addTunnel(t02);
        graph.addTunnel(t10);
        graph.addTunnel(t20);
        final Room r1 = new Room(0, 0, BuildingRestriction.OUTER_RING, null);
        final Room r2 = new Room(1, 0, BuildingRestriction.OUTER_RING, null);
        t02.buildRoom(r1);
        t20.buildRoom(r2);
        // both rooms at same distance to start
        final Tunnel start = graph.getTunnel(new Coordinate(0, 0)).get();
        final StructureSpell spell = new StructureSpell(0, null, 1, false);
        // expecting t02 since both rooms have the same dist. to start but r1 has id 0 < 1
        Assertions.assertSame(t02, spell.findNearestRoom(start, graph));
    }

    @Test
    void noRoomTest() {
        final Dungeon dun = new Dungeon();
        final TunnelGraph graph = dun.getGraph();
        final Tunnel t01 = new Tunnel(new Coordinate(0, 1), false);
        final Tunnel t02 = new Tunnel(new Coordinate(0, 2), false);
        final Tunnel t10 = new Tunnel(new Coordinate(1, 0), false);
        final Tunnel t20 = new Tunnel(new Coordinate(2, 0), false);
        graph.addTunnel(t01);
        graph.addTunnel(t02);
        graph.addTunnel(t10);
        graph.addTunnel(t20);
        final Tunnel start = graph.getTunnel(new Coordinate(0, 0)).get();
        final StructureSpell spell = new StructureSpell(0, null, 1, false);
        // expecting null here as there are no rooms
        Assertions.assertSame(null, spell.findNearestRoom(start, graph));
    }

    @Test
    void twoPathsOneRoomTest() {
        final Dungeon dun = new Dungeon();
        final TunnelGraph graph = dun.getGraph();
        final Tunnel t01 = new Tunnel(new Coordinate(0, 1), false);
        final Tunnel t02 = new Tunnel(new Coordinate(0, 2), false);
        final Tunnel t10 = new Tunnel(new Coordinate(1, 0), false);
        final Tunnel t20 = new Tunnel(new Coordinate(2, 0), false);
        final Tunnel t21 = new Tunnel(new Coordinate(2, 1), false);
        final Tunnel t12 = new Tunnel(new Coordinate(1, 2), false);
        final Tunnel t22 = new Tunnel(new Coordinate(2, 2), false);
        graph.addTunnel(t01);
        graph.addTunnel(t02);
        graph.addTunnel(t10);
        graph.addTunnel(t20);
        graph.addTunnel(t21);
        graph.addTunnel(t12);
        graph.addTunnel(t22);
        final Room room = new Room(0, 0, BuildingRestriction.INNER_RING, null);
        t22.buildRoom(room);
        final Tunnel start = graph.getTunnel(new Coordinate(0, 0)).get();
        final StructureSpell spell = new StructureSpell(0, null, 1, false);
        // expecting t22 here to make sure a room at the end of a loop is also found
        // without a second path to reach it messing up the method
        Assertions.assertSame(t22, spell.findNearestRoom(start, graph));
    }

}
