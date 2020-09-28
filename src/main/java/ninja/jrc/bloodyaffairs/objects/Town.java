package ninja.jrc.bloodyaffairs.objects;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Town {
    private UUID UUID;
    private String name;

    private UUID leaderUUID;
    private final Set<UUID> assistantUUIDs = new HashSet<>();
    private final Set<UUID> memberUUIDs = new HashSet<>();

    private UUID nationUUID;

    private Set<Claim> claims = new HashSet<>();

    private int money;


    public Town(String name, UUID leaderUUID){
        this.UUID = java.util.UUID.randomUUID();
        this.name = name;
        this.leaderUUID = leaderUUID;
        this.memberUUIDs.add(leaderUUID);
        this.nationUUID = null;
    }

    public java.util.UUID getUUID() {
        return UUID;
    }

    public String getName() {
        return name;
    }

    // User roles

    public java.util.UUID getLeaderUUID() {
        return leaderUUID;
    }

    public void setLeaderUUID(java.util.UUID leaderUUID) {
        this.leaderUUID = leaderUUID;
    }

    public Set<java.util.UUID> getAssistantUUIDs() {
        return assistantUUIDs;
    }

    public Set<java.util.UUID> getMemberUUIDs() {
        return memberUUIDs;
    }

    public void removeMember(UUID memberUUID){
        assistantUUIDs.remove(memberUUID);
        memberUUIDs.remove(memberUUID);
    }

    public void promoteMember(UUID memberUUID){
        assistantUUIDs.add(memberUUID);
    }

    public boolean demoteMember(UUID memberUUID){
        if(!memberUUIDs.contains(memberUUID) || !assistantUUIDs.contains(memberUUID)){
            return false;
        }
        assistantUUIDs.remove(memberUUID);
        return true;
    }

    // Nation

    public java.util.UUID getNationUUID() {
        return nationUUID;
    }

    // Claims

    public Set<Claim> getClaims() {
        return claims;
    }

    public void addClaim(Claim claim){
        claims.add(claim);
    }

    public void removeClaim(Claim claim){
        claims.remove(claim);
    }

    // Money

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

}
