package ninja.jrc.bloodyaffairs.objects;

import java.util.UUID;

public class BAPlayer{
    private final UUID playerUUID;
    private int reputation;

    public BAPlayer(UUID playerUUID){
        this.playerUUID = playerUUID;
        this.reputation = 0;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void addReputation(int toAdd){
        this.reputation += toAdd;
    }

    public int getReputation() {
        return reputation;
    }
}
