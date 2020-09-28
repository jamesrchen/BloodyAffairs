package ninja.jrc.bloodyaffairs.managers;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.objects.Claim;
import org.dynmap.markers.MarkerSet;

public class DynmapManager {
    private final BloodyAffairs plugin;
    private final TownManager townManager;
    private final MarkerSet markerSet;

    public DynmapManager(BloodyAffairs plugin, TownManager townManager, MarkerSet markerSet){
        this.plugin = plugin;
        this.townManager = townManager;
        this.markerSet = markerSet;
    }

    public void UpdateClaim(Claim claim){

    }



}
