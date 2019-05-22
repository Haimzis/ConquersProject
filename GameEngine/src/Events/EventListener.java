package Events;

import java.util.ArrayList;
import java.util.List;

public class EventListener {
    private List<EventObject> eventObjectsList = new ArrayList<>();
    //default eventHandler
    private EventHandler eventsHandler= eventObject -> {
        //do nothing
    };
    public void setEventsHandler(EventHandler eventsHandler){
        this.eventsHandler = eventsHandler;
    }
    public void addEventObject(EventObject eventObject){
        eventObjectsList.add(eventObject);
    }
    public void activateEventsHandler(){
        for(EventObject eventObject: eventObjectsList){
            eventsHandler.handle(eventObject);
        }
        //make empty
        eventObjectsList = new ArrayList<>();
    }
}
