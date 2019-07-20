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
    public RoomDescriptor getRoom(int id) {
        return activeRooms.get(id-1);
    }
    public RoomDescriptor getRoomByUserName(String userName) {
        List<RoomDescriptor> result = new ArrayList<>();
        activeRooms.forEach(roomDescriptor -> {
            if(roomDescriptor.hasPlayerByName(userName)) {
                result.add(roomDescriptor);
            }
        });
        return result.get(0);
    }
}
