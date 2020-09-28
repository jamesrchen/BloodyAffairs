package ninja.jrc.bloodyaffairs.tasks;

import ninja.jrc.bloodyaffairs.managers.BAScoreboardManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class UpdateScoreboardTask implements Runnable {
    private final Logger logger;
    private final Server server;
    private final BAScoreboardManager baScoreboardManager;

    public UpdateScoreboardTask(Logger logger, Server server, BAScoreboardManager baScoreboardManager){
        this.logger = logger;
        this.server = server;
        this.baScoreboardManager = baScoreboardManager;
    }

    @Override
    public void run() {

        for(Player player : server.getOnlinePlayers()){
            baScoreboardManager.updateScoreboard(player);
        }

    }
}
