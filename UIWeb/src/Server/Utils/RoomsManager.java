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
        RoomDescriptor[] result = new RoomDescriptor[1];
        activeRooms.forEach(roomDescriptor -> {
            if(roomDescriptor.hasPlayerByName(userName)) {
                result[0] = roomDescriptor;
            }
        });
        return result[0];
    }
}
