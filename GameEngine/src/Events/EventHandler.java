package Events;

import java.io.Serializable;

public interface EventHandler extends Serializable {
     void handle(EventObject eventObject);
}
