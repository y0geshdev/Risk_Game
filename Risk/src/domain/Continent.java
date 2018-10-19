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
    
    /**
     * default constructor for continent
     */
    public Continent() {
        this.territories = new ArrayList<>();
    }

    /**
     * Creates a new continent with a given name
     * 
     * @param name:
     * 				Sets the given name to the continent's name
     */
    public Continent(String name) {
        this.name = name;
        this.territories = new ArrayList<>();
    }
/**
 * Creates a continent with given name and continentArmyValue
 * 
 * @param name: 
 * 				Name for the new continent
 *
 * @param continentArmyValue: 
 * 				Continent value for this continent
 */
    public Continent(String name, int continentArmyValue) {
        this.name = name;
        this.continentArmyValue = continentArmyValue;
        this.territories = new ArrayList<>();
    }

    /**
     * Gets the control value of the continent
     * 
     * @return this continent's continentArmyValue which is continent value
     */
    public int getContinentArmyValue() {
        return continentArmyValue;
    }
    /**
     * Changes the ContinentArmyValue for this continent
     * 
     * @param continentArmyValue:
     * 							This is the new ContinentArmyValue for this continent
     */
    public void setContinentArmyValue(int continentArmyValue) {
        this.continentArmyValue = continentArmyValue;
    }
    
    /**
     * Gets the name of this continent
     * 
     * @return this continent's name
     */
    public String getName() {
        return name;
    }
    /**
     * Changes the name for this continent
     * 
     * @param name:
     * 			   This is the new name for this continent
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Gets the list of territories which belongs to this continent
     * @return List of territories that this continent contains
     */
    public List<Territory> getTerritories() {
        return territories;
    }
    /**
     * Sets the territories for this continent
     * @param territories:
     * 					List of territories that this continent contains
     * 					
     * 					
     */
    public void setTerritories(List<Territory> territories) {
        this.territories = territories;
    }

	@Override
	public String toString() {
		return this.name;
	}
    
    
}
