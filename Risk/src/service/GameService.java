package service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import controller.GameController;
import controller.MapController;
import domain.Continent;
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

	private Map<Integer, List<Player>> randomPlayerTurnHelper = new LinkedHashMap<>();

	/**
	 * This method is used to allocate armies to different players randomly.
	 * 
	 * @param numberOfPlayers:
	 *            It has all the player object.
	 */
	public void distributeTerritories(List<Player> numberOfPlayers) {
		List<Territory> territoryObjectList = new ArrayList<>(MapController.territoriesSet);
		// variable to hold territory to work with.
		Territory tempTerritory;
		while (territoryObjectList.size() != 0) {
			for (int i = 0; i < numberOfPlayers.size() && territoryObjectList.size() != 0; i++) {
				Player onePlayer = numberOfPlayers.get(i);

				int randIndex = randomIndex(0, territoryObjectList.size() - 1);
				// avoid getting it List again and again.
				tempTerritory = territoryObjectList.get(randIndex);
				onePlayer.getTerritories().add(tempTerritory);
				onePlayer.updateArmyCount(-1);
				tempTerritory.setOwner(onePlayer);
				tempTerritory.setArmyOfTheTerritory(1);
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

	/**
	 * This method set the number of armies to be given to a player at the start of
	 * reinforcement phase.
	 * 
	 * @param playerInFocus
	 *            : Player who is currently in reinforcement phase
	 */
	public void addArmiesForReinforcementPhase(Player playerInFocus) {

		List<Territory> playerTerritories = playerInFocus.getTerritories();
		int numberOfTerritories = playerInFocus.getTerritories().size();
		int numberOfArmiesToAdd = 0;
		Iterator<Continent> contIterator = MapController.continentsSet.iterator();
		Continent contObject;
		while (contIterator.hasNext()) {
			contObject = contIterator.next();
			List<Territory> territoriesInContinent = contObject.getTerritories();
			if (ifContinentOccupied(territoriesInContinent, playerTerritories)) {
				numberOfArmiesToAdd += contObject.getContinentArmyValue();
			}
		}

		if (numberOfTerritories < 9) {
			numberOfArmiesToAdd += 3;
			playerInFocus.updateArmyCount(numberOfArmiesToAdd);
		} else {
			numberOfArmiesToAdd += (int) Math.floor(numberOfTerritories / 3);
			playerInFocus.updateArmyCount(numberOfArmiesToAdd);
		}
	}

	/**
	 * This method checks if the players owns all the territories of a continent or
	 * not.
	 * 
	 * @param territoriesInContinent
	 *            : List of territories present in a continent.
	 * @param playerTerritories
	 *            : List of territories player has currently.
	 * @return
	 */
	public boolean ifContinentOccupied(List<Territory> territoriesInContinent, List<Territory> playerTerritories) {
		boolean ifOccupied = true;
		for (int i = 0; i < playerTerritories.size(); i++) {
			if (!territoriesInContinent.equals(playerTerritories.get(i))) {
				ifOccupied = false;
				break;
			}
		}
		return ifOccupied;
	}

	/**
	 * This method set the territories and armies to all the players.
	 * 
	 * @param playerList
	 *            : List of players who are playing the game.
	 * @param totalNumberOfPlayers:
	 *            total number of players to check how many armies should be
	 *            assigned at the start of the game.
	 */
	public void setTerritoriesAndArmiesToPlayers(List<Player> playerList, int totalNumberOfPlayers) {
		int armyCount = getArmyCount(totalNumberOfPlayers);
		for (int i = 0; i < totalNumberOfPlayers; i++) {
			Player playerObj = new Player();
			playerObj.setName("Player " + (i + 1));
			playerObj.setArmyCount(armyCount);
			// already added this in constructor.
			// playerObj.setTerritories(new ArrayList<Territory>());
			playerList.add(playerObj);
		}
		distributeTerritories(playerList);

	}

	/**
	 * This method is used to get the number of armies according to the number of
	 * players
	 * 
	 * @param playerCount:
	 *            Number of player playing the game
	 * @return
	 */
	private int getArmyCount(int playerCount) {

		switch (playerCount) {

		case 2:
			return 40;
		case 3:
			return 35;
		case 4:
			return 30;
		case 5:
			return 25;
		case 6:
			return 20;
		default:
			return 15;

		}
	}

	/**
	 * This function validates if the player in focus has valid number of armies as
	 * in if a player has 0 armies on his turn he won't be able to place any army
	 * 
	 * @param playerInFocus:
	 *            Player whose turn it is to place army on his territories
	 * @return
	 */
	public boolean validatePlayerArmyNumber(Player playerInFocus) {

		if (playerInFocus.getArmyCount() == 0) {
			return false;
		}

		return true;
	}

	/**
	 * This method gives the player who will have the next turn to play. Initially,
	 * turn is decided randomly and then based on the previous player next player is
	 * fetched form the palyerList.
	 * 
	 * @param prevPlayer
	 *            : player who has completed his turn
	 * @param playerList
	 *            : List of players in the game.
	 * @return
	 */
	public Player getPlayer(Player prevPlayer, List<Player> playerList) {

		int diceNumber;
		Player nextPlayer = null;
		if (prevPlayer == null) {
			for (int i = 0; i < playerList.size(); i++) {
				List<Player> playerListForTurn = new ArrayList<>();
				diceNumber = randomIndex(1, 6);
				if (randomPlayerTurnHelper.get(diceNumber) != null) {
					randomPlayerTurnHelper.get(diceNumber).add(playerList.get(i));
				} else {
					playerListForTurn.add(playerList.get(i));
					randomPlayerTurnHelper.put(diceNumber, playerListForTurn);
				}
			}
			int counter = 6;

			while (counter != 0) {
				if (randomPlayerTurnHelper.get(counter) != null) {
					nextPlayer = randomPlayerTurnHelper.get(counter)
							.get(randomPlayerTurnHelper.get(counter).size() - 1);
				}
				counter--;
			}
			return nextPlayer;

		} else {
			int indexOfPreviousPlayer = playerList.indexOf(prevPlayer);
			if (indexOfPreviousPlayer == playerList.size() - 1) {
				nextPlayer = playerList.get(0);
				return nextPlayer;
			} else {
				nextPlayer = playerList.get(indexOfPreviousPlayer + 1);
				return nextPlayer;
			}
		}

	}

	/**
	 * This method validates if the input number of armies are valid or not.
	 * 
	 * @param inputText
	 *            : Text entered by the user.
	 * @param playerInFocus
	 *            : Player who has entered the input.
	 * @param territoryInFocus:
	 *            territory where the player is trying to add armies.
	 * @throws Exception
	 */
	public void validateArmyInput(String inputText, Player playerInFocus, Territory territoryInFocus) throws Exception {
		String error;
		int numberOfArmiesInput;
		try {
			numberOfArmiesInput = Integer.parseInt(inputText);
			if (numberOfArmiesInput > playerInFocus.getArmyCount()) {
				error = "Number of armies cannot be more than what owner owns";
				throw new Exception(error);

			} else if (numberOfArmiesInput < 1) {
				error = "Number of Armies cannot be less than 1";
				throw new Exception(error);

			}

		} catch (NumberFormatException exception) {
			error = "Please, Enter a valid number";
			throw new Exception(error);

		}
		if (territoryInFocus == null) {
			error = "Please select at least one territory";
			throw new Exception(error);
		}
	}

	public List<Territory> attackableTerritories(Territory territory) {

		Player player = territory.getOwner();
		List<Territory> defenderTerritories = new ArrayList<>(territory.getNeighbourTerritories());
		for (Territory ter : player.getTerritories()) {
			if (defenderTerritories.contains(ter))
				defenderTerritories.remove(ter);
		}
		return defenderTerritories;
	}

	public void attack(Territory attackerTerritory, Territory defenderTerritory) {
		Player attacker = attackerTerritory.getOwner();
		Player defender = defenderTerritory.getOwner();

		defender.getTerritories().remove(defenderTerritory);
		defenderTerritory.setOwner(null);

		attackerTerritory.setArmyOfTheTerritory(attackerTerritory.getArmyOfTheTerritory() - 1);
		defenderTerritory.setArmyOfTheTerritory(1);

		attacker.getTerritories().add(defenderTerritory);
		defenderTerritory.setOwner(attacker);
	}

	/**
	 * This method is used to do fortification in which armies are moved from one
	 * territory to another territory.
	 * 
	 * @param from:
	 *            Territory from which armies to be moved.
	 * @param to:
	 *            Territory to which armies to be moved.
	 * @param armiesToMove:
	 *            Number of armies to move.
	 * @param errorList:
	 *            List to hold validation errors.
	 */
	public void fortification(Territory from, Territory to, int armiesToMove, List<String> errorList) {
		if (from.getArmyOfTheTerritory() <= 1) {
			errorList.add("Don't have sufficient armies to move.");
			return;
		} else if (from.getArmyOfTheTerritory() <= armiesToMove) {
			errorList.add("Can only move upto " + String.valueOf(from.getArmyOfTheTerritory() - 1) + " armies.");
			return;
		} else if (from == to) {
			errorList.add("Can't move from same territory to same territory.");
			return;
		} else {
			from.setArmyOfTheTerritory(from.getArmyOfTheTerritory() - armiesToMove);
			to.setArmyOfTheTerritory(to.getArmyOfTheTerritory() + armiesToMove);
		}
	}

	public List<Territory> fortifiableTerritories(Territory territory) {
		Player player = territory.getOwner();
		List<Territory> fortifiableTerritories = new ArrayList<>();
		for (Territory terr : territory.getNeighbourTerritories()) {
			if (player.getTerritories().contains(terr))
				fortifiableTerritories.add(terr);
		}
		return fortifiableTerritories;
	}
}
