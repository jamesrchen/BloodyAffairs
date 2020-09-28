package ninja.jrc.bloodyaffairs.objects;

import java.time.Instant;
import java.util.UUID;

public class TownInvite {
    private final UUID inviter;
    private final Town town;
    private final Long creationDate;

    public TownInvite(Town town, UUID inviter, UUID invitee){
        this.town = town;
        this.inviter = inviter;
        this.creationDate = Instant.now().getEpochSecond();
    }

    public UUID getInviter() {
        return inviter;
    }

    public Town getTown() {
        return town;
    }
}
