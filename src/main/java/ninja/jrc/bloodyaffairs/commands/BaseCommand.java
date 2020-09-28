package ninja.jrc.bloodyaffairs.commands;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class BaseCommand implements CommandExecutor {
    private final Logger logger;
    private final BloodyAffairs plugin;

    public BaseCommand(Logger logger, BloodyAffairs plugin){
        this.logger = logger;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER,1f,1f);
        }
        sender.sendMessage(ChatColor.GREEN+"Bloody Affairs made by James Chen, Version: "+ChatColor.RED+plugin.getDescription().getVersion());
        return true;
    }

}
