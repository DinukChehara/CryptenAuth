package me.tomqnto.cryptenAuth.data;

import me.tomqnto.cryptenAuth.CryptenAuth;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YAMLDataManager implements DataManager {

    private final CryptenAuth plugin;
    private File dataFile;
    private YamlConfiguration playerDataConfig;
    private Map<UUID, String> playerPasswords; // UUID -> Hashed Password
    private Map<UUID, String> playerLastIps; // UUID -> Last Login IP
    private Map<String, List<UUID>> ipToRegisteredPlayers; // IP -> List of UUIDs registered from this IP

    public YAMLDataManager(CryptenAuth plugin) {
        this.plugin = plugin;
        this.playerPasswords = new HashMap<>();
        this.playerLastIps = new HashMap<>();
        this.ipToRegisteredPlayers = new HashMap<>();
        setupFile();
        loadData();
    }

    private void setupFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml!" + e.getMessage());
            }
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    public boolean registerPlayer(UUID uuid, String playerName, String hashedPassword, String ipAddress) {
        if (isRegistered(uuid)) {
            return false;
        }
        playerPasswords.put(uuid, hashedPassword);
        playerLastIps.put(uuid, ipAddress);

        ipToRegisteredPlayers.computeIfAbsent(ipAddress, k -> new ArrayList<>()).add(uuid);

        playerDataConfig.set(uuid.toString() + ".name", playerName);
        playerDataConfig.set(uuid.toString() + ".password", hashedPassword);
        playerDataConfig.set(uuid.toString() + ".last-ip", ipAddress);
        saveData();
        return true;
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        return playerPasswords.containsKey(uuid);
    }

    @Override
    public String getHashedPassword(UUID uuid) {
        return playerPasswords.get(uuid);
    }

    @Override
    public boolean updatePassword(UUID uuid, String newHashedPassword) {
        if (!isRegistered(uuid)) {
            return false;
        }
        playerPasswords.put(uuid, newHashedPassword);
        playerDataConfig.set(uuid.toString() + ".password", newHashedPassword);
        saveData();
        return true;
    }

    @Override
    public boolean updateLastLoginIp(UUID uuid, String ipAddress) {
        if (!isRegistered(uuid)) {
            return false;
        }
        // Remove from old IP list if IP changed
        String oldIp = playerLastIps.get(uuid);
        if (oldIp != null && !oldIp.equals(ipAddress)) {
            List<UUID> oldIpPlayers = ipToRegisteredPlayers.get(oldIp);
            if (oldIpPlayers != null) {
                oldIpPlayers.remove(uuid);
                if (oldIpPlayers.isEmpty()) {
                    ipToRegisteredPlayers.remove(oldIp);
                }
            }
        }

        playerLastIps.put(uuid, ipAddress);
        ipToRegisteredPlayers.computeIfAbsent(ipAddress, k -> new ArrayList<>()).add(uuid);

        playerDataConfig.set(uuid.toString() + ".last-ip", ipAddress);
        saveData();
        return true;
    }

    @Override
    public String getLastLoginIp(UUID uuid) {
        return playerLastIps.get(uuid);
    }

    @Override
    public int getRegisteredAccountsCountByIp(String ipAddress) {
        List<UUID> players = ipToRegisteredPlayers.get(ipAddress);
        return players != null ? players.size() : 0;
    }

    @Override
    public boolean removePlayer(UUID uuid) {
        if (!isRegistered(uuid)) {
            return false;
        }

        playerPasswords.remove(uuid);
        String lastIp = playerLastIps.remove(uuid);
        if (lastIp != null) {
            List<UUID> playersFromIp = ipToRegisteredPlayers.get(lastIp);
            if (playersFromIp != null) {
                playersFromIp.remove(uuid);
                if (playersFromIp.isEmpty()) {
                    ipToRegisteredPlayers.remove(lastIp);
                }
            }
        }
        playerDataConfig.set(uuid.toString(), null); // Remove the entire section for the player
        saveData();
        return true;
    }

    @Override
    public void saveData() {
        try {
            playerDataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml!" + e.getMessage());
        }
    }

    @Override
    public void loadData() {
        playerDataConfig = YamlConfiguration.loadConfiguration(dataFile);
        playerPasswords.clear();
        playerLastIps.clear();
        ipToRegisteredPlayers.clear();

        if (playerDataConfig.getKeys(false).isEmpty()) {
            return;
        }
        for (String uuidString : playerDataConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            String hashedPassword = playerDataConfig.getString(uuidString + ".password");
            String lastIp = playerDataConfig.getString(uuidString + ".last-ip");
            if (hashedPassword != null) {
                playerPasswords.put(uuid, hashedPassword);
            }
            if (lastIp != null) {
                playerLastIps.put(uuid, lastIp);
                ipToRegisteredPlayers.computeIfAbsent(lastIp, k -> new ArrayList<>()).add(uuid);
            }
        }
    }
}
