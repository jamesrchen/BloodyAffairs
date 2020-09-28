package ninja.jrc.bloodyaffairs.utils;


import ninja.jrc.bloodyaffairs.objects.Pair;
import org.bukkit.ChunkSnapshot;
import org.bukkit.util.Vector;

public class ChunkUtils {

    // This is a util class with pure functions, no need for instantiating.
    private ChunkUtils(){}

    /**
     * Gets the id of the chunk.
     *
     * @param chunkSnapshot ChunkSnapshot of chunk to get ID of
     * @return the id of the chunk as a String, follows format "[WORLD NAME][XCoords][ZCoords]"
     */
    public static String getChunkID(ChunkSnapshot chunkSnapshot){
        String chunkID = "";
        chunkID += chunkSnapshot.getWorldName();
        chunkID += ":";
        chunkID += chunkSnapshot.getX();
        chunkID += ":";
        chunkID += chunkSnapshot.getZ();
        return chunkID;
    }

    public static String getWorldFromID(String chunkID){
        return chunkID.split(":")[0];
    }

    public static Pair<Vector, Vector> getCorners(ChunkSnapshot chunkSnapshot){
        int chunkX = chunkSnapshot.getX();
        int chunkZ = chunkSnapshot.getZ();
        int realX1 = chunkX*16;
        int realZ1 = chunkZ*16;
        double realX2 = realX1+15;
        double realZ2 = realZ1+15;

        Vector vector1 = new Vector().setX(realX1).setZ(realZ1);
        Vector vector2 = new Vector().setX(realX2).setZ(realZ2);

        return new Pair<>(vector1, vector2);
    }

    public static Vector getChunkCenter(int x, int z){
        int realX = x*16;
        int realZ = z*16;

        int centerX = realX < 0 ? realX+8 : realX-8;
        int centerZ = realZ < 0 ? realZ+8 : realX-8;

        return new Vector().setX(centerX).setZ(centerZ);
    }

}
