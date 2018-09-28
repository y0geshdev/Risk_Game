package domain;

import java.util.ArrayList;
import java.util.List;
/**
 * This entity class represent a Territory in game.
 * @author Yogesh
 *
 */
public class Territory {

    private String name;
    private List<Territory> neighbourTerritories;
    private String continent;
    private Player owner;

    public Territory() {
        this.neighbourTerritories = new ArrayList<>();
    }

    public Territory(String name, String continent) {
        this.name = name;
        this.continent = continent;
        this.neighbourTerritories = new ArrayList<>();
    }

    public Territory(String name){
        this.name = name;
        this.neighbourTerritories = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Territory> getNeighbourTerritories() {
        return neighbourTerritories;
    }

    public void setNeighbourTerritories(List<Territory> neighbourTerritories) {
        this.neighbourTerritories = neighbourTerritories;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

	@Override
	public String toString() {
		return this.name;
	}
    
    
}
