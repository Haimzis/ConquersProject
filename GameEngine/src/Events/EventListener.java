package Events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventListener {
    private List<EventObject> eventObjectsList = new ArrayList<>();
    private EventHandler eventsHandler= new EventHandler(){ //default eventHandler

        @Override
        public void handle(EventObject eventObject) {
            //do nothing
        }
    };
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
        //make empty
        eventObjectsList = new ArrayList<>();
    }
}
