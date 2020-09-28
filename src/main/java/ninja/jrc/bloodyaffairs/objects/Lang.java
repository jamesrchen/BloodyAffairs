package ninja.jrc.bloodyaffairs.objects;

import org.bukkit.ChatColor;

public enum Lang {
    ERROR("&4There was an error!"),

    INSUFFICIENT_ARGUMENTS("&4Insufficient arguments provided!"),
    TOWN_ALREADY_EXISTS("&4The town with specified name already exists!"),

    ALREADY_TOWN_MEMBER("&4You are already part of a town!"),
    IS_TOWN_LEADER("&4You are the town leader!"),

    NOT_TOWN_MEMBER("&4You are not in a town!"),
    NOT_TOWN_LEADER("&4You are not the town leader!"),
    NOT_TOWN_ASSISTANT("&4You are not the town assistant or higher!"),

    CANNOT_CHANGE_PERMISSION("&4You cannot change their permission!"),

    PLAYER_ALREADY_ASSISTANT("&4The player is already an assistant or higher!"),
    PLAYER_NOT_ASSISTANT("&4The player is not an assistant!"),

    INSUFFICIENT_TOWN_PERMISSIONS("&4Insufficient town permissions!"),

    NO_SUCH_TOWN_MEMBER("&4There is no such town member!"),
    NOT_IN_PLAYER_TOWN("&4The player is not in your town!"),

    TOWN_CREATED("&2Town, %s&2 has been created by %s"),
    TOWN_DISBANDED("&c&2Town, %s&2 has been disbanded by %s"),

    NATION_CREATED("&2Nation, %s&2 has been created by %s"),

    LAND_ALREADY_CLAIMED("&4This chunk has already been claimed!"),
    LAND_NOT_CLAIMED("&4You have not claimed this chunk yet!"),
    LAND_CLAIMED_SUCCESS("&2Claimed successfully!"),
    LAND_UNCLAIMED_SUCCESS("&2Unclaimed successfully!"),

    TOWN_LIST("&4Towns"),

    NO_SUCH_PLAYER("&4No player with that name!"),

    INVITEE_ALREADY_TOWN_MEMBER("&4The invitee is already a member of a town!"),
    INVITE_SUCCESS("&2Successfully invited %s&2 to your town"),
    INVITED("&2You have been invited by %s&2 to %s&2,\nto accept do /t accept"),
    NO_INVITES("&4You have no pending invites!"),
    ACCEPTED_INVITE("&2Successfully accepted invite to %s"),
    INVITATION_ACCEPTED("&2%s&2 accepted your invite!"),

    LEFT_TOWN("&2You have successfully left %s"),

    KICKED_FROM_TOWN("&2You have been kicked from %s&2 by %s&2!"),

    NEW_TOWN_MEMBER("&2%s&2 has joined the town!"),
    TOWN_MEMBER_LEFT("&2%s&2 has left the town!"),

    TOWN_MEMBER_PROMOTED("&2%s&2 has been promoted in town!"),
    TOWN_MEMBER_DEMOTED("&2%s has been demoted in town!"),

    CHANGED_HOME_CHUNK("&2Home Chunk has been moved to X:%s Y:%s Z:%s"),
    NO_HOME_CHUNK("&4Your town has no home chunk!"),

    NOT_CLAIM_PROPERTY("&2%s &4is not a valid claim property"),
    ENABLE_CLAIM_PROPERTY("&4Claim property: &2%s &4has been enabled"),
    DISABLE_CLAIM_PROPERTY("&4Claim property: &2%s &4has been disabled"),

    NOT_ENOUGH_MONEY("&4You do not have enough money!");


    private final String string;

    public String toStringWithPrefix() {
        return ChatColor.translateAlternateColorCodes('&', "&3[&cBA&3] "+this.string)+ChatColor.RESET;
    }

    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', this.string)+ChatColor.RESET;
    }

    private Lang(String string) {
        this.string = string;
    }

}
