package ninja.jrc.bloodyaffairs.objects;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Nation {
    private UUID UUID;
    private String name;
    private final UUID leaderUUID;
    private Set<UUID> townUUIDs;

    public Nation(String nationName, UUID leaderUUID, UUID capitalTown){
        this.UUID = UUID.randomUUID();
        this.name = nationName;

        this.leaderUUID = leaderUUID;

        this.townUUIDs = new HashSet<UUID>();
        this.townUUIDs.add(capitalTown);
    }

    public java.util.UUID getUUID() {
        return UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UUID> getTownUUIDs() {
        return townUUIDs;
    }

    public void addTown(UUID townUUID){
        townUUIDs.add(townUUID);
    }

    public void removeTown(UUID townUUID){
        townUUIDs.remove(townUUID);
    }

    public Boolean containsTown(UUID townUUID){
        return townUUIDs.contains(townUUID);
    }

}
