package ninja.jrc.bloodyaffairs.listeners;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.objects.events.NewClaimEvent;
import ninja.jrc.bloodyaffairs.objects.events.RemoveClaimEvent;
import ninja.jrc.bloodyaffairs.objects.events.TownCreateEvent;
import org.bukkit.event.Listener;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

public class DynmapListener implements Listener {
    private final BloodyAffairs plugin;
    private final DynmapAPI dynmapAPI;
    private final MarkerSet markerSet;
    private final MarkerIcon homeIcon;

    public DynmapListener(BloodyAffairs plugin, DynmapAPI dynmapAPI){
        this.plugin = plugin;
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

    public void OnClaimCreate(NewClaimEvent event){

    }

    public void OnRemoveClaim(RemoveClaimEvent event){

    }

    public void OnTownCreate(TownCreateEvent event){
        // TODO: Do some cleaning up
    }

}
