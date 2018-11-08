package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.util.Pair;

/**
 * This entity class represent a Player in game.
 * 
 * @author Yogesh
 *
 */
public class Player {

	/**
	 * It represents player's name.
	 */
	private String name;

	/**
	 * It represents list of territories.
	 */
	private List<Territory> territories;

	/**
	 * Army count which this player holds to place on territories.
	 */
	private int armyCount;

	/**
	 * default constructor for Player
	 */
	public Player() {
		armyCount = 0;
		territories = new ArrayList<>();
	}

	/**
	 * Gets the name of this player
	 * 
	 * @return this player's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Changes the name for this player
	 * 
	 * @param name:
	 *            This is the new name for this player
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * gets the list of territories
	 * 
	 * @return the list of territories player owns
	 */
	public List<Territory> getTerritories() {
		return territories;
	}

	/**
	 * Changes the territories associated with this player
	 * 
	 * @param territories
	 *            This is the list of territories player owns
	 */
	public void setTerritories(List<Territory> territories) {
		this.territories = territories;
	}

	/**
	 * sets the army count for this player
	 * 
	 * @param armyCount:
	 *            Number of armies that this player owns
	 */
	public void setArmyCount(int armyCount) {
		this.armyCount = armyCount;
	}

	/**
	 * gets the number of armies that this player owns
	 * 
	 * @return the number of armies that player owns
	 */
	public int getArmyCount() {
		return armyCount;
	}

	/**
	 * Overridden toString() method.
	 */
	public String toString() {
		return this.name;
	}

	/**
	 * This method have logic to do reinforcement for current player.
	 * 
	 * @param selectedTerritory:
	 *            Territory to which armies is to be added for reinforcement.
	 * @param numberOfArmies:
	 *            number of armies to add to a territory as reinforcement.
	 */
	public void reinforcement(Territory selectedTerritory, int numberOfArmies) {
		selectedTerritory.setArmyCount(selectedTerritory.getArmyCount() + numberOfArmies);
		this.setArmyCount(this.getArmyCount() - numberOfArmies);
	}

	/**
	 * This method have the logic to fortify one territory from other territory.
	 * 
	 * @param from:
	 *            Territory from which armies to be moved.
	 * @param to:
	 *            Territory to which armies to be moved.
	 * @param armiesToMove:
	 *            Number of armies to move.
	 */
	public void fortify(Territory from, Territory to, int armiesToMove) {
		from.setArmyCount(from.getArmyCount() - armiesToMove);
		to.setArmyCount(to.getArmyCount() + armiesToMove);
	}

