package ninja.jrc.bloodyaffairs.listeners;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.managers.TownManager;
import ninja.jrc.bloodyaffairs.objects.Claim;
import ninja.jrc.bloodyaffairs.objects.ClaimProperty;
import ninja.jrc.bloodyaffairs.objects.Town;
import ninja.jrc.bloodyaffairs.utils.ChunkUtils;
import org.bukkit.ChunkSnapshot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Optional;
import java.util.logging.Logger;

public class BlockBreakListener implements Listener {
    private final BloodyAffairs plugin;
    private final Logger logger;
    private final TownManager townManager;

    public BlockBreakListener(BloodyAffairs plugin, TownManager townManager){
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.townManager = townManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        ChunkSnapshot chunkSnapshot = event.getBlock().getChunk().getChunkSnapshot();
        String chunkID = ChunkUtils.getChunkID(chunkSnapshot);
        Optional<Claim> optionalClaim = townManager.getChunkClaim(chunkID);
        if(!optionalClaim.isPresent()){
            return;
        }
        Claim claim = optionalClaim.get();
        if(townManager.canPlayerEdit(event.getPlayer().getUniqueId(), claim.getTown().getUUID())){
            return;
        }else{
            if(claim.getClaimProperties().contains(ClaimProperty.OPEN)){
                return;
            }else{
                event.setCancelled(true);
            }
        }
    }

}
