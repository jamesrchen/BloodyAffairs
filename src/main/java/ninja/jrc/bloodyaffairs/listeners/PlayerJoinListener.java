package ninja.jrc.bloodyaffairs.listeners;

import ninja.jrc.bloodyaffairs.managers.BAScoreboardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

public class PlayerJoinListener implements Listener {
    private final Logger logger;
    private final BAScoreboardManager baScoreboardManager;

    public PlayerJoinListener(Logger logger, BAScoreboardManager baScoreboardManager){
        this.logger = logger;
        this.baScoreboardManager = baScoreboardManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        baScoreboardManager.updateScoreboard(event.getPlayer());
    }

}
