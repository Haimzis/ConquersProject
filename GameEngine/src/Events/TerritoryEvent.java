package Events;

public class TerritoryEvent extends EventObject {
    public TerritoryEvent(int identity,String action) {
        super(action);
        this.identity = identity;
    }
}
