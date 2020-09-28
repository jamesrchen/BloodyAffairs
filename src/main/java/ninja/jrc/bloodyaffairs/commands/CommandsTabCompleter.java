package ninja.jrc.bloodyaffairs.commands;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.managers.NationManager;
import ninja.jrc.bloodyaffairs.managers.TownManager;
import ninja.jrc.bloodyaffairs.objects.ClaimProperty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandsTabCompleter implements TabCompleter {
    private final BloodyAffairs plugin;
    private final TownManager townManager;
    private final NationManager nationManager;


    public CommandsTabCompleter(BloodyAffairs plugin, TownManager townManager, NationManager nationManager) {
        this.plugin = plugin;
        this.townManager = townManager;
        this.nationManager = nationManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "town": {
                if (args.length < 2) {
                    return Arrays.asList("create", "disband", "invite", "kick", "leave", "claim", "unclaim", "list", "promote", "demote");
                }
            }
            case "claim": {
                if (args.length < 2) {
                    return Arrays.asList("toggle");
                }

                if (args[0].equalsIgnoreCase("toggle")) {
                    List<String> suggestions = new ArrayList<>();
                    for (ClaimProperty claimProperty : ClaimProperty.values()) {
                        suggestions.add(claimProperty.toString());
                    }
                    return suggestions;
                }
            }
            default: return null;
        }
    }
}
