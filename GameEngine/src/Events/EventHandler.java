package Events;

import java.io.Serializable;

public interface EventHandler {
     void handle(EventObject eventObject);
}
