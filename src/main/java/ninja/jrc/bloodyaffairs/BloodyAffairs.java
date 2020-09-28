package ninja.jrc.bloodyaffairs;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import ninja.jrc.bloodyaffairs.commands.*;
import ninja.jrc.bloodyaffairs.listeners.*;
import ninja.jrc.bloodyaffairs.managers.*;
import ninja.jrc.bloodyaffairs.objects.enchants.EmptyEnchant;
import ninja.jrc.bloodyaffairs.tasks.SaveDataTask;
import ninja.jrc.bloodyaffairs.tasks.UpdateScoreboardTask;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

import java.util.Arrays;
import java.util.logging.Logger;

public final class BloodyAffairs extends JavaPlugin {
    private CustomItemManager customItemManager;
    private EmptyEnchant emptyEnchant;

    private TownManager townManager;
    private NationManager nationManager;
    private StatsManager statsManager;

    private PersistentDataManager persistentDataManager;
    private BAScoreboardManager baScoreboardManager;
    private BAPlayerManager baPlayerManager;

    private Economy econ = null;
    private Chat chat = null;
    private DynmapAPI dynmapAPI;
    private final Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.logger.info("Initializing Bloody Affairs, Version: "+this.getDescription().getVersion());

        if (!setupEconomy() ) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupChat();

        if(!setupDynmap()){
            logger.severe(String.format("[%s] - Disabled due to no Dynmap dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        customItemManager = new CustomItemManager(this);
        townManager = new TownManager(this, econ, chat, dynmapAPI);
        nationManager = new NationManager();
        statsManager = new StatsManager();
        baPlayerManager = new BAPlayerManager();
        persistentDataManager = new PersistentDataManager(this, townManager, nationManager, statsManager, baPlayerManager);
        baScoreboardManager = new BAScoreboardManager(this, townManager, nationManager, statsManager, baPlayerManager);

        if (!persistentDataManager.readData()){
            logger.severe("Error encountered while reading data!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerEvents();
        registerCommands();
        registerTasks();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat(){
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public boolean setupDynmap(){
        if(getServer().getPluginManager().getPlugin("Dynmap") == null){
            return false;
        }
        dynmapAPI = (DynmapAPI) getServer().getPluginManager().getPlugin("Dynmap");
        return true;
    }

    public void registerEvents(){
        final PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(new PlayerChatListener(this, baPlayerManager, townManager, nationManager), this);

        pm.registerEvents(new BlockBreakListener(this, townManager), this);
        pm.registerEvents(new BlockPlaceListener(this, townManager), this);
        pm.registerEvents(new PlayerMoveListener(logger, townManager), this);
        pm.registerEvents(new PlayerJoinListener(logger, baScoreboardManager), this);
        pm.registerEvents(new EntityDamageListener(this, baPlayerManager), this);
        pm.registerEvents(new EntityExplodeListener(this, townManager), this);
        pm.registerEvents(new BlockExplodeListener(this, townManager), this);
        pm.registerEvents(new BlockSpreadListener(this, townManager), this);
        pm.registerEvents(new BlockIgniteListener(this, townManager), this);


    }

    public void registerCommands(){
        CommandsTabCompleter commandsTabCompleter = new CommandsTabCompleter(this, townManager, nationManager);

        getCommand("bloodyaffairsadmin").setExecutor(new AdminCommand(logger, this, townManager, nationManager));
        getCommand("bloodyaffairsadmin").setAliases(Arrays.asList("baa"));
        getCommand("bloodyaffairsadmin").setTabCompleter(commandsTabCompleter);

        getCommand("bloodyaffairs").setExecutor(new BaseCommand(logger, this));
        getCommand("bloodyaffairs").setAliases(Arrays.asList("ba", "bloodya", "baffairs"));
        getCommand("bloodyaffairs").setTabCompleter(commandsTabCompleter);

        getCommand("claim").setExecutor(new ClaimCommand(this, econ, townManager));
        getCommand("claim").setAliases(Arrays.asList("c"));
        getCommand("claim").setTabCompleter(commandsTabCompleter);

        getCommand("town").setExecutor((new TownCommand(this, econ, townManager)));
        getCommand("town").setAliases(Arrays.asList("t"));
        getCommand("town").setTabCompleter(commandsTabCompleter);
    }

    public void registerTasks(){

        this.getServer().getScheduler().scheduleSyncRepeatingTask(this,
                new UpdateScoreboardTask(logger, this.getServer(), baScoreboardManager),
                20*5L, 20L);

        this.getServer().getScheduler().scheduleSyncRepeatingTask(this,
                new SaveDataTask(this.persistentDataManager),
                20*60L, 20*120L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        persistentDataManager.saveData();
    }

}
