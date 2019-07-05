package GameEngine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PlayerManager {

    private final Set<String> playersSet;

    public PlayerManager() {
        playersSet = new HashSet<>();
    }

    public synchronized void addPlayer(String username) {
        playersSet.add(username);
    }

    public synchronized void removePlayer(String username) {
        playersSet.remove(username);
    }

    public synchronized Set<String> getPlayers() {
        return Collections.unmodifiableSet(playersSet);
    }

    public boolean isPlayerExists(String username) {
        return playersSet.contains(username);
    }
}
