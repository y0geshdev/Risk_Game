package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * This entity class represent a Continent in game.
 * @author Yogesh
 */
public class Continent {

	private String name;
    private List<Territory> territories;
    private int continentArmyValue;

    public Continent() {
        this.territories = new ArrayList<>();
    }

    public Continent(String name) {
        this.name = name;
        this.territories = new ArrayList<>();
    }

    public Continent(String name, int continentArmyValue) {
        this.name = name;
        this.continentArmyValue = continentArmyValue;
        this.territories = new ArrayList<>();
    }

    public int getContinentArmyValue() {
        return continentArmyValue;
    }

    public void setContinentArmyValue(int continentArmyValue) {
        this.continentArmyValue = continentArmyValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Territory> getTerritories() {
        return territories;
    }

    public void setTerritories(List<Territory> territories) {
        this.territories = territories;
    }
}
