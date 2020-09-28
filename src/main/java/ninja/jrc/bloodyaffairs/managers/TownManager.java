package ninja.jrc.bloodyaffairs.managers;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.objects.*;
import ninja.jrc.bloodyaffairs.utils.ChunkUtils;
import ninja.jrc.bloodyaffairs.utils.PrefixUtil;
import org.bukkit.ChatColor;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import java.util.*;

// Town Manager controls both Town and Claim objects
public class TownManager {
    private final BloodyAffairs plugin;

    private final Economy econ;
    private final Chat chat;

    private final DynmapAPI dynmapAPI;
    private final MarkerSet markerSet;
    private final MarkerIcon homeIcon;

    // Town Objects
    private final Map<UUID, Town> townsByUUID = new HashMap<>();
    private final Map<String, Town> townsByName = new HashMap<>();
    private final Map<UUID, Town> townsByMemberUUID = new HashMap<>();

    // Claim Objects
    private final Map<String, Claim> claimByChunkID = new HashMap<>();

    private final Map<Claim, AreaMarker> areaMarkerByClaim = new HashMap<>();
    private final Map<Town, Marker> homeMarkerByClaim = new HashMap<>();

    // TownInvite Objects
    private final Map<UUID, TownInvite> inviteByInviteeUUID = new HashMap<>();

    // Admin bypass set
    private final Set<UUID> adminBypass = new HashSet<>();
    public Set<UUID> getAdminBypass() {
        return adminBypass;
    }
    /////////////////////////////////////////////////////////

    public TownManager(BloodyAffairs plugin, Economy econ, Chat chat, DynmapAPI dynmapAPI){
        this.plugin = plugin;

        this.econ = econ;
        this.chat = chat;

        plugin.getLogger().info("Creating markerset");
        this.dynmapAPI = dynmapAPI;
        if(dynmapAPI.getMarkerAPI().getMarkerSet("bloodyaffairs.markerset") == null){
            this.markerSet = dynmapAPI.getMarkerAPI().createMarkerSet("bloodyaffairs.markerset", "Towns", dynmapAPI.getMarkerAPI().getMarkerIcons(), false);
        }else {
            this.markerSet = dynmapAPI.getMarkerAPI().getMarkerSet("bloodyaffairs.markerset");
            this.markerSet.setMarkerSetLabel("Towns");
        }
        if(markerSet == null) {
            plugin.getLogger().severe("Error creating marker set");
            throw new NullPointerException("Error creating marker set");
        }
        for(AreaMarker areaMarker : this.markerSet.getAreaMarkers()){
            areaMarker.deleteMarker();
        }
        for(Marker marker : this.markerSet.getMarkers()){
            marker.deleteMarker();
        }

        this.homeIcon = dynmapAPI.getMarkerAPI().getMarkerIcon("default");

    }

    public Collection<Town> getAllTowns(){
        return townsByUUID.values();
    }

    public void setAllTowns(Set<Town> towns){
        for(Town town : towns){
            townsByUUID.put(town.getUUID(), town);
            townsByName.put(town.getName(), town);
            for(UUID memberUUID : town.getMemberUUIDs()){
                townsByMemberUUID.put(memberUUID, town);
            }
            for(Claim claim : town.getClaims()){
                claim.setTown(town);
                claim.setChunkSnapshot(plugin.getServer().getWorld(claim.getWorld()).getChunkAt(claim.getChunkX(), claim.getChunkZ()).getChunkSnapshot());
                claimByChunkID.put(claim.getChunkID(), claim);
                addClaimMarker(claim);
            }
        }
    }

    public Set<String> getAllTownNames(){
        return townsByName.keySet();
    }

    public Optional<Town> findTownByUUID(UUID townUUID){
        return Optional.ofNullable(townsByUUID.getOrDefault(townUUID, null));
    }

    public Optional<Town> findTownByName(String name){
        return Optional.ofNullable(townsByName.getOrDefault(name, null));
    }

    public Optional<Town> findTownByMemberUUID(UUID memberUUID){
        return Optional.ofNullable(townsByMemberUUID.getOrDefault(memberUUID, null));
    }

    /**
     * Store data from a set of towns(for persistent data manager to store after getting data).
     *
     * @param townSet set of town objects
     */
    public void storeData(Set<Town> townSet){
        for(Town town : townSet){
            townsByUUID.put(town.getUUID(), town);
            townsByName.put(town.getName(), town);
            for(UUID memberUUID : town.getMemberUUIDs()){
                townsByMemberUUID.put(memberUUID, town);
            }
        }
    }

