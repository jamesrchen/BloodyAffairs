package ninja.jrc.bloodyaffairs.managers;

import ninja.jrc.bloodyaffairs.objects.BAPlayer;

import java.util.*;

public class BAPlayerManager {
    private final Map<UUID, BAPlayer> BAPlayerMap = new HashMap<>();

    public BAPlayerManager(){ }

    public Collection<BAPlayer> getBAPlayers(){
        return BAPlayerMap.values();
    }

    public void setAllBAPlayers(Set<BAPlayer> BAPlayerSet){
        for(BAPlayer baPlayer : BAPlayerSet){
            BAPlayerMap.put(baPlayer.getPlayerUUID(), baPlayer);
        }
    }

    public Map<UUID, BAPlayer> getBAPlayerMap() {
        return BAPlayerMap;
    }

    public boolean isRegistered(UUID playerUUID){
        return BAPlayerMap.containsKey(playerUUID);
    }

    public void addReputation(UUID playerUUID, int toAdd){
        if(BAPlayerMap.containsKey(playerUUID)){
            BAPlayer baPlayer = BAPlayerMap.get(playerUUID);
            baPlayer.addReputation(toAdd);
        }else{
            BAPlayer baPlayer = new BAPlayer(playerUUID);
            baPlayer.addReputation(toAdd);
            BAPlayerMap.put(playerUUID, baPlayer);
        }
    }

    public int getReputation(UUID playerUUID){
        if(!BAPlayerMap.containsKey(playerUUID)){
            return 0;
        }
        return BAPlayerMap.get(playerUUID).getReputation();
    }



}
