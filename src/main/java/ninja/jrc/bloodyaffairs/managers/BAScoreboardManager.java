package ninja.jrc.bloodyaffairs.managers;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.objects.Claim;
import ninja.jrc.bloodyaffairs.objects.ClaimProperty;
import ninja.jrc.bloodyaffairs.objects.Town;
import ninja.jrc.bloodyaffairs.utils.ChunkUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class BAScoreboardManager {
    private final BloodyAffairs plugin;
    private final TownManager townManager;
    private final NationManager nationManager;
    private final StatsManager statsManager;
    private final BAPlayerManager baPlayerManager;

    private final ScoreboardManager scoreboardManager;

    public BAScoreboardManager(BloodyAffairs plugin, TownManager townManager, NationManager nationManager, StatsManager statsManager, BAPlayerManager baPlayerManager) {
        this.plugin = plugin;
        this.townManager = townManager;
        this.nationManager = nationManager;
        this.statsManager = statsManager;
        this.baPlayerManager = baPlayerManager;

        this.scoreboardManager = plugin.getServer().getScoreboardManager();
    }

    public void updateScoreboard(Player player){
        Scoreboard board = scoreboardManager.getNewScoreboard();
        Objective obj = board.registerNewObjective("BA", "dummy", "BloodyAffairs");
        obj.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"【BloodyAffairs】");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Optional<Town> optionalTown = townManager.findTownByMemberUUID(player.getUniqueId());

        String townName = ChatColor.GREEN+"-";
        String nationName = ChatColor.GREEN+"-";
        String claimName = ChatColor.GREEN+"Wilderness";
        String tensionCount = ChatColor.RED+"250";
        String reputationCount = ChatColor.RED+Integer.toString(baPlayerManager.getReputation(player.getUniqueId()));
        // ChatColor.YELLOW+"⚡ "+ChatColor.RED+"⚛ "+ChatColor.BLUE+ChatColor.BOLD+"☄ "+ChatColor.GREEN+"✧"
        String unlockedTech = ChatColor.YELLOW+"⚡ "+ChatColor.RED+"⚛ "+ChatColor.BLUE+ChatColor.BOLD+"☽ "+ChatColor.GREEN+"✧";

        if(optionalTown.isPresent()){
            Town town = optionalTown.get();
            townName = ChatColor.AQUA+town.getName();
        }

        Optional<Claim> optionalClaim = townManager.getChunkClaim(ChunkUtils.getChunkID(player.getLocation().getChunk().getChunkSnapshot()));
        if(optionalClaim.isPresent()){
            Claim claim = optionalClaim.get();
            claimName = ChatColor.AQUA+claim.getTown().getName();
            if(claim.isHomeChunk()){
                claimName += ChatColor.BOLD+" Ⓗ";
            }
            if(claim.getClaimProperties() != null){
                for(ClaimProperty claimProperty : claim.getClaimProperties()){
                    claimName += " "+claimProperty.getSymbol();
                }
            }
        }

        ArrayList<String> scoreboardScores = new ArrayList<>();

        scoreboardScores.add("");
        scoreboardScores.add("");
        scoreboardScores.add(ChatColor.YELLOW+"Reputation: "+reputationCount);
        scoreboardScores.add("");
        scoreboardScores.add(ChatColor.YELLOW+"Tension: "+tensionCount+ChatColor.RED+"/250");
        scoreboardScores.add(ChatColor.YELLOW+"Unlocked: "+unlockedTech);
        scoreboardScores.add("");
        scoreboardScores.add(ChatColor.DARK_RED+"Town: "+townName);
        scoreboardScores.add(ChatColor.DARK_RED+"Nation: "+nationName);
        scoreboardScores.add("");
        scoreboardScores.add(ChatColor.RED+"Claim: "+claimName);
        scoreboardScores.add("");
        scoreboardScores.add(ChatColor.DARK_GRAY+"Version: "+plugin.getDescription().getVersion()+"         ");

        Collections.reverse(scoreboardScores);

        int index = 0;
        int spaceIndex = 0;

        for(String scoreText : scoreboardScores) {
            if (scoreText.equals("")) {
                // TODO Understand what this does :O
                scoreText = new String(new char[spaceIndex]).replace("\0", " ");
                spaceIndex++;
            }
            obj.getScore(scoreText).setScore(index);
            index++;
        }

        Objective repObj =  board.registerNewObjective("BARep", "dummy", ChatColor.RED+"Rep");
        repObj.setDisplaySlot(DisplaySlot.BELOW_NAME);

        Collection<Player> onlinePlayers = (Collection<Player>) plugin.getServer().getOnlinePlayers();

        for(Player onlinePlayer : onlinePlayers) {
            repObj.getScore(onlinePlayer.getName()).setScore(baPlayerManager.getReputation(onlinePlayer.getUniqueId()));
        }

        player.setScoreboard(board);
    }

}
