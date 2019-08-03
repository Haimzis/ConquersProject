package Server.Utils;

import java.util.ArrayList;
import java.util.List;

public class RoomsContainer {
    private List<RoomManager> activeRooms = new ArrayList<>();

    public void addNewRoom(RoomManager newRoom) {
        activeRooms.add(newRoom);
    }

    public List<RoomManager> getActiveRooms() {
        return activeRooms;
    }
    public RoomManager getRoom(int id) {
        return activeRooms.get(id-1);
    }
    public RoomManager getRoomByUserName(String userName) {
        List<RoomManager> result = new ArrayList<>();
        activeRooms.forEach(roomManager -> {
            if(roomManager.hasPlayerByName(userName)) {
                result.add(roomManager);
            }
        });
        if(!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

}
