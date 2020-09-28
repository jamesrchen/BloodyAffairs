package ninja.jrc.bloodyaffairs.objects;

public enum CustomItem {
    DENSE_STAR("Dense Star"),
    ULTRA_DENSE_STAR("Ultra Dense Star"),
    REINFORCED_ELYTRA("Reinforced Elytra"),
    CPU("CPU"),
    CIRCUIT_BOARD("Circuit Board");

    String name;

    CustomItem(String name){
        this.name = name;
    }

}
