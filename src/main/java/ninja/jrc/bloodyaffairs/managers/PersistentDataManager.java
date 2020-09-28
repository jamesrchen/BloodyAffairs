package ninja.jrc.bloodyaffairs.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.objects.BAPlayer;
import ninja.jrc.bloodyaffairs.objects.Town;

import java.io.*;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

public class PersistentDataManager {
    private final Gson gson = new Gson();

    private final BloodyAffairs plugin;
    private final Logger logger;

    private final TownManager townManager;
    private final NationManager nationManager;
    private final StatsManager statsManager;
    private final BAPlayerManager baPlayerManager;

    private final File dataDir;

    private final File townFile;
    private final File playerFile;

    public PersistentDataManager(BloodyAffairs plugin, TownManager townManager, NationManager nationManager, StatsManager statsManager, BAPlayerManager baPlayerManager){
        this.plugin = plugin;
        this.logger = this.plugin.getLogger();

        this.townManager = townManager;
        this.nationManager = nationManager;
        this.statsManager = statsManager;
        this.baPlayerManager = baPlayerManager;

        dataDir = new File(this.plugin.getDataFolder(), "data");
        townFile = new File(dataDir, "towns.json");
        playerFile = new File(dataDir, "players.json");

        if(this.plugin.getDataFolder().mkdir()){
            logger.info("No plugin directory was found, created plugin directory");
        }

        if(dataDir.mkdir()){
            logger.info("No data directory was found, created data directory");
        }else{
            logger.info("Data directory found");
        }

        try {
            if(townFile.createNewFile()){
                logger.info("No towns.json file was found, created towns.json");
            }
            if(playerFile.createNewFile()){
                logger.info("No players.json file was found, created players.json");
            }

        } catch (IOException e) {
            logger.severe(e.toString());
            logger.severe("Error while creating file, disabling plugin");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

    }

    /**
     * Saves town data in a JSON
     *
     * @return true if saved data, false if did not save data
     */
    public boolean saveData(){
        logger.info("Saving data...");

        Collection<Town> towns = townManager.getAllTowns();
        Collection<BAPlayer> BAPlayers = baPlayerManager.getBAPlayers();

        String townsJSON = this.gson.toJson(towns, new TypeToken<Collection<Town>>(){}.getType());
        String BAPlayersJSON = this.gson.toJson(BAPlayers, new TypeToken<Collection<BAPlayer>>(){}.getType());

        try {
            FileWriter townsFileWriter = new FileWriter(townFile);
            FileWriter BAPlayersFileWriter = new FileWriter(playerFile);

            townsFileWriter.write(townsJSON);
            townsFileWriter.close();

            BAPlayersFileWriter.write(BAPlayersJSON);
            BAPlayersFileWriter.close();

        } catch (IOException e) {
            logger.severe(e.toString());
            logger.severe("Error while writing to file, disabling plugin");
            this.plugin.getServer().getPluginManager().disablePlugin(plugin);
            return false;
        }
        logger.info("Data saved!");
        return true;
    }

    public boolean readData(){
        logger.info("Reading data...");


        try {
            FileReader townFileReader = new FileReader(townFile);
            FileReader playerFileReader = new FileReader(playerFile);

            Set<Town> towns = gson.fromJson(townFileReader, new TypeToken<Set<Town>>(){}.getType());
            Set<BAPlayer> BAPlayers = gson.fromJson(playerFileReader, new TypeToken<Set<BAPlayer>>(){}.getType());

            if(townFile.length() <= 0){
                logger.info("towns.json is empty");
            }else{
                townManager.setAllTowns(towns);
            }
            if(playerFile.length() <= 0){
                logger.info("players.json is empty");
            }else{
                baPlayerManager.setAllBAPlayers(BAPlayers);
            }

        } catch (FileNotFoundException e) {
            logger.severe(e.toString());
            logger.severe("Error while reading from file, disabling plugin");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return false;
        }
        logger.info("Data read and saved!");
        return true;
    }


}
