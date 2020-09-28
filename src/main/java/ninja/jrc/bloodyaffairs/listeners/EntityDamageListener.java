package ninja.jrc.bloodyaffairs.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.managers.BAPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.awt.*;

public class EntityDamageListener implements Listener {
    private final BloodyAffairs plugin;
    private final BAPlayerManager baPlayerManager;

    public EntityDamageListener(BloodyAffairs plugin, BAPlayerManager baPlayerManager){
        this.plugin = plugin;
        this.baPlayerManager = baPlayerManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player murderer = (Player) event.getDamager();
            if(event.getEntity() instanceof Player){
                if(((Player)event.getEntity()).getHealth() - event.getFinalDamage() <= 0){
                    murderer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("-100 "+ChatColor.RED+"REP"));
                    baPlayerManager.addReputation(murderer.getUniqueId(), -100);
                    return;
                }
            }
            else if(event.getEntity() instanceof Mob){
                if(((Mob) event.getEntity()).getHealth() - event.getFinalDamage() <= 0){
                    if(event.getEntity() instanceof Villager){
                        murderer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("-100 "+ChatColor.RED+"REP"));
                        plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', event.getEntity().getName()+" was slain by "+murderer.getName()+" &2(-100 Rep)"));
                        baPlayerManager.addReputation(murderer.getUniqueId(), -100);
                        return;
                    }
                    baPlayerManager.addReputation(murderer.getUniqueId(), 1);
                    return;
                }
            }
        }
    }

}
