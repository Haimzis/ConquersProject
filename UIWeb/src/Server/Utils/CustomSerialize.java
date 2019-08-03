package Server.Utils;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CustomSerialize implements JsonSerializer<TerritoryActionMessage> {
    @Override
    public JsonElement serialize(TerritoryActionMessage territoryActionMessage, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        Gson gson = new Gson();
        JsonElement defArmy = gson.toJsonTree(territoryActionMessage.getDefendingArmy());
        JsonElement attackArmy = gson.toJsonTree(territoryActionMessage.getDefendingArmy());
        object.add("defendingArmy", defArmy);
        object.add("conqueringArmy", attackArmy);
        object.addProperty("targetTerritoryId", territoryActionMessage.getTargetTerritoryId());
        object.addProperty("draw", territoryActionMessage.isDraw());
        object.addProperty("success", territoryActionMessage.isSuccess());
        object.addProperty("conquerorName", territoryActionMessage.getConquerorName());
        return object;
    }

}
