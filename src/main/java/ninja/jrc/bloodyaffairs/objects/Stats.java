package ninja.jrc.bloodyaffairs.objects;

public class Stats {
    private int tension;

    public Stats(){ }

    public int getTension() {
        return tension;
    }

    public void setTension(int tension) {
        this.tension = tension;
    }

    public void addTension(int toAdd){
        if(this.tension >= 250){
            return;
        }else if((this.tension + toAdd) >= 250){
            this.tension = 250;
        }else{
            this.tension += toAdd;
        }
    }


}
