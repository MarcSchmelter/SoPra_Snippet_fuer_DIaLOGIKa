package de.unisaarland.cs.se.selab.spell;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.Trap;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.model.dungeon.TunnelGraph;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class StructureSpell extends Spell {

    private final boolean conquer;

    public StructureSpell(final int id, final BidType triggerType,
            final int triggerSlot, final boolean conquer) {
        super(id, triggerType, triggerSlot);
        this.conquer = conquer;
        this.cost = 5;
    }

    @Override
    public void cast(final Player p, final ConnectionWrapper c) {
        if (!ready) {
            c.sendSpellCast(getId(), p.getId());
            readyUp();
            return;
        }
        if (conquer) { // conquering mechanics
            final Optional<Tunnel> optun = p.getDungeon().getBattleGround();
            if (optun.isEmpty()) {
                return;
            }
            final Tunnel tun = optun.get();
            tun.conquer();
            final Optional<Trap> optrap = tun.getTrap();
            optrap.ifPresent(p::addTrap);
            final List<Monster> monsters = tun.getMonsters();
            for (final Monster mon :
                    monsters) {
                mon.setUsed(true);
            }
            tun.clearDefenders();
            use();
            return;
        }

        final Optional<Tunnel> optun = p.getDungeon().getBattleGround();
        if (optun.isEmpty()) {
            return;
        }
        final Tunnel tun = optun.get();

        if (tun.isRoom()) { // makes ispresent check unnecessary
            final int id = tun.getRoom().get().getId();
            removeMonster(tun);
            tun.removeRoom();
            c.sendRoomRemoved(p.getId(), id);
            use();
            return;
        } // ends quickly if battleground is a room

        final Tunnel fintun = findNearestRoom(tun, p.getDungeon().getGraph());

        if (fintun == null) { // no rooms left in dungeon
            use();
            return;
        }

        if (fintun.getRoom().isEmpty()) {
            use();
            return;
        }

        final Room room = fintun.getRoom().get();
        c.sendRoomRemoved(p.getId(), room.getId());
        removeMonster(fintun);
        fintun.removeRoom();
        use();


    }

    private void removeMonster(final Tunnel tun) {
        if (tun.getMonsters().size() > 0) {
            tun.getMonsters().remove(0);
        }
    }

    /**
     * searches for the closest tunnel in the dungeon containing a room that is not the
     * initial tunnel in the parameters. since that case is considered by hand before this
     * method is called, it does not need to consider it again and thus will ignore the
     * initial tunnel. the algorithm used is probably something close to breadth first search,
     * i wouldn't know for sure though.
     *
     * @param tun   tunnel to start looking from
     * @param graph tunnergraph of the dungeon
     * @return nearest tunnel containing a room or null if there is none
     */
    public @Nullable Tunnel findNearestRoom(final Tunnel tun, final TunnelGraph graph) {

        final Set<Tunnel> visited = new HashSet<>();
        final Set<Tunnel> current = new HashSet<>(List.of(tun));
        while (!current.isEmpty()) {
            visited.addAll(current);
            // get all neighbors and filter out the ones that were visited already
            final Set<Tunnel> tunnels = new HashSet<>(
                    current.stream()
                            .flatMap(tunnel -> graph.getNeighbours(tunnel.getCoordinate()).stream())
                            .filter(t -> !visited.contains(t)).toList());
            final List<Tunnel> rooms = tunnels.stream()
                    .filter(Tunnel::isRoom).toList();
            if (!rooms.isEmpty()) {
                // filter for room with min id
                // can skip ispresent here because of isroom check above
                Tunnel res = rooms.get(0);
                for (final Tunnel room :
                        rooms) {
                    if (room.getRoom().get().getId() < res.getRoom().get().getId()) {
                        res = room;
                    }
                }
                return res;
            }
            current.clear();
            current.addAll(tunnels);
        }
        return null;
        // only reached if there are no rooms found in the entire dungeon
    }
}
