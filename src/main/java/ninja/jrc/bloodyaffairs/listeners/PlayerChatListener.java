package ninja.jrc.bloodyaffairs.listeners;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.managers.BAPlayerManager;
import ninja.jrc.bloodyaffairs.managers.NationManager;
import ninja.jrc.bloodyaffairs.managers.TownManager;
import ninja.jrc.bloodyaffairs.objects.BAPlayer;
import ninja.jrc.bloodyaffairs.objects.Town;
import ninja.jrc.bloodyaffairs.utils.PrefixUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerChatListener implements Listener {
    private final BloodyAffairs plugin;
    private final BAPlayerManager baPlayerManager;
    private final TownManager townManager;
    private final NationManager nationManager;

    public PlayerChatListener(BloodyAffairs plugin, BAPlayerManager baPlayerManager, TownManager townManager, NationManager nationManager){
        this.plugin = plugin;
        this.baPlayerManager = baPlayerManager;
        this.townManager = townManager;
        this.nationManager = nationManager;
    }

    @EventHandler
    public void OnChatEvent(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        Optional<Town> optionalTown =  townManager.findTownByMemberUUID(player.getUniqueId());

        String repPrefix =  PrefixUtil.getRepPrefix(baPlayerManager.getReputation(player.getUniqueId()));
        String townPrefix = optionalTown.isPresent() ? PrefixUtil.getTownPrefix(optionalTown.get()) : "";
        event.setFormat("<"+repPrefix+" | "+townPrefix+"%s> %s");
    }

}
