package Events;

public class RoundEvent extends EventObject{
    public RoundEvent(String action) {
        super(action);
        this.identity ="EveryOne";
    }
}
