package ninja.jrc.bloodyaffairs.objects;

import org.bukkit.ChunkSnapshot;
import org.bukkit.util.Vector;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


public class Claim {
    private final String chunkID;
    private final Long creationDate;
    // Chunk Location
    private final String world;
    private final int chunkX;
    private final int chunkZ;

    private transient Town town;
    private transient ChunkSnapshot chunkSnapshot;

    // Claim "properties"
    // Nation(If in nation)
    private transient Nation nation;

    // Is it the home chunk?
    private boolean isHomeChunk;
    private Vector homeLocation;

    private final Set<ClaimProperty> claimProperties = new HashSet<>();

    public Claim(String chunkUUID, Town town, ChunkSnapshot chunkSnapshot, String world, int chunkX, int chunkZ){
        this.town = town;
        this.chunkID = chunkUUID;
        this.creationDate = Instant.now().getEpochSecond();
        this.chunkSnapshot = chunkSnapshot;
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public String getChunkID() {
        return chunkID;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public String getWorld() {
        return world;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    // Chunksnapshot

    public ChunkSnapshot getChunkSnapshot() {
        return chunkSnapshot;
    }

    public void setChunkSnapshot(ChunkSnapshot chunkSnapshot) {
        this.chunkSnapshot = chunkSnapshot;
    }

    // Nation

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    // Town

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    ////////////
    // FIELDS //
    ////////////

    // isHomeChunk
    public void setHomeChunk(boolean homeChunk) {
        isHomeChunk = homeChunk;
    }

    public boolean isHomeChunk() {
        return isHomeChunk;
    }

    public void setHomeLocation(Vector homeLocation) {
        this.homeLocation = homeLocation;
    }

    public Vector getHomeLocation() {
        return homeLocation;
    }

    //  Claim properties

    public Set<ClaimProperty> getClaimProperties() {
        return claimProperties;
    }
}
