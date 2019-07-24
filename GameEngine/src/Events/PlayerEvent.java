package Events;

public class PlayerEvent extends EventObject {
    public PlayerEvent(String identity, String action) {
        super(action);
        this.identity = identity;
    }
}
