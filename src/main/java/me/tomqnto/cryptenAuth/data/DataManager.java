package me.tomqnto.cryptenAuth.data;

import java.util.UUID;

public interface DataManager {

    /**
     * Registers a new player with their hashed password.
     * @param uuid The UUID of the player.
     * @param playerName The name of the player.
     * @param hashedPassword The BCrypt hashed password.
     * @return true if registration was successful, false otherwise (e.g., player already exists).
     */
    boolean registerPlayer(UUID uuid, String playerName, String hashedPassword, String ipAddress);

    /**
     * Checks if a player is registered.
     * @param uuid The UUID of the player.
     * @return true if the player is registered, false otherwise.
     */
    boolean isRegistered(UUID uuid);

    /**
     * Retrieves the hashed password for a given player.
     * @param uuid The UUID of the player.
     * @return The hashed password, or null if the player is not registered.
     */
    String getHashedPassword(UUID uuid);

    /**
     * Updates the hashed password for an existing player.
     * @param uuid The UUID of the player.
     * @param newHashedPassword The new BCrypt hashed password.
     * @return true if the password was updated, false if the player was not found.
     */
    boolean updatePassword(UUID uuid, String newHashedPassword);

    /**
     * Updates the last known login IP address for a player.
     * @param uuid The UUID of the player.
     * @param ipAddress The IP address to store.
     * @return true if the IP was updated, false if the player was not found.
     */
    boolean updateLastLoginIp(UUID uuid, String ipAddress);

    /**
     * Retrieves the last known login IP address for a player.
     * @param uuid The UUID of the player.
     * @return The last login IP address, or null if not found.
     */
    String getLastLoginIp(UUID uuid);

    /**
     * Gets the number of registered accounts associated with a given IP address.
     * @param ipAddress The IP address to check.
     * @return The count of registered accounts from that IP.
     */
    int getRegisteredAccountsCountByIp(String ipAddress);

    /**
     * Removes a player's registration data.
     * @param uuid The UUID of the player to unregister.
     * @return true if the player was successfully unregistered, false otherwise.
     */
    boolean removePlayer(UUID uuid);

    /**
     * Saves all pending data changes to storage.
     */
    void saveData();

    /**
     * Loads all data from storage.
     */
    void loadData();
}
