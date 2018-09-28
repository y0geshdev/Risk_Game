package domain;

import java.util.List;
/**
 * This entity class represent a Player in game.
 * @author Yogesh
 *
 */
public class Player {
    private String name;
    private List<Territory> territories;
    
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
	
	@Override
	public String toString() {
		return this.name;
	}
	
	

}
