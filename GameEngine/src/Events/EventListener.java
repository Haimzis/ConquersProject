package Events;

import java.util.ArrayList;
import java.util.List;

public class EventListener {
    private List<EventObject> eventObjectsList = new ArrayList<>();
    private EventHandler eventsHandler;
    public void setEventsHandler(EventHandler eventsHandler){
        this.eventsHandler = eventsHandler;
    }
    public List<EventObject> getEventObjectsList() {
        return eventObjectsList;
    }
    public void addEventObject(EventObject eventObject){
        eventObjectsList.add(eventObject);
    }
    public void activateEventsHandler(){
        for(EventObject eventObject: eventObjectsList){
            eventsHandler.handle(eventObject);
        }
    }
}
