package service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import controller.GameController;
import controller.MapController;
import domain.CardExchangeViewModel;
import domain.Continent;
import domain.PhaseViewModel;
import domain.Player;
import domain.Territory;
import javafx.util.Pair;

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
		
		// iterate over players list and assign then random territory till all the
		// territories are assigned.
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

		// check if current player occupy whole continents to add continent control
		// values.
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
		//iterate till the total Number of players and create  that many player objects.
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
		// At the start of the game prevPlayer will be null and player who gets the maximum number on dice roll will have the first turn 
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
			//if it's last player of the list the next player will be the first one in the list
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
			//army input cannot be greater than the total army the player has.
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
	 * This method delegate attack from controller to player class.
	 * 
	 * @param attackerTerritory:
	 *            Territory from which attack is performed.
	 * @param defenderTerritory:
	 *            Territory to which attack is performed.
	 * @param isAllOutMode:
	 *            true if current attack is of All-Out mode else false.
	 * @param totalAttackerDice:
	 *            Number of dice to roll for attacker if current mode of attack is
	 *            normal mode.
	 * @param totalDefenderDice:
	 *            Number of dice to roll for defender if current mode of attack is
	 *            normal mode.
	 * @param phaseViewModel:
	 *            PhaseViewModel instance to update information on phase view with
	 *            each step of attack.
	 * @return A {@link Pair} class which hold data as Boolean and Integer
	 *         representing attack outcome and minimum troops to move.
	 */
	public Pair<Boolean, Integer> attack(Territory attackerTerritory, Territory defenderTerritory, boolean isAllOutMode,
			int totalAttackerDice, int totalDefenderDice, PhaseViewModel phaseViewModel) {
		Player attacker = attackerTerritory.getOwner();
		Player defender = defenderTerritory.getOwner();

		return attacker.attack(attackerTerritory, defenderTerritory, defender, isAllOutMode, totalAttackerDice,
				totalDefenderDice, phaseViewModel);

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

		// doing BFS to get all the territory which can be fortified by given territory.
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
	 * This method current player's reinforcement method to add reinforcement armies
	 * to selected territory.
	 * 
	 * @param selectedTerritory:
	 *            Territory to which armies is to be added for reinforcement.
	 * @param numberOfArmies:
	 *            number of armies to add to a territory as reinforcement.
	 */
	public void addReinforcement(Territory selectedTerritory, int numberOfArmies) {

		Player player = selectedTerritory.getOwner();
		player.reinforcement(selectedTerritory, numberOfArmies);

	}

	/**
	 * This method check if currentPlayer can attack any further or not.
	 * 
	 * @param currentPlayer:
	 *            Player to which further attacking is possible or not is checked.
	 * @return true if this currentPlayer can attack further else false.
	 */
	public boolean canPlayerAttackFurther(Player currentPlayer) {
		boolean canAttackFurther = false;

		outerFor: for (Territory territory : currentPlayer.getTerritories()) {
			if (territory.getArmyCount() > 1) {
				for (Territory neighbourTerritory : territory.getNeighbourTerritories()) {
					if (neighbourTerritory.getOwner() != currentPlayer) {
						canAttackFurther = true;
						break outerFor;
					}
				}
			}
		}

		return canAttackFurther;
	}

	/**
	 * This method defines the condition to verify if startUpPhase has ended or not.
	 * 
	 * @return boolean: true if startUp phase is finished else false.
	 */
	public boolean endOfStartUpPhase(Set<Player> playersWithZeroArmies, List<Player> playersList) {
		if (playersWithZeroArmies.size() == playersList.size()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method defines the condition to verify if the Reinforcement phase has
	 * ended or not.
	 * 
	 * @param playerInFocus
	 *            : Player who is doing the reinforcement currently.
	 * @return boolean: true if reinforcement phase is finished for current player
	 *         else false;
	 */
	public boolean endOfReinforcementPhase(Player playerInFocus, CardExchangeViewModel cardExchangeViewModel) {
		if (playerInFocus.getArmyCount() == 0) {
			cardExchangeViewModel.setCardAndOwnedTerritory(null);
			return true;

		} else {
			return false;
		}
	}

	/**
	 * This method checks if game is won by passed player or not as if player occupy
	 * all the territories then game is won by him.
	 * 
	 * @param player:
	 *            Player instance for which check is done.
	 * @param totalTerritoriesInMap:
	 *            Total number of territories.
	 * @return true if player own all the territories of map else false.
	 */
	public boolean isGameEnded(Player player, int totalTerritoriesInMap) {
		boolean isGameEnded = false;
		if (player.getTerritories().size() == totalTerritoriesInMap)
			isGameEnded = true;
		return isGameEnded;
	}

	/**
	 * This method is used to validate the number of dices user have entered for
	 * attacker and defender in normal mode.
	 * 
	 * @param attackerTerritory:
	 *            Attacking territory instance.
	 * @param defenderTerritory:
	 *            defender territory instance.
	 * @param attackerTotalDice:
	 *            Number of dice attacker will roll.
	 * @param defenderTotalDice:
	 *            Number of dice defender will roll.
	 * @param errorList:
	 *            List to hold validation errors.
	 */
	public void validateSelectedDiceNumber(Territory attackerTerritory, Territory defenderTerritory,
			String attackerTotalDice, String defenderTotalDice, List<String> errorList) {
		int totalAttackerDice, totalDefenderDice;
		try {
			totalAttackerDice = Integer.parseInt(attackerTotalDice);
			totalDefenderDice = Integer.parseInt(defenderTotalDice);
		} catch (NumberFormatException e) {
			errorList.add("Enter valid number of dice for attacker and defender.");
			return;
		}
		if (totalAttackerDice > 3 || totalAttackerDice < 1
				|| totalAttackerDice > attackerTerritory.getArmyCount() - 1) {
			errorList.add("Selected attacker can roll min 1 and max "
					+ (3 > attackerTerritory.getArmyCount() - 1 ? attackerTerritory.getArmyCount() - 1 : 3));
		}
		if (totalDefenderDice > 2 || totalDefenderDice < 1 || totalDefenderDice > defenderTerritory.getArmyCount()) {
			errorList.add("Selected defender can roll min 1 and max "
					+ (2 > defenderTerritory.getArmyCount() ? defenderTerritory.getArmyCount() : 2));
		}
	}

}