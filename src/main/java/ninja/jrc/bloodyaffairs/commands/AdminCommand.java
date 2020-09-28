package ninja.jrc.bloodyaffairs.commands;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.managers.NationManager;
import ninja.jrc.bloodyaffairs.managers.TownManager;
import ninja.jrc.bloodyaffairs.utils.ChunkUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class AdminCommand implements CommandExecutor {
    private final Logger logger;
    private final BloodyAffairs plugin;
    private final TownManager townManager;
    private final NationManager nationManager;

    public AdminCommand(Logger logger, BloodyAffairs plugin, TownManager townManager, NationManager nationManager){
        this.logger = logger;
        this.plugin = plugin;
        this.townManager = townManager;
        this.nationManager = nationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("bloodyaffairs.admin")){
            return true;
        }

        if(args.length < 1){
            return true;
        }

        switch (args[0]){
            case "unclaim": {
                if(!(sender instanceof Player)){
                    return false;
                }
                String chunkID = ChunkUtils.getChunkID(((Player) sender).getLocation().getChunk().getChunkSnapshot());
                if(!townManager.getChunkClaim(chunkID).isPresent()){
                    sender.sendMessage("Not a claim!");
                    return false;
                }else{
                    townManager.removeClaim(chunkID);
                    sender.sendMessage(ChatColor.GREEN+"Done!");
                    return true;
                }
            }
            case "bypass":
                if(!(sender instanceof Player)){
                    return false;
                }
                Player player = (Player) sender;
                Set<UUID> adminBypass = townManager.getAdminBypass();
                if(adminBypass.contains(player.getUniqueId())){
                    sender.sendMessage(ChatColor.GREEN+"Disabling admin bypass!");
                    adminBypass.remove(player.getUniqueId());
                }else{
                    sender.sendMessage(ChatColor.GREEN+"Enabling admin bypass!");
                    adminBypass.add(player.getUniqueId());
                }

        }

        return true;
    }
}
