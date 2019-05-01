package GameObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private int ID;
    private String player_name;
    private int funds;
    private List<Integer> TerritoriesID;

    public Player(int id, String player_name, int funds) {
        ID = id;
        this.player_name = player_name;
        this.funds = funds;
        TerritoriesID = new ArrayList<>();
    }
    public Player(Player player) {
        ID = player.getID();
        this.player_name =player.getPlayer_name();
        this.funds = player.getFunds();
        this.TerritoriesID = new ArrayList<>();
        if (!player.getTerritoriesID().isEmpty())
            player.getTerritoriesID().forEach(territoryID -> this.TerritoriesID.add(new Integer(territoryID)));
    }

    //**************************//
    /*    Getters & Setters     */
    //**************************//
    public List<Integer> getTerritoriesID() {
        return TerritoriesID;
    }
    public int getID() {
        return ID;
    }
    public String getPlayer_name() {
        return player_name;
    }
    public int getFunds() {
        return funds;
    }

    //**************************//
    /*          Methods         */
    //**************************//
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Player)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Player c = (Player) o;

        // Compare the data members and return accordingly
        return this.ID == c.getID();
    }
    //increment Funds to player
    public void incrementFunds(int fundsAmount)
    {
        this.funds += fundsAmount;
    }
    //decrement Funds to player
    public void decrementFunds(int fundsAmount)
    {
        this.funds -= fundsAmount;
    }
    public void addTerritory(Territory territory)
    {
            TerritoriesID.add(territory.getID());
    }
}
