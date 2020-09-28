package ninja.jrc.bloodyaffairs.commands;

import net.milkbowl.vault.economy.Economy;
import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.managers.TownManager;
import ninja.jrc.bloodyaffairs.objects.*;
import ninja.jrc.bloodyaffairs.utils.ChunkUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

// TownCommand should be directly responsible for TownManager, and not interact with NationManager
public class TownCommand implements CommandExecutor {
    private final Logger logger;
    private final BloodyAffairs plugin;
    private final Economy econ;

    private final TownManager townManager;

    public TownCommand(BloodyAffairs plugin, Economy econ, TownManager townManager){
        this.logger = plugin.getLogger();
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

        if(args.length < 1){
            sender.sendMessage(ChatColor.RED+"Arguments not provided!");
            return false;
        }

        Player player = (Player) sender;
        
        switch(args[0].toLowerCase()){

            // Town leader commands //

            // Create a town
            case "create": {
                // Input validation
                if (args.length < 2) {
                    sender.sendMessage(Lang.INSUFFICIENT_ARGUMENTS.toStringWithPrefix());
                    return false;
                }
                if (townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER)) {
                    sender.sendMessage(Lang.ALREADY_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                if (townManager.findTownByName(args[1].toLowerCase()).isPresent()) {
                    sender.sendMessage(Lang.TOWN_ALREADY_EXISTS.toStringWithPrefix());
                    return true;
                }

                // Input has been validated and now create a new town
                Town town = townManager.createTown(args[1].toLowerCase(), player.getUniqueId());
                plugin.getServer().broadcastMessage(String.format(Lang.TOWN_CREATED.toStringWithPrefix(), town.getName(), player.getDisplayName()));
                return true;
            }

            // Disband town
            case "disband": {
                Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER) || !optionalTown.isPresent()) {
                    sender.sendMessage(Lang.NOT_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                Town town = optionalTown.get();
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.LEADER)) {
                    sender.sendMessage(Lang.NOT_TOWN_LEADER.toStringWithPrefix());
                    return true;
                }

                townManager.removeTown(town.getUUID());
                plugin.getServer().broadcastMessage(String.format(Lang.TOWN_DISBANDED.toStringWithPrefix(), town.getName(), player.getDisplayName()));
                return true;
            }

            // Claim current chunk
            case "claim":
            case "c": {
                Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER) || !optionalTown.isPresent()) {
                    sender.sendMessage(Lang.NOT_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                Town town = optionalTown.get();
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.ASSISTANT)) {
                    sender.sendMessage(Lang.INSUFFICIENT_TOWN_PERMISSIONS.toStringWithPrefix());
                    return true;
                }
                String chunkID = ChunkUtils.getChunkID(player.getLocation().getChunk().getChunkSnapshot());
                Optional<Claim> optionalClaim = townManager.getChunkClaim(chunkID);
                if(optionalClaim.isPresent()){
                    sender.sendMessage(Lang.LAND_ALREADY_CLAIMED.toStringWithPrefix());
                    return true;
                }

