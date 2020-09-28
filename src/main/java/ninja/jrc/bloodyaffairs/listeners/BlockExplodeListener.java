package ninja.jrc.bloodyaffairs.listeners;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.managers.TownManager;
import ninja.jrc.bloodyaffairs.objects.Claim;
import ninja.jrc.bloodyaffairs.objects.ClaimProperty;
import ninja.jrc.bloodyaffairs.utils.ChunkUtils;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class BlockExplodeListener implements Listener {
    private final BloodyAffairs plugin;
    private final Logger logger;
    private final TownManager townManager;

    public BlockExplodeListener(BloodyAffairs plugin, TownManager townManager){
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.townManager = townManager;
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event){
        List<Block> blockList = event.blockList();
        Iterator<Block> blockIterator = blockList.iterator();
        while(blockIterator.hasNext()){
            Block block = blockIterator.next();
            String chunkID = ChunkUtils.getChunkID(block.getChunk().getChunkSnapshot());
            Optional<Claim> optionalClaim = townManager.getChunkClaim(chunkID);
            if(optionalClaim.isPresent()){
                Claim claim = optionalClaim.get();
                if(!claim.getClaimProperties().contains(ClaimProperty.EXPLOSION)){
                    blockIterator.remove();
                }
            }
        }
    }
}
