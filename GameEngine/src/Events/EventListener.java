package Events;

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

    public int getVersion(){
        return eventObjectsList.size();
    }

    public synchronized void addEventObject(EventObject eventObject){
        eventObjectsList.add(eventObject);
    }
    public synchronized List<EventObject> getEventObjectsList(int fromIndex){
        if (fromIndex < 0 || fromIndex >= eventObjectsList.size()) {
            fromIndex = 0;
        }
        return eventObjectsList.subList(fromIndex, eventObjectsList.size());
    }

    //engine handling
    public synchronized void activateEventsHandler(){
        for(EventObject eventObject: eventObjectsList){
            eventsHandler.handle(eventObject);
        }
        //make empty
        eventObjectsList = new ArrayList<>();
    }
    public synchronized void setEventsHandler(EventHandler eventsHandler){
        this.eventsHandler = eventsHandler;
    }
    public synchronized void resetEventListener() {
        eventObjectsList = new ArrayList<>();
    }
}
