package Events;

public class PlayerEvent extends EventObject {
    public PlayerEvent(int identity, String action) {
        super(action);
        this.identity = identity;
    }
}
