package ninja.jrc.bloodyaffairs.objects;

public enum ClaimProperty {
    FIRE("♨"),
    EXPLOSION("☀"),
    OPEN("Ⓞ"),
    MOB("Ⓜ");

    String symbol;

    ClaimProperty(String symbol){
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