    /**
     * Creates a new town.
     *
     * @param name Name of the town
     * @param leaderUUID UUID of the player who will be the leader of the town
     * @return Optional of Town. Empty if town already exists
     */
    public Town createTown(String name, UUID leaderUUID){
        if(townsByName.containsKey(name.toLowerCase())){
            throw new IllegalArgumentException("Cannot create a town with the same name as another town");
        }
        Town town = new Town(name.toLowerCase(), leaderUUID);
        townsByUUID.put(town.getUUID(), town);
        townsByName.put(town.getName(), town);
        townsByMemberUUID.put(leaderUUID, town);
        return town;
    }

    /**
     * Removes a town.
     *
     * @param townUUID UUID of the town
     */
    public void removeTown(UUID townUUID){
        if(!townsByUUID.containsKey(townUUID)){
            throw new IllegalArgumentException("Cannot remove a town that does not exist");
        }
        Town town = townsByUUID.get(townUUID);
        townsByUUID.remove(townUUID);
        townsByName.remove(town.getName());
        for(UUID memberUUID : town.getMemberUUIDs()){
            townsByMemberUUID.remove(memberUUID);
        }
        for(Claim claim: town.getClaims()){
            removeClaimMarker(claim);
            claimByChunkID.remove(claim.getChunkID());
        }

        // Cannot use a for loop with keySet apparently
        Iterator<UUID> iterator = inviteByInviteeUUID.keySet().iterator();

        while(iterator.hasNext()){
            UUID inviteeUUID = iterator.next();
            if(inviteByInviteeUUID.get(inviteeUUID).getTown().equals(town)){
                inviteByInviteeUUID.remove(inviteeUUID);
            }
        }
    }

    /**
     * Add a member to a town
     *
     * @param townUUID UUID of the town
     * @param playerUUID UUID of player
     */
    public void addTownMember(UUID townUUID, UUID playerUUID){
        if(this.townsByMemberUUID.containsKey(playerUUID)){
            throw new IllegalArgumentException("Cannot add a player already in a town to another town");
        }
        if(!this.townsByUUID.containsKey(townUUID)){
            throw new IllegalArgumentException("Cannot add a player to a town that does not exist");
        }
        Town town = townsByUUID.get(townUUID);
        town.getMemberUUIDs().add(playerUUID);
        townsByMemberUUID.put(playerUUID, town);
        Player invitedPlayer = plugin.getServer().getPlayer(playerUUID);
        for(UUID memberUUID : town.getMemberUUIDs()){
            Player player = plugin.getServer().getPlayer(memberUUID);
            if(invitedPlayer != null && player != null && player.isOnline()){
                player.sendMessage(String.format(Lang.NEW_TOWN_MEMBER.toStringWithPrefix(), invitedPlayer.getDisplayName()));
            }
        }
    }

    /**
     * Remove member from a town
     *
     * @param playerUUID UUID of player who is to be removed from town
     */
    public void removeTownMember(UUID playerUUID){
        if(!townsByMemberUUID.containsKey(playerUUID)){
            throw new IllegalArgumentException("Cannot remove a player who is not a town member");
        }
        Town town = townsByMemberUUID.get(playerUUID);

        if(town.getLeaderUUID().equals(playerUUID)){
            throw new IllegalArgumentException("Cannot remove town leader");
        }

        town.removeMember(playerUUID);

        townsByMemberUUID.remove(playerUUID);

        Player player = plugin.getServer().getPlayer(playerUUID);
        if(player != null){
            for(UUID memberUUID : town.getMemberUUIDs()){
                Player member = plugin.getServer().getPlayer(memberUUID);
                if(member != null &&  member.isOnline()){
                    member.sendMessage(String.format(Lang.TOWN_MEMBER_LEFT.toStringWithPrefix(), player.getDisplayName()));
                }
            }
        }
    }

    /**
     * Promote a member
     *
     * @param playerUUID UUID of player who is to be promoted
     */
    public void promoteTownMember(UUID playerUUID){
        if(!townsByMemberUUID.containsKey(playerUUID)) {
            throw new IllegalArgumentException("Cannot promote a player who is not a town member");
        }

        Town town = townsByMemberUUID.get(playerUUID);

        if(town.getAssistantUUIDs().contains(playerUUID)){
            throw new IllegalArgumentException("Cannot promote a town assistant");
        }
        if(town.getLeaderUUID().equals(playerUUID)){
            throw new IllegalArgumentException("Cannot promote a town leader");
        }

        town.promoteMember(playerUUID);

        Player player = plugin.getServer().getPlayer(playerUUID);
        if(player != null){
            for(UUID memberUUID : town.getMemberUUIDs()){
                Player member = plugin.getServer().getPlayer(memberUUID);
                if(member != null && member.isOnline()){
                    member.sendMessage(String.format(Lang.TOWN_MEMBER_PROMOTED.toStringWithPrefix(), player.getDisplayName()));
                }
            }
        }
    }

