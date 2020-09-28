package ninja.jrc.bloodyaffairs.listeners;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.managers.TownManager;
import ninja.jrc.bloodyaffairs.objects.Claim;
import ninja.jrc.bloodyaffairs.objects.ClaimProperty;
import ninja.jrc.bloodyaffairs.utils.ChunkUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Fire;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

import java.util.Optional;

public class BlockSpreadListener implements Listener {
    private final BloodyAffairs plugin;
    private final TownManager townManager;

    public BlockSpreadListener(BloodyAffairs plugin, TownManager townManager){
        this.plugin = plugin;
        this.townManager = townManager;
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event){
        Block block = event.getBlock();
        String chunkID = ChunkUtils.getChunkID(block.getLocation().getChunk().getChunkSnapshot());

        if(block.getBlockData().getMaterial().equals(Material.FIRE)){
            Optional<Claim> optionalClaim = townManager.getChunkClaim(chunkID);
            if(optionalClaim.isPresent()){
                Claim claim = optionalClaim.get();
                if(!claim.getClaimProperties().contains(ClaimProperty.FIRE)){
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

}