	/**
	 * This method perform attack from attacker to defender territory.
	 * 
	 * @param attackerTerritory:
	 *            Territory from which attack is performed.
	 * @param defenderTerritory:
	 *            Territory to which attack is performed.
	 * @param defender:
	 *            Owner of defenderTerritory.
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
	public Pair<Boolean, Integer> attack(Territory attackerTerritory, Territory defenderTerritory, Player defender,
			boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice, PhaseViewModel phaseViewModel) {
		boolean isWon = false;
		int remainingAttackingTroops = -1;
		List<Integer> attackerDiceRolls;
		List<Integer> defenderDiceRolls;

		// if the attack mode is normal mode.
		if (!isAllOutMode) {
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "dice rolling...");
			attackerDiceRolls = recordDiceRolls(totalAttackerDice, true);
			defenderDiceRolls = recordDiceRolls(totalDefenderDice, false);
			remainingAttackingTroops = attackerHelper(attackerTerritory, defenderTerritory, attackerDiceRolls,
					defenderDiceRolls, phaseViewModel);

			if (defenderTerritory.getArmyCount() == 0) {
				isWon = true;
			}

		}

		// if the attack is all-out mode attack.
		else {
			while (attackerTerritory.getArmyCount() > 1) {
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "dice rolling...");
				attackerDiceRolls = recordDiceRolls(attackerTerritory.getArmyCount() - 1, true);
				defenderDiceRolls = recordDiceRolls(defenderTerritory.getArmyCount(), false);
				remainingAttackingTroops = attackerHelper(attackerTerritory, defenderTerritory, attackerDiceRolls,
						defenderDiceRolls, phaseViewModel);

				if (defenderTerritory.getArmyCount() == 0) {
					isWon = true;
					break;
				}
			}
		}

		// if attacker territory in current attack.
		if (isWon) {
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "attacker won territory.");
			defender.getTerritories().remove(defenderTerritory);
			this.getTerritories().add(defenderTerritory);
			defenderTerritory.setOwner(this);
			return new Pair<>(isWon, remainingAttackingTroops);
		} else {
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "attacker didn't won territory.");
			return new Pair<Boolean, Integer>(isWon, null);
		}
	}

	/**
	 * Performs the attack based on dice roll outcome for attacker and defender.
	 * 
	 * @param attackerTerritory:
	 *            Territory who is attacking.
	 * @param defenderTerritory:
	 *            Territory who is defending.
	 * @param attackerDiceRolls:
	 *            List of dice roll outcome for attacker.
	 * @param defenderDiceRolls:
	 *            List of dice roll outcome for defender.
	 * @param phaseViewModel:
	 *            Instance of {@link PhaseViewModel} class to update information for
	 *            phaseView during attack.
	 * @return an integer representing as how many troops are survived from
	 *         attacking territory.
	 */
	public int attackerHelper(Territory attackerTerritory, Territory defenderTerritory, List<Integer> attackerDiceRolls,
			List<Integer> defenderDiceRolls, PhaseViewModel phaseViewModel) {
		int remainingAttackingTroops = attackerDiceRolls.size();

		// sort dice in descending order
		Collections.sort(attackerDiceRolls, Collections.reverseOrder());
		Collections.sort(defenderDiceRolls, Collections.reverseOrder());

		int iterations = attackerDiceRolls.size() > defenderDiceRolls.size() ? defenderDiceRolls.size()
				: attackerDiceRolls.size();
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "attacker dice "
				+ attackerDiceRolls.toString() + "\n" + "defender dice " + defenderDiceRolls.toString());

		for (int i = 0; i < iterations; i++) {
			// case where attacker won current dice roll.
			if (attackerDiceRolls.get(i) > defenderDiceRolls.get(i)) {
				phaseViewModel
						.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "attacker won in " + i + " die roll.");
				defenderTerritory.setArmyCount(defenderTerritory.getArmyCount() - 1);
			}
			// case where defender beats attacker current dice roll.
			else {
				phaseViewModel
						.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "defender won in " + i + " die roll.");
				attackerTerritory.setArmyCount(attackerTerritory.getArmyCount() - 1);
				remainingAttackingTroops--;
			}
		}

		return remainingAttackingTroops;
	}

	/**
	 * This method created an ArrayList filled with dice roll outcome based of how
	 * many armies are involved in attack.
	 * 
	 * @param armySize:
	 *            Number of armies involved in attack.
	 * @param isAttacker:
	 *            true if this call is from attacker front else false.
	 * @return a List<Integer> representing dice outcomes.
	 */
	public List<Integer> recordDiceRolls(int armySize, boolean isAttacker) {
		Random random = new Random();
		List<Integer> list = new ArrayList<>();
		int noOfDices;

		// decide how many max dice can be rolled.
		if (isAttacker) {
			if (armySize >= 3)
				noOfDices = 3;
			else
				noOfDices = armySize;
		} else {
			if (armySize >= 2)
				noOfDices = 2;
			else
				noOfDices = armySize;
		}

		int randomInt;
		for (int i = 0; i < noOfDices; i++) {
			randomInt = random.nextInt(6) + 1;
			list.add(i, randomInt);
		}

		return list;
	}
}
