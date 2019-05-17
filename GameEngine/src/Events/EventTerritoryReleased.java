package Events;

public class EventTerritoryReleased implements EventObject{
    private Integer identity;
    private String message="";

    @Override
    public Integer getIdentity() {
        return identity;
    }
    public String getMessage() {
        return message;
    }

    public EventTerritoryReleased(Integer identity,String message){
        this.identity = identity;
        this.message = message;
    }
    public EventTerritoryReleased(Integer identity){
        this.identity = identity;
    }

}
