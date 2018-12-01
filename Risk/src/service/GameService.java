package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import controller.GameController;
import controller.MapController;
import domain.AggressiveStrategy;
import domain.BenevolentStrategy;
import domain.Card;
import domain.CardExchangeViewModel;
import domain.Continent;
import domain.GameObjectClass;
import domain.HumanStrategy;
import domain.PhaseViewModel;
import domain.Player;
import domain.PlayerStrategyEnum;
import domain.Territory;
import javafx.application.Platform;
import javafx.util.Pair;

/**
 * This class handle all the service call from GameController class and provide
 * business logic for same.
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
	 * @param territoriesSet:
	 *            set of territories.
	 */
	public void assignTerritories(List<Player> numberOfPlayers, Set<Territory> territoriesSet) {
		List<Territory> territoryObjectList = new ArrayList<>(territoriesSet);

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
	 * @param playerStrategy:
	 *            Strategy with which the current player is playing
	 * 
	 * @param model:
	 *            instance of CardExchangeViewModel
	 */
	public void calcArmiesForReinforcement(Player playerInFocus, PlayerStrategyEnum playerStrategy,
			CardExchangeViewModel model) {

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

		if (!playerStrategy.equals(PlayerStrategyEnum.HUMAN)) {

			Territory cardAndOwnedTerritory = null;
			Queue<Card> playerCards = model.getCurrentPlayerCards(playerInFocus);
			List<Card> inputCard = new ArrayList<>();

			List<Card> infantAmryCardList = new ArrayList<>();
			List<Card> cavAmryCardList = new ArrayList<>();
			List<Card> artAmryCardList = new ArrayList<>();

			Iterator<Card> ite = playerCards.iterator();
			while (ite.hasNext()) {
				Card currCard = ite.next();
				if (currCard.getCardType().equalsIgnoreCase(CardExchangeViewModel.INFANTRY_ARMY)) {
					infantAmryCardList.add(currCard);
				} else if (currCard.getCardType().equalsIgnoreCase(CardExchangeViewModel.CAVALRY_ARMY)) {
					cavAmryCardList.add(currCard);
				} else {
					artAmryCardList.add(currCard);
				}
			}

			if (infantAmryCardList.size() == 3) {
				cardAndOwnedTerritory = removeCardsFromPlayer(model, playerInFocus, infantAmryCardList);
				model.setTotalNumberOfExchanges(model.getTotalNumberOfExchanges() + 1);
				if (cardAndOwnedTerritory != null) {
					model.setPlayerArmyCount(playerInFocus, 0, cardAndOwnedTerritory);
					cardAndOwnedTerritory.setArmyCount(2);
				} else {
					model.setPlayerArmyCount(playerInFocus, 0, null);
				}
			} else if (cavAmryCardList.size() == 3) {
				cardAndOwnedTerritory = removeCardsFromPlayer(model, playerInFocus, cavAmryCardList);
				model.setTotalNumberOfExchanges(model.getTotalNumberOfExchanges() + 1);
				if (cardAndOwnedTerritory != null) {
					model.setPlayerArmyCount(playerInFocus, 0, cardAndOwnedTerritory);
					cardAndOwnedTerritory.setArmyCount(2);
				} else {
					model.setPlayerArmyCount(playerInFocus, 0, null);
				}
			} else if (artAmryCardList.size() == 3) {
				cardAndOwnedTerritory = removeCardsFromPlayer(model, playerInFocus, artAmryCardList);
				model.setTotalNumberOfExchanges(model.getTotalNumberOfExchanges() + 1);
				if (cardAndOwnedTerritory != null) {
					model.setPlayerArmyCount(playerInFocus, 0, cardAndOwnedTerritory);
					cardAndOwnedTerritory.setArmyCount(2);
				} else {
					model.setPlayerArmyCount(playerInFocus, 0, null);
				}
			} else if (infantAmryCardList.size() >= 1 && cavAmryCardList.size() >= 1 && artAmryCardList.size() >= 1) {
				inputCard.add(infantAmryCardList.get(0));
				inputCard.add(cavAmryCardList.get(0));
				inputCard.add(artAmryCardList.get(0));
				cardAndOwnedTerritory = removeCardsFromPlayer(model, playerInFocus, inputCard);
				model.setTotalNumberOfExchanges(model.getTotalNumberOfExchanges() + 1);
				if (cardAndOwnedTerritory != null) {
					model.setPlayerArmyCount(playerInFocus, 0, cardAndOwnedTerritory);
					cardAndOwnedTerritory.setArmyCount(2);
				} else {
					model.setPlayerArmyCount(playerInFocus, 0, null);
				}
			}

		}
	}

	private Territory removeCardsFromPlayer(CardExchangeViewModel model, Player currentPlayer,
			List<Card> selectedCards) {

		Territory cardAndOwnedTerritory = null;
		Queue<Card> removedCards = new LinkedList<>();
		Iterator<Card> ite = model.getCurrentPlayerCards(currentPlayer).iterator();
		List<Card> inputCard = selectedCards;
		// Iterate over the cards that player is having
		while (ite.hasNext()) {
			Card playerCard = ite.next();
			if (inputCard.contains(playerCard)) {
				ite.remove();
				removedCards.add(playerCard);
				if (currentPlayer.getTerritories().contains(playerCard.getCardTerritory())) {
					cardAndOwnedTerritory = playerCard.getCardTerritory();
				}
			}
		}
		// add the exchanged cards to the initial deck of cards again.
		model.getAllCards().addAll(removedCards);
		return cardAndOwnedTerritory;
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
	 *//*
		 * public void createPlayers(List<Player> playerList, int totalNumberOfPlayers)
		 * { int armyCount = getArmyCount(totalNumberOfPlayers); // iterate till the
		 * total Number of players and create that many player objects. for (int i = 0;
		 * i < totalNumberOfPlayers; i++) { Player playerObj = new Player();
		 * playerObj.setName("Player " + (i + 1)); playerObj.setArmyCount(armyCount);
		 * playerList.add(playerObj); } // stubbing players
		 * playerList.get(0).setPlayingStrategy(new HumanStrategy());
		 * playerList.get(1).setPlayingStrategy(new AggressiveStrategy());
		 * //playerList.get(2).setPlayingStrategy(new AggressiveStrategy()); }
		 */

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
		// At the start of the game prevPlayer will be null and player who gets the
		// maximum number on dice roll will have the first turn
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
			// if it's last player of the list the next player will be the first one in the
			// list
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
			// army input cannot be greater than the total army the player has.
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
	 * @param attacker:
	 *            Player who is attacking.
	 * @param defender:
	 *            Player whose territory is attacked.
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
	 * @return A Boolean and Integer representing attack outcome and minimum troops
	 *         to move.
	 */
	public Pair<Boolean, Integer> attack(Player attacker, Player defender, Territory attackerTerritory,
			Territory defenderTerritory, boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice,
			PhaseViewModel phaseViewModel) {

		return attacker.attack(defender, attackerTerritory, defenderTerritory, isAllOutMode, totalAttackerDice,
				totalDefenderDice, phaseViewModel);

	}

	/**
	 * This method is used to validate the parameters passed for fortification and
	 * populate errorList accordingly
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
	public void validatefortifcationParameters(Territory from, Territory to, int armiesToMove, List<String> errorList) {
		if (from.getArmyCount() <= 1) {
			errorList.add("Don't have sufficient armies to move.");
			return;
		} else if (from.getArmyCount() <= armiesToMove) {
			errorList.add("Can only move upto " + String.valueOf(from.getArmyCount() - 1) + " armies.");
			return;
		} else if (from == to) {
			errorList.add("Can't move from same territory to same territory.");
			return;
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
	 * @param phaseViewModel:
	 *            Reference to PhaseviewModel
	 * @param player:
	 *            Reference to a player object
	 */
	public void fortify(Player player, Territory from, Territory to, int armiesToMove, PhaseViewModel phaseViewModel) {
		player.fortify(from, to, armiesToMove, phaseViewModel);
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
	/*
	 * private int getArmyCount(int playerCount) { switch (playerCount) { case 2:
	 * return 40; case 3: return 35; case 4: return 30; case 5: return 25; case 6:
	 * return 20; default: return 15; } }
	 */

	/**
	 * This method current player's reinforcement method to add reinforcement armies
	 * to selected territory.
	 * 
	 * @param selectedTerritory:
	 *            Territory to which armies is to be added for reinforcement.
	 * @param numberOfArmies:
	 *            number of armies to add to a territory as reinforcement.
	 * @param phaseViewModel:
	 *            Reference to PhaseviewModel
	 * @param player:
	 *            Reference to a player object
	 */
	public void addReinforcement(Player player, Territory selectedTerritory, int numberOfArmies,
			PhaseViewModel phaseViewModel) {

		player.reinforcement(selectedTerritory, numberOfArmies, phaseViewModel);

	}

	/**
	 * This method check if currentPlayer can attack any further or not by checking
	 * if current player have any territory with army greater than 1 and that
	 * territory neighbor as a territory owned by some other player.
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
	 * @param playersWithZeroArmies:
	 *            set of players that have zero armies.
	 * @param playersList:
	 *            List of players
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
	 * @param cardExchangeViewModel:
	 *            Reference to CardExchangeViewModel.
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

	/**
	 * Setter for playerstrategyEnumMap sets player to corresponding strategy in a
	 * Map
	 * 
	 * @param playerList:
	 *            list of players playing game
	 * @param playerStrategyMapping
	 *            : playerStrategy Mapping map
	 */
	public void setPlayerStartegyEnumMap(List<Player> playerList,
			Map<Player, PlayerStrategyEnum> playerStrategyMapping) {
		for (int i = 0; i < playerList.size(); i++) {
			Player curPlayer = playerList.get(i);
			if (curPlayer.getPlayingStrategy() instanceof AggressiveStrategy) {
				playerStrategyMapping.put(curPlayer, PlayerStrategyEnum.AGGRESSIVE);
			} else if (curPlayer.getPlayingStrategy() instanceof HumanStrategy) {
				playerStrategyMapping.put(curPlayer, PlayerStrategyEnum.HUMAN);
			} else if (curPlayer.getPlayingStrategy() instanceof BenevolentStrategy) {
				playerStrategyMapping.put(curPlayer, PlayerStrategyEnum.BENEVOLENT);
			} else if (curPlayer.getPlayingStrategy() instanceof Random) {
				playerStrategyMapping.put(curPlayer, PlayerStrategyEnum.RANDOM);
			} else {
				playerStrategyMapping.put(curPlayer, PlayerStrategyEnum.CHEATER);
			}
		}
	}

	/**
	 * This is startUp phase logic for non human player where a random territory is
	 * selected and all the armies is player to it.
	 * 
	 * @param currentPlayer:
	 *            Player instance for which armies to be placed.
	 * @param armyCount:
	 *            Total number of armies to be placed.
	 * @param phaseViewModel:
	 *            instance of phaseViewModel.
	 */
	public void nonHumanStartUpPhase(Player currentPlayer, int armyCount, PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo("StartUp Phase: placing armies on radom territory.");
		phaseViewModel.setCurrentPlayer(currentPlayer.getName());
		Territory selectedTerritory = currentPlayer.getTerritories()
				.get(randomIndex(0, currentPlayer.getTerritories().size() - 1));
		selectedTerritory.setArmyCount(selectedTerritory.getArmyCount() + armyCount);
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nMoved " + armyCount
				+ " armies to randomly selected territory(" + selectedTerritory.getName() + ").");
		currentPlayer.setArmyCount(0);

	}

	/**
	 * This method serializes the game state to a file
	 * 
	 * @param fileToSave:
	 *            file in which game state is saved
	 * @param continentSet:
	 *            Set of Continents at that game state
	 * @param territorySet:
	 *            Set of Territories at that game state
	 * @param playerList:
	 *            List of players playing the game at that game state
	 * @param currentPlayer:
	 *            Player playing at that instance of game state
	 * @param currentPhase:
	 *            Phase at that game state
	 * @param ifStartUpIsComepleted:
	 *            boolean parameter true if start up phase is completed else false
	 * @param playerStrategyMapping:
	 *            mapping of players to their corresponding strategies.
	 * @param errorList:
	 *            List of string to hold error.
	 * 
	 * @return true if game state is serialized
	 */
	public boolean serialize(File fileToSave, HashSet<Continent> continentSet, HashSet<Territory> territorySet,
			List<Player> playerList, Player currentPlayer, String currentPhase, boolean ifStartUpIsComepleted,
			Map<Player, PlayerStrategyEnum> playerStrategyMapping, List<String> errorList) {

		FileOutputStream fileOutput;
		ObjectOutputStream out = null;

		try {
			fileOutput = new FileOutputStream(fileToSave);
			out = new ObjectOutputStream(fileOutput);
			GameObjectClass gameState = new GameObjectClass(continentSet, territorySet, playerList, currentPlayer,
					currentPhase, ifStartUpIsComepleted);

			out.writeObject(gameState);
			out.close();
			fileToSave = null;
			Platform.exit();
		} catch (Exception e) {
			String error = "Game Cannot be saved";
			errorList.add(error);
			return false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				fileToSave = null;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}