package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controller.GameController;
import controller.MapController;
import domain.Player;
import domain.Territory;

/**
 * This class handle all the service call from {@link GameController} class and
 * provide business logic for same.
 * 
 * @author Yogesh
 *
 */
public class GameService {

	/**
	 * This method is used to allocate armies to different players randomly.
	 * 
	 * @param numberOfPlayers:
	 *            It has all the player object.
	 */
	public void distributeTerritories(List<Player> numberOfPlayers) {
		List<Territory> territoryObjectList = new ArrayList<>(MapController.territoriesSet);

		while (territoryObjectList.size() != 0) {
			for (int i = 0; i < numberOfPlayers.size() && territoryObjectList.size() != 0; i++) {
				Player onePlayer = numberOfPlayers.get(i);

				int randIndex = randomIndex(0, territoryObjectList.size() - 1);
				onePlayer.getTerritories().add(territoryObjectList.get(randIndex));
				territoryObjectList.get(randIndex).setOwner(onePlayer);
				territoryObjectList.remove(randIndex);
			}
		}

	}

	/**
	 * This method is used to get a random number which will be between 0 and size
	 * of list of territories.
	 * 
	 * @param min
	 *            : lower range of the random number generated
	 * @param max
	 *            : upper range of the random number generated.
	 * 
	 * @return : returns the random number generated between the range.
	 * 
	 */
	public int randomIndex(int min, int max) {
		Random randIndex = new Random();
		return randIndex.nextInt((max - min) + 1) + min;
	}

}
