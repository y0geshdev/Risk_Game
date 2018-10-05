package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controller.MapController;
import domain.Player;
import domain.Territory;

/**
 * This class handle all the service call from {@link GameService} class and
 * provide business logic for same.
 * 
 * @author Yogesh
 *
 */
public class GameService {
	
	public void distributeTerritories(List<Player> numberOfPlayers) {
		List<Territory> territoryObjectList	=	new ArrayList<>(MapController.territoriesSet);
		
		while(territoryObjectList.size()!=0) {
			for(int i=0;i<numberOfPlayers.size();i++) {
				Player onePlayer	=	numberOfPlayers.get(i);
				int randIndex	=	randomIndex(0, territoryObjectList.size());
				onePlayer.getTerritories().add(territoryObjectList.get(randIndex));
				territoryObjectList.remove(randIndex);
			}
		}
		
	}
	
	public int randomIndex(int min, int max) {
		Random randIndex = new Random();
		return randIndex.nextInt((max - min) + 1) + min;
	}
}
