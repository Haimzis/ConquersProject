package Server.Utils;

import java.util.ArrayList;
import java.util.List;

public class RoomsManager {
    private List<RoomDescriptor> activeRooms = new ArrayList<>();

    public void addNewRoom(RoomDescriptor newRoom) {
        activeRooms.add(newRoom);
    }

    public List<RoomDescriptor> getActiveRooms() {
        return activeRooms;
    }

    public void removeRoom(int id) {
        activeRooms.remove(id-1);
    }

    public RoomDescriptor getRoom(int id) {
        return activeRooms.get(id-1);
    }
}
