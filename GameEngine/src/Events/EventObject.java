package Events;

public abstract class EventObject {
    protected String action;
    protected int identity;
    protected final long time;

    EventObject(String action){
        this.action = action;
        this.time = System.currentTimeMillis();
    }
    EventObject(){
        this.action = "none";
        this.identity=0;
        this.time = 0;
    }
    public int getIdentity() {
        return identity;
    }
    public String getAction(){
        return action;
    }
}
