package me.tomqnto.cryptenAuth.util;

import me.tomqnto.cryptenAuth.CryptenAuth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final CryptenAuth plugin;
    private final Map<UUID, Long> activeSessions; // UUID -> Expiration Timestamp (milliseconds)

    public SessionManager(CryptenAuth plugin) {
        this.plugin = plugin;
        this.activeSessions = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new session for a player, setting its expiration based on plugin configuration.
     * @param uuid The UUID of the player.
     */
    public void createSession(UUID uuid) {
        long expirationTime = System.currentTimeMillis() + ((long) plugin.getPluginConfig().getSessionDurationMinutes() * 60 * 1000);
        activeSessions.put(uuid, expirationTime);
    }

    /**
     * Checks if a player has a valid, unexpired session.
     * @param uuid The UUID of the player.
     * @return true if the session is valid, false otherwise.
     */
    public boolean isValidSession(UUID uuid) {
        if (!activeSessions.containsKey(uuid)) {
            return false;
        }
        long expirationTime = activeSessions.get(uuid);
        if (System.currentTimeMillis() > expirationTime) {
            activeSessions.remove(uuid); // Session expired
            return false;
        }
        return true;
    }

    /**
     * Removes a player's session.
     * @param uuid The UUID of the player.
     */
    public void removeSession(UUID uuid) {
        activeSessions.remove(uuid);
    }

    /**
     * Clears all active sessions. Useful on plugin disable.
     */
    public void clearSessions() {
        activeSessions.clear();
    }
}