                Claim claim = townManager.makeChunkClaim(player.getLocation().getChunk().getChunkSnapshot(), town.getUUID());
                sender.sendMessage(Lang.LAND_CLAIMED_SUCCESS.toStringWithPrefix());
                if(claim.getTown().getClaims().size() == 1){
                    townManager.setHomeChunk(claim.getChunkID(), player.getLocation());
                }
                return true;

            }

            case "unclaim": {
                Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER) || !optionalTown.isPresent()) {
                    sender.sendMessage(Lang.NOT_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }

                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.ASSISTANT)) {
                    sender.sendMessage(Lang.INSUFFICIENT_TOWN_PERMISSIONS.toStringWithPrefix());
                    return true;
                }
                String chunkID = ChunkUtils.getChunkID(player.getLocation().getChunk().getChunkSnapshot());
                Optional<Claim> optionalClaim = townManager.getChunkClaim(chunkID);
                if(!optionalClaim.isPresent() || !optionalClaim.get().getTown().equals(optionalTown.get())){
                    sender.sendMessage(Lang.LAND_NOT_CLAIMED.toStringWithPrefix());
                    return true;
                }
                townManager.removeClaim(chunkID);
                sender.sendMessage(Lang.LAND_UNCLAIMED_SUCCESS.toStringWithPrefix());
                return true;
            }

            case "deposit":
            case "dep": {
                if(!(args.length > 1) || !StringUtils.isNumeric(args[1])){
                    sender.sendMessage(Lang.INSUFFICIENT_ARGUMENTS.toString());
                    return true;
                }

                return true;

            }

            case "sethome": {
                Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER) || !optionalTown.isPresent()) {
                    sender.sendMessage(Lang.NOT_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                Town town = optionalTown.get();
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.ASSISTANT)) {
                    sender.sendMessage(Lang.INSUFFICIENT_TOWN_PERMISSIONS.toStringWithPrefix());
                    return true;
                }
                String chunkID = ChunkUtils.getChunkID(player.getLocation().getChunk().getChunkSnapshot());
                Optional<Claim> optionalClaim = townManager.getChunkClaim(chunkID);
                if(!optionalClaim.isPresent() || !optionalClaim.get().getTown().equals(town)){
                    sender.sendMessage(Lang.LAND_NOT_CLAIMED.toStringWithPrefix());
                    return true;
                }

                townManager.setHomeChunk(chunkID, player.getLocation());
                return true;


            }

            case "home": {
                Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER) || !optionalTown.isPresent()) {
                    sender.sendMessage(Lang.NOT_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }

                Optional<Location> optionalLocation =  townManager.getHomeChunk(optionalTown.get().getUUID());
                if(!optionalLocation.isPresent()){
                    player.sendMessage(Lang.NO_HOME_CHUNK.toStringWithPrefix());
                    return true;
                }else{
                    player.teleport(optionalLocation.get());
                    return true;
                }

            }

            // List all towns
            case "list":
            case "ls":
            case "l": {
                Collection<Town> towns = townManager.getAllTowns();
                String finalMsg = ChatColor.YELLOW+"________["+Lang.TOWN_LIST.toString()+ChatColor.YELLOW+"]________";
                finalMsg+=ChatColor.RESET;
                for(Town town : towns){
                    finalMsg+="\n";
                    finalMsg+=ChatColor.GREEN;
                    finalMsg+=town.getName();
                    finalMsg+=ChatColor.RED;
                    finalMsg+=" - ";
                    finalMsg+=ChatColor.RED;
                    finalMsg+="(";
                    finalMsg+=ChatColor.GREEN;
                    finalMsg+=town.getMemberUUIDs().size();
                    finalMsg+=ChatColor.RED;
                    finalMsg+=")";
                }
                sender.sendMessage(finalMsg);
                return true;
            }

            // Invite a player to town
            case "invite":
            case "i": {
                Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER) || !optionalTown.isPresent()) {
                    sender.sendMessage(Lang.NOT_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                Town town = optionalTown.get();
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.ASSISTANT)) {
                    sender.sendMessage(Lang.INSUFFICIENT_TOWN_PERMISSIONS.toStringWithPrefix());
                    return true;
                }
                if(args.length < 2){
                    sender.sendMessage(Lang.INSUFFICIENT_ARGUMENTS.toStringWithPrefix());
                    return false;
                }

                Player invitee = plugin.getServer().getPlayer(args[1]);
                if(invitee == null){
                    sender.sendMessage(Lang.NO_SUCH_PLAYER.toStringWithPrefix());
                    return true;
                }
                if(townManager.checkPermission(invitee.getUniqueId(), TownPermissionScopes.MEMBER)){
                    sender.sendMessage(Lang.INVITEE_ALREADY_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }

                townManager.makeInvite(town, player.getUniqueId(), invitee.getUniqueId());
                sender.sendMessage(String.format(Lang.INVITE_SUCCESS.toStringWithPrefix(), invitee.getDisplayName()));

                String inviteMessage = "";
                inviteMessage += String.format(Lang.INVITED.toStringWithPrefix(), player.getDisplayName(), town.getName());

                invitee.sendMessage(inviteMessage);
                return true;

            }

            // Accept a town invite
            case "accept":
            case "a": {
                if (townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER)) {
                    sender.sendMessage(Lang.ALREADY_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                Optional<TownInvite> optionalTownInvite = townManager.getTownInvite(player.getUniqueId());
                if(!optionalTownInvite.isPresent()){
                    sender.sendMessage(Lang.NO_INVITES.toStringWithPrefix());
                    return true;
                }

                TownInvite townInvite = optionalTownInvite.get();
                Player inviter = plugin.getServer().getPlayer(townInvite.getInviter());
                if(inviter != null) {
                    if (inviter.isOnline()) {
                        inviter.sendMessage(String.format(Lang.INVITATION_ACCEPTED.toStringWithPrefix(), player.getDisplayName()));
                    }

                    player.sendMessage(String.format(Lang.ACCEPTED_INVITE.toStringWithPrefix(), townInvite.getTown().getName()));
                    townManager.removeTownInvite(player.getUniqueId());
                    townManager.addTownMember(townInvite.getTown().getUUID(), player.getUniqueId());
                    return true;
                }
            }

            // Leave a town
            case "leave": {
                Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER) || !optionalTown.isPresent()) {
                    sender.sendMessage(Lang.NOT_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                Town town = optionalTown.get();
                if (townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.LEADER)) {
                    sender.sendMessage(Lang.IS_TOWN_LEADER.toStringWithPrefix());
                    return true;
                }
                townManager.removeTownMember(player.getUniqueId());
                player.sendMessage(String.format(Lang.LEFT_TOWN.toStringWithPrefix(), town.getName()));
                return true;

            }

            // Kick others from a town
            case "kick":
            case "k": {
                Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER) || !optionalTown.isPresent()) {
                    sender.sendMessage(Lang.NOT_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                Town town = optionalTown.get();
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.LEADER)) {
                    sender.sendMessage(Lang.NOT_TOWN_LEADER.toStringWithPrefix());
                    return true;
                }
                if(args.length < 2){
                    sender.sendMessage(Lang.INSUFFICIENT_ARGUMENTS.toStringWithPrefix());
                    return false;
                }

                Player selectedPlayer = plugin.getServer().getPlayer(args[1]);
                if(selectedPlayer == null){
                    sender.sendMessage(Lang.NO_SUCH_PLAYER.toStringWithPrefix());
                    return true;
                }
                Optional<Town> optionalSelectedMemberTown = townManager.findTownByMemberUUID(selectedPlayer.getUniqueId());
                if(!optionalSelectedMemberTown.isPresent()){
                    sender.sendMessage(Lang.NO_SUCH_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                if(!optionalSelectedMemberTown.get().equals(town)){
                    sender.sendMessage(Lang.NOT_IN_PLAYER_TOWN.toStringWithPrefix());
                    return true;
                }

                selectedPlayer.sendMessage(String.format(Lang.KICKED_FROM_TOWN.toStringWithPrefix(), town.getName(), player.getDisplayName()));
                townManager.removeTownMember(selectedPlayer.getUniqueId());
                return true;

            }

            // Promote a member of town
            case "promote":
            case "p": {
                // Validate the player
                Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER) || !optionalTown.isPresent()) {
                    sender.sendMessage(Lang.NOT_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                Town town = optionalTown.get();
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.LEADER)) {
                    sender.sendMessage(Lang.NOT_TOWN_LEADER.toStringWithPrefix());
                    return true;
                }
                if(args.length < 2){
                    sender.sendMessage(Lang.INSUFFICIENT_ARGUMENTS.toStringWithPrefix());
                    return false;
                }

                // Validate the specified player
                Player selectedPlayer = plugin.getServer().getPlayer(args[1]);
                if(selectedPlayer == null){
                    sender.sendMessage(Lang.NO_SUCH_PLAYER.toStringWithPrefix());
                    return true;
                }
                if(!townManager.checkPermission(selectedPlayer.getUniqueId(), TownPermissionScopes.MEMBER)){
                    sender.sendMessage(Lang.NO_SUCH_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                // Check if specified player is in the same town
                if(!town.getMemberUUIDs().contains(selectedPlayer.getUniqueId())){
                    sender.sendMessage(Lang.NOT_IN_PLAYER_TOWN.toStringWithPrefix());
                    return true;
                }
                // Check if specified player is leader
                if(townManager.checkPermission(selectedPlayer.getUniqueId(), TownPermissionScopes.LEADER)){
                    sender.sendMessage(Lang.CANNOT_CHANGE_PERMISSION.toStringWithPrefix());
                    return true;
                }
                /// Check if player is already an assistant
                if(townManager.checkPermission(selectedPlayer.getUniqueId(), TownPermissionScopes.ASSISTANT)){
                    sender.sendMessage(Lang.PLAYER_ALREADY_ASSISTANT.toStringWithPrefix());
                    return true;
                }

                townManager.promoteTownMember(selectedPlayer.getUniqueId());

                return true;

            }

            // Demote a member of town
            case "demote":
            case "d": {
                // Validate the player
                Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.MEMBER) || !optionalTown.isPresent()) {
                    sender.sendMessage(Lang.NOT_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                Town town = optionalTown.get();
                if (!townManager.checkPermission(player.getUniqueId(), TownPermissionScopes.LEADER)) {
                    sender.sendMessage(Lang.NOT_TOWN_LEADER.toStringWithPrefix());
                    return true;
                }
                if(args.length < 2){
                    sender.sendMessage(Lang.INSUFFICIENT_ARGUMENTS.toStringWithPrefix());
                    return false;
                }

                // Validate the specified player
                Player selectedPlayer = plugin.getServer().getPlayer(args[1]);

                if(selectedPlayer == null){
                    sender.sendMessage(Lang.NO_SUCH_PLAYER.toStringWithPrefix());
                    return true;
                }
                if(!townManager.checkPermission(selectedPlayer.getUniqueId(), TownPermissionScopes.MEMBER)){
                    sender.sendMessage(Lang.NO_SUCH_TOWN_MEMBER.toStringWithPrefix());
                    return true;
                }
                // Check is specified player is not in a town
                if(!town.getMemberUUIDs().contains(selectedPlayer.getUniqueId())){
                    sender.sendMessage(Lang.NOT_IN_PLAYER_TOWN.toStringWithPrefix());
                    return true;
                }
                // Check if specified player is leader
                if(townManager.checkPermission(selectedPlayer.getUniqueId(), TownPermissionScopes.LEADER)){
                    sender.sendMessage(Lang.CANNOT_CHANGE_PERMISSION.toStringWithPrefix());
                    return true;
                }
                // Check is player is not an assistant
                if(!townManager.checkPermission(selectedPlayer.getUniqueId(), TownPermissionScopes.ASSISTANT)){
                    sender.sendMessage(Lang.PLAYER_NOT_ASSISTANT.toStringWithPrefix());
                    return true;
                }

                townManager.demoteTownMember(selectedPlayer.getUniqueId());
                return true;
            }

            default:
                return false;
        }

    }
}
