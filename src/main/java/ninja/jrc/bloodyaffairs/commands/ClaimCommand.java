package ninja.jrc.bloodyaffairs.commands;

import net.milkbowl.vault.economy.Economy;
import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.managers.TownManager;
import ninja.jrc.bloodyaffairs.objects.*;
import ninja.jrc.bloodyaffairs.utils.ChunkUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;

public class ClaimCommand implements CommandExecutor {
    private final BloodyAffairs plugin;
    private final Economy econ;
    private final TownManager townManager;

    public ClaimCommand(BloodyAffairs plugin, Economy econ, TownManager townManager){
        this.plugin = plugin;
        this.econ = econ;
        this.townManager = townManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("You need to be a player to use this command!");
            return true;
        }
        Player player = (Player) sender;

        String chunkID = ChunkUtils.getChunkID(player.getLocation().getChunk().getChunkSnapshot());

        if(args.length < 1){
            sender.sendMessage(Lang.INSUFFICIENT_ARGUMENTS.toStringWithPrefix());
            return false;
        }

        switch (args[0].toLowerCase()){

            case "toggle": {
                if(args.length < 2){
                    sender.sendMessage(Lang.INSUFFICIENT_ARGUMENTS.toStringWithPrefix());
                    return false;
                }
                String claimPropertyString = args[1].toUpperCase();
                if(!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.ASSISTANT)){
                    sender.sendMessage(Lang.INSUFFICIENT_TOWN_PERMISSIONS.toStringWithPrefix());
                    return true;
                }
                Town town = townManager.findTownByMemberUUID(player.getUniqueId()).get();

                Optional<Claim> optionalClaim = townManager.getChunkClaim(chunkID);
                if(!optionalClaim.isPresent() || !town.getClaims().contains(optionalClaim.get())){
                    sender.sendMessage(Lang.LAND_NOT_CLAIMED.toStringWithPrefix());
                    return true;
                }
                Claim claim = optionalClaim.get();

                try{
                    ClaimProperty claimProperty = ClaimProperty.valueOf(claimPropertyString);
                    Set<ClaimProperty> claimProperties = claim.getClaimProperties();
                    if(!claimProperties.contains(claimProperty)){
                        claimProperties.add(claimProperty);
                        sender.sendMessage(String.format(Lang.ENABLE_CLAIM_PROPERTY.toStringWithPrefix(), claimPropertyString));
                        return true;
                    }else{
                        claimProperties.remove(claimProperty);
                        sender.sendMessage(String.format(Lang.DISABLE_CLAIM_PROPERTY.toStringWithPrefix(), claimPropertyString));
                        return true;
                    }

                }catch (IllegalArgumentException e){
                    sender.sendMessage(String.format(Lang.NOT_CLAIM_PROPERTY.toStringWithPrefix(), claimPropertyString));
                    return true;
                }

            }

        }


        return false;
    }
}
