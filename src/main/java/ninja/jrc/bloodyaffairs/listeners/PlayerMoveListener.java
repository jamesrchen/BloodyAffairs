package ninja.jrc.bloodyaffairs.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import ninja.jrc.bloodyaffairs.managers.TownManager;
import ninja.jrc.bloodyaffairs.objects.Claim;
import ninja.jrc.bloodyaffairs.objects.Town;
import ninja.jrc.bloodyaffairs.utils.ChunkUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;
import java.util.logging.Logger;

public class PlayerMoveListener implements Listener {
    private final Logger logger;
    private final TownManager townManager;

    public PlayerMoveListener(Logger logger, TownManager townManager){
        this.logger = logger;
        this.townManager = townManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
    }
}
