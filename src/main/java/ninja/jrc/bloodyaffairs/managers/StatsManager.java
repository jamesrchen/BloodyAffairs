package ninja.jrc.bloodyaffairs.managers;

import ninja.jrc.bloodyaffairs.objects.BAPlayer;
import ninja.jrc.bloodyaffairs.objects.Stats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {
    private final Map<UUID, BAPlayer> BAPlayerMap = new HashMap<>();
    private Stats stats;

    public StatsManager(){

    }

    public Map<UUID, BAPlayer> getBAPlayerMap() {
        return BAPlayerMap;
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
}
