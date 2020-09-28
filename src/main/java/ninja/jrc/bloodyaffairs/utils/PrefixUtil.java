package ninja.jrc.bloodyaffairs.utils;

import net.md_5.bungee.api.ChatColor;
import ninja.jrc.bloodyaffairs.objects.BAPlayer;
import ninja.jrc.bloodyaffairs.objects.Town;

public class PrefixUtil {

    // This is a util class with pure functions, no need for instantiating.
    private PrefixUtil() {}

    public static String getTownPrefix(Town town){
        return ChatColor.translateAlternateColorCodes('&', "&r[&6"+town.getName()+"&r]");
    }

    public static String getRepPrefix(int rep){
        return ChatColor.translateAlternateColorCodes('&', "&r"+rep+" &cRep&r");
    }

}
