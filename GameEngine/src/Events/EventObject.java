package Events;

public abstract class EventObject {
    protected String action;
    protected Object identity;
    protected final long time;

    EventObject(String action){
        this.action = action;
        this.time = System.currentTimeMillis();
    }
    public Object getIdentity() {
        return identity;
    }
    public String getAction(){
        return action;
    }
}
