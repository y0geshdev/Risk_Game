package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import controller.GameController;
import controller.MapController;
import domain.Continent;
import domain.PhaseViewModel;
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
	 * This method is used to allocate territories to different players randomly.
	 * 
	 * @param numberOfPlayers:
	 *            It has all the player object.
	 */
	public void assignTerritories(List<Player> numberOfPlayers) {
		List<Territory> territoryObjectList = new ArrayList<>(MapController.territoriesSet);

		Territory tempTerritory;
		while (territoryObjectList.size() != 0) {
			for (int i = 0; i < numberOfPlayers.size() && territoryObjectList.size() != 0; i++) {
				Player onePlayer = numberOfPlayers.get(i);

				int randIndex = randomIndex(0, territoryObjectList.size() - 1);
				tempTerritory = territoryObjectList.get(randIndex);
				onePlayer.getTerritories().add(tempTerritory);
				tempTerritory.setOwner(onePlayer);
				tempTerritory.setArmyCount(1);
				onePlayer.setArmyCount(onePlayer.getArmyCount() - 1);
				territoryObjectList.remove(randIndex);
			}
		}

	}

	/**
	 * This method calculate the number of armies which needs to be given to a
	 * player for reinforcement phase.
	 * 
	 * @param playerInFocus:
	 *            Player who is currently in reinforcement phase
	 */
	public void calcArmiesForReinforcement(Player playerInFocus) {

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
			playerInFocus.setArmyCount(playerInFocus.getArmyCount() + numberOfArmiesToAdd);
		} else {
			numberOfArmiesToAdd += (int) Math.floor(numberOfTerritories / 3);
			playerInFocus.setArmyCount(playerInFocus.getArmyCount() + numberOfArmiesToAdd);
		}
	}

	/**
	 * This method checks if the players owns all the territories of a continent or
	 * not.
	 * 
	 * @param territoriesInContinent:
	 *            List of territories present in a continent.
	 * @param playerTerritories:
	 *            List of territories player has currently.
	 * @return boolean: true if player owns all the territory of
	 *         territoriesInContinent else false.
	 */
	public boolean ifContinentOccupied(List<Territory> territoriesInContinent, List<Territory> playerTerritories) {
		boolean ifOccupied = true;
		for (int i = 0; i < territoriesInContinent.size(); i++) {
			if (!playerTerritories.contains(territoriesInContinent.get(i))) {
				ifOccupied = false;
				break;
			}
		}
		return ifOccupied;
	}

	/**
	 * This method created player instances and adds them to playerList
	 * 
	 * @param playerList:
	 *            List of players who are playing the game.
	 * @param totalNumberOfPlayers:
	 *            total number of players to check how many armies should be
	 *            assigned at the start of the game.
	 */
	public void createPlayers(List<Player> playerList, int totalNumberOfPlayers) {
		int armyCount = getArmyCount(totalNumberOfPlayers);
		for (int i = 0; i < totalNumberOfPlayers; i++) {
			Player playerObj = new Player();
			playerObj.setName("Player " + (i + 1));
			playerObj.setArmyCount(armyCount);
			playerList.add(playerObj);
		}
	}

	/**
	 * This method gives the player who will have the next turn to play. Initially,
	 * turn is decided randomly and then based on the previous player next player is
	 * fetched form the palyerList.
	 * 
	 * @param prevPlayer:
	 *            player who has completed his turn
	 * @param playerList:
	 *            List of players in the game.
	 * @return Player: player who will be having next turn.
	 */
	public Player getNextPlayer(Player prevPlayer, List<Player> playerList) {

		int diceNumber;
		Player nextPlayer = null;
		Integer maxNumber = Integer.MIN_VALUE;

		if (prevPlayer == null) {
			for (int i = 0; i < playerList.size(); i++) {
				diceNumber = randomIndex(1, 6);
				if (maxNumber <= diceNumber) {
					maxNumber = diceNumber;
					nextPlayer = playerList.get(i);
				}
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
	 * @param errorList
	 *            : Error list containing errors related the text user input
	 */
	public void validateArmyInput(String inputText, Player playerInFocus, Territory territoryInFocus,
			List<String> errorList) {
		String error;
		int numberOfArmiesInput;
		try {
			numberOfArmiesInput = Integer.parseInt(inputText);
			if (numberOfArmiesInput > playerInFocus.getArmyCount()) {
				error = "Number of armies cannot be more than what owner owns";
				errorList.add(error);

			} else if (numberOfArmiesInput < 1) {
				error = "Number of Armies cannot be less than 1";
				errorList.add(error);
			}

		} catch (NumberFormatException exception) {
			error = "Please, Enter a valid number";
			errorList.add(error);

		}
		if (territoryInFocus == null) {
			error = "Please select at least one territory";
			errorList.add(error);
		}
	}

	/**
	 * This method will return a list of territories on which given territories can
	 * attack.
	 * 
	 * @param territory:
	 *            territory for which attackable territories needs to be found.
	 * @return List: a list of all the territories on which given territory can
	 *         attack.
	 */
	public List<Territory> getAttackableTerritories(Territory territory) {

		Player player = territory.getOwner();
		List<Territory> defenderTerritories = new ArrayList<>(territory.getNeighbourTerritories());
		for (Territory ter : player.getTerritories()) {
			if (defenderTerritories.contains(ter))
				defenderTerritories.remove(ter);
		}
		return defenderTerritories;
	}

	/**
	 * This method perform attack from attacker to defender territory.
	 * @param phaseViewModel 
	 * 
	 * @param attackerTerritory:
	 *            attacking territory object.
	 * @param defenderTerritory:
	 *            defending territory object.
	 */
	public boolean attack(Territory attackerTerritory, Territory defenderTerritory, PhaseViewModel phaseViewModel) {
		//boolean isWon;
		Player attacker = attackerTerritory.getOwner();
		Player defender = defenderTerritory.getOwner();
		
		return attacker.attack(attackerTerritory,defenderTerritory,defender, phaseViewModel);
		
		/*defender.getTerritories().remove(defenderTerritory);
		defenderTerritory.setArmyCount(1);
		defenderTerritory.setOwner(attacker);
		attackerTerritory.setArmyCount(attackerTerritory.getArmyCount() - 1);
		attacker.getTerritories().add(defenderTerritory);*/
		/*
		 * If the player wins at least one territory during his attack phase he is
		 * entitled to get One card else keep the possibility of drawing the card to
		 * false
		 */
		//return true;
	}

	/*
	 * This function can be used to call the attack after user has entered the input
	 */
	public boolean attack(Territory attackerTerritory, Territory defenderTerritory, int attackerDiceNumber,
			int defenderDiceNumber) {
		List<Integer> attackerNumberList = new ArrayList<>();
		List<Integer> defenderNumberList = new ArrayList<>();

		while (attackerDiceNumber != 0) {
			int rand = randomIndex(0, 6);
			attackerNumberList.add(rand);
			attackerDiceNumber--;
		}

		while (defenderDiceNumber != 0) {
			int rand = randomIndex(0, 6);
			defenderNumberList.add(rand);
			defenderDiceNumber--;
		}

		Collections.sort(attackerNumberList, Collections.reverseOrder());
		Collections.sort(defenderNumberList, Collections.reverseOrder());

		List<Integer> chances = chancesWonByAttacker(attackerNumberList, defenderNumberList);

		int win = chances.get(0);
		int loss = chances.get(1);

		attackerTerritory.setArmyCount(attackerTerritory.getArmyCount() - loss);
		defenderTerritory.setArmyCount(defenderTerritory.getArmyCount() - win);

		if (defenderTerritory.getArmyCount() == 0) {
			Player attacker = attackerTerritory.getOwner();
			Player defender = defenderTerritory.getOwner();

			defender.getTerritories().remove(defenderTerritory);
			defenderTerritory.setOwner(attacker);
			attacker.getTerritories().add(defenderTerritory);
			/*
			 * If the player wins at least one territory during his attack phase he is
			 * entitled to get One card else keep the possibility of drawing the card to
			 * false
			 */

		}
		return true;
	}

	/*
	 * This method is used to decide how many chances have been won by the attacker
	 * by comparing the list of numbers turned up on the dice for both defender and
	 * attacker
	 */
	public List<Integer> chancesWonByAttacker(List<Integer> attackerNumberList, List<Integer> defenderNumberList) {

		int win = 0, loss = 0;
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < attackerNumberList.size(); i++) {
			int attackerNumber = attackerNumberList.get(i);
			int defenderNumber = -1;
			if (i < defenderNumberList.size()) {
				defenderNumber = defenderNumberList.get(i);
				if (attackerNumber > defenderNumber) {
					win++;
				} else {
					loss++;
				}
			}
		}
		result.add(win);
		result.add(loss);
		return result;
	}

	/*
	 * This method is used to get the valid number of dices that defender and
	 * attacker can use for defending and attacking respectively.
	 * 
	 * @param playerTerritory
	 * 
	 * @param playerType
	 * 
	 * @return
	 */
	public int getNumberOfDiceToRoll(Territory playerTerritory, String playerType) {
		int armyCountOnTerritory = playerTerritory.getArmyCount();

		if (playerType.equalsIgnoreCase("Attacker")) {
			if (armyCountOnTerritory > 3) {
				return 3;
			} else if (armyCountOnTerritory == 3) {
				return 2;
			} else if (armyCountOnTerritory == 2) {
				return 1;
			} else {
				return -1;
			}

		} else {
			if (armyCountOnTerritory >= 2) {
				return 2;
			} else {
				return 1;
			}
		}
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
	public void fortify(Territory from, Territory to, int armiesToMove, List<String> errorList) {
		if (from.getArmyCount() <= 1) {
			errorList.add("Don't have sufficient armies to move.");
			return;
		} else if (from.getArmyCount() <= armiesToMove) {
			errorList.add("Can only move upto " + String.valueOf(from.getArmyCount() - 1) + " armies.");
			return;
		} else if (from == to) {
			errorList.add("Can't move from same territory to same territory.");
			return;
		} else {
			Player player = from.getOwner();
			player.fortify(from, to, armiesToMove);
		}
	}

	/**
	 * This method will return a list of territories can be fortified by a given
	 * territory.
	 * 
	 * @param territory:
	 *            territory for which fortifiable territories needs to be found.
	 * @return List: a list of all the territories on which given territory can
	 *         fortify.
	 */
	public List<Territory> getFortifiableTerritories(Territory territory) {
		Player player = territory.getOwner();
		List<Territory> fortifiableTerritories = new ArrayList<>();

		Queue<Territory> queue = new LinkedList<>();
		queue.add(territory);
		Territory t;
		while (queue.size() > 0) {
			t = queue.poll();
			for (Territory neighbours : t.getNeighbourTerritories()) {
				if (neighbours.getOwner() == player && !fortifiableTerritories.contains(neighbours)) {
					fortifiableTerritories.add(neighbours);
					queue.add(neighbours);
				}
			}
		}
		if (fortifiableTerritories.contains(territory))
			fortifiableTerritories.remove(territory);
		return fortifiableTerritories;
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
	private int randomIndex(int min, int max) {
		Random randIndex = new Random();
		return randIndex.nextInt((max - min) + 1) + min;
	}

	/**
	 * This method is used to get the number of armies according to the number of
	 * players
	 * 
	 * @param playerCount:
	 *            Number of player playing the game
	 * @return int number of armies per player according to total number of players
	 *         playing game.
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
	 * This method current player's reinforcement method to add reinforcement armies to selected territory.
	 * @param selectedTerritory:
	 * 							Territory to which armies is to be added for reinforcement.
	 * @param numberOfArmies:
	 * 						number of armies to add to a territory as reinforcement.
	 */
	public void addReinforcement(Territory selectedTerritory, int numberOfArmies) {
		
		Player player = selectedTerritory.getOwner();
		player.reinforcement(selectedTerritory,numberOfArmies);
		
	}

}
