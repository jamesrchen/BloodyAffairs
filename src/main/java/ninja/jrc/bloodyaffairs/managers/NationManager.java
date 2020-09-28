package ninja.jrc.bloodyaffairs.managers;

import ninja.jrc.bloodyaffairs.objects.Nation;
import ninja.jrc.bloodyaffairs.objects.Town;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NationManager {
    private final Map<UUID, Nation> nationsByUUID = new HashMap<>();
    private final Map<String, Nation> nationsByName = new HashMap<>();

    public NationManager(){ }

    public Set<String> getAllNationNames(){
        return nationsByName.keySet();
    }

    public Nation findNationByName(String name){
        return nationsByName.getOrDefault(name, null);
    }

    // For persistent data manager to call
    public void storeData(Set<Nation> nationSet){
        for(Nation nation : nationSet){
            nationsByUUID.put(nation.getUUID(), nation);
            nationsByName.put(nation.getName(), nation);
        }
    }

//    public Nation createNation(String name, Town capital){
//
//        new Nation()
//    }
}