    /**
     * Demote a member
     *
     * @param playerUUID UUID of player who is to be demoted
     */
    public void demoteTownMember(UUID playerUUID){
        if(!townsByMemberUUID.containsKey(playerUUID)){
            throw new IllegalArgumentException("Cannot demote a player who is not a town member");
        }

        Town town = townsByMemberUUID.get(playerUUID);

        if(!town.getAssistantUUIDs().contains(playerUUID)){
            throw new IllegalArgumentException("Cannot demote a town member who is not assistant");
        }
        if(town.getLeaderUUID().equals(playerUUID)){
            throw new IllegalArgumentException("Cannot demote a town leader");
        }

        town.demoteMember(playerUUID);

        for(UUID memberUUID : town.getMemberUUIDs()){
            Player member = plugin.getServer().getPlayer(memberUUID);
            Player player = plugin.getServer().getPlayer(playerUUID);
            if(member != null && player != null && member.isOnline()){
                member.sendMessage(String.format(Lang.TOWN_MEMBER_DEMOTED.toStringWithPrefix(), player.getDisplayName()));
            }
        }
    }

    /**
     * Check if the player has permissions of a role in their town
     *
     * @param playerUUID UUID of player
     * @return true if player has permissions, false if player does not have permissions
     */
    public boolean checkPermission(UUID playerUUID, TownPermissionScopes scopes){
        if(!townsByMemberUUID.containsKey(playerUUID)){
            return false;
        }
        else if(scopes.equals(TownPermissionScopes.MEMBER)){
            return townsByMemberUUID.containsKey(playerUUID);
        }
        else if(scopes.equals(TownPermissionScopes.ASSISTANT)){
            Town town = townsByMemberUUID.get(playerUUID);
            return town.getAssistantUUIDs().contains(playerUUID) || town.getLeaderUUID().equals(playerUUID);
        }
        else if(scopes.equals(TownPermissionScopes.LEADER)){
            Town town = townsByMemberUUID.get(playerUUID);

            return town.getLeaderUUID().equals(playerUUID);
        }
        else {
            return false;
        }
    }

    // Claiming //

    /**
     * Check if a chunk is claimed
     *
     * @param chunkID ID of chunk
     * @return true if chunk is claimed, false if it is not claimed
     */
    public boolean isChunkClaimed(String chunkID){
        return this.claimByChunkID.containsKey(chunkID);
    }

    /**
     * Get claim at chunk
     *
     * @param chunkID ID of chunk
     * @return Optional of Claim object
     */
    public Optional<Claim> getChunkClaim(String chunkID){
        return Optional.ofNullable(this.claimByChunkID.getOrDefault(chunkID, null));
    }

    /**
     * Make a claim for the given chunk
     *
     * @param chunkSnapshot snapshot of chunk that claim is in
     * @param townUUID UUID of town
     * @return Claim object
     */
    public Claim makeChunkClaim(ChunkSnapshot chunkSnapshot, UUID townUUID){
        String chunkID = ChunkUtils.getChunkID(chunkSnapshot);
        if(this.claimByChunkID.containsKey(chunkID)){
            throw new IllegalArgumentException("Cannot make a claim in a chunk with a claim");
        }
        if(!this.townsByUUID.containsKey(townUUID)){
            throw new IllegalArgumentException("Cannot make a claim for a town that doesn't exist");
        }
        Town town = this.townsByUUID.get(townUUID);
        Claim claim = new Claim(chunkID, town, chunkSnapshot, chunkSnapshot.getWorldName(),chunkSnapshot.getX(), chunkSnapshot.getZ());
        town.addClaim(claim);
        this.claimByChunkID.put(chunkID, claim);
        addClaimMarker(claim);
        return claim;
    }

    /**
     * Remove a claim for the specified chunk
     *
     * @param chunkID ID of the chunk
     */
    public void removeClaim(String chunkID){
        if(!claimByChunkID.containsKey(chunkID)){
            throw new IllegalArgumentException("Cannot remove a claim that does not exist");
        }

        Claim claim = claimByChunkID.get(chunkID);
        Town town = claim.getTown();
        removeClaimMarker(claim);
        town.removeClaim(claim);
        claimByChunkID.remove(chunkID);
    }

    public void setHomeChunk(String chunkID, Location location){
        if(!claimByChunkID.containsKey(chunkID)){
            throw new IllegalArgumentException("Cannot remove a claim that does not exist");
        }

        Claim claim = claimByChunkID.get(chunkID);
        Town town = claim.getTown();

        for(Claim townClaims : town.getClaims()){
            if(townClaims.isHomeChunk()){
                townClaims.setHomeChunk(false);
                townClaims.setHomeLocation(null);
            }
        }
        claim.setHomeChunk(true);
        claim.setHomeLocation(location.toVector());

        for(UUID memberUUID : town.getMemberUUIDs()){
            Player member = plugin.getServer().getPlayer(memberUUID);
            if(member != null && member.isOnline()){
                member.sendMessage(String.format(Lang.CHANGED_HOME_CHUNK.toStringWithPrefix(), location.getBlockX(), location.getBlockY(),location.getBlockZ()));
            }
        }
        addClaimMarker(claim);

    }

    public Optional<Location> getHomeChunk(UUID townUUID){
        if(!townsByUUID.containsKey(townUUID)){
            throw new IllegalArgumentException("Cannot get home chunk of a town that does not exist");
        }

        Location homeLocation = null;
        for(Claim claim : townsByUUID.get(townUUID).getClaims()){
            if(claim.isHomeChunk()){
                World world = plugin.getServer().getWorld(ChunkUtils.getWorldFromID(claim.getChunkID()));
                homeLocation = claim.getHomeLocation().toLocation(world);
                break;
            }
        }
        return Optional.ofNullable(homeLocation);
    }

    /**
     * Checks if player has edit access in a town
     *
     * @param playerUUID UUID of player
     * @param townUUID UUID of town
     * @return true if player has edit access, false if no access
     */
    public boolean canPlayerEdit(UUID playerUUID, UUID townUUID){
        if(!townsByUUID.containsKey(townUUID)){
            throw new IllegalArgumentException("Town does not exist!");
        }
        return townsByUUID.get(townUUID).getMemberUUIDs().contains(playerUUID) || adminBypass.contains(playerUUID);
    }

    // Invitation

    /**
     * Get the TownInvite object for the given user
     *
     * @param invitee UUID of invitee
     * @return Optional of TownInvitee
     */
    public Optional<TownInvite> getTownInvite(UUID invitee){
        return Optional.ofNullable(inviteByInviteeUUID.get(invitee));
    }

    /**
     * Remove the invite from an invitee
     *
     * @param invitee UUID of invitee
     */
    public void removeTownInvite(UUID invitee){
        inviteByInviteeUUID.remove(invitee);
    }

    /**
     * Remove the invite from an invitee
     *
     * @param invitee UUID of invitee
     */
    public TownInvite makeInvite(Town town, UUID inviter, UUID invitee){
        TownInvite townInvite = new TownInvite(town, inviter, invitee);
        inviteByInviteeUUID.put(invitee, townInvite);
        return townInvite;
    }

    // Add claim marker (Dynmap)
    private void addClaimMarker(Claim claim){
        ChunkSnapshot chunkSnapshot = claim.getChunkSnapshot();
        String chunkID = claim.getChunkID();

        Pair<Vector, Vector> corners = ChunkUtils.getCorners(chunkSnapshot);

        Vector corner1 = corners.getKey();
        Vector corner2 = corners.getValue();

        String markerID = "ClaimMarker_"+chunkID;
        if(markerSet.findAreaMarker(markerID) != null){
            areaMarkerByClaim.put(claim, markerSet.findAreaMarker(markerID));
            markerSet.findAreaMarker(markerID).deleteMarker();
        }
        AreaMarker am = markerSet.createAreaMarker(markerID, "Claim:"+chunkID, false, chunkSnapshot.getWorldName(), new double[4], new double[4], false);
        double[] d1 = {corner1.getX(), corner2.getX()+1};
        double[] d2 = {corner1.getZ(), corner2.getZ()+1};
        am.setCornerLocations(d1, d2);
        am.setLabel(claim.getTown().getName());

        String desc = "";

        desc+="Claimed by: <b>"+claim.getTown().getName()+"</b>";

        am.setDescription(desc);

        if(claim.isHomeChunk()){
            if(homeMarkerByClaim.containsKey(claim.getTown())){
                homeMarkerByClaim.get(claim.getTown()).deleteMarker();
            }
            Vector home = claim.getHomeLocation();
            Marker homeMarker =  markerSet.createMarker("HomeMarker_"+claim.getTown().getName(),claim.getTown().getName()+" [Home]",chunkSnapshot.getWorldName(),home.getX(),64,home.getZ(),homeIcon, false);
            homeMarkerByClaim.put(claim.getTown(), homeMarker);
        }

        areaMarkerByClaim.put(claim, am);
    }

    // Remove claim marker (Dynmap)
    private void removeClaimMarker(Claim claim){
        if(homeMarkerByClaim.containsKey(claim.getTown()) && claim.isHomeChunk()){
            homeMarkerByClaim.get(claim.getTown()).deleteMarker();
            homeMarkerByClaim.remove(claim.getTown());
        }
        areaMarkerByClaim.get(claim).deleteMarker();
        areaMarkerByClaim.remove(claim);
    }


}