package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This entity class represent a Player in game.
 * 
 * @author Yogesh
 *
 */
public class Player {
	private String name;
	private List<Territory> territories;
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

	@Override
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
	 * This method implement all-out mode attack phase.
	 * 
	 * @param attackerTerritory:
	 *            Territory from which attacker attack.
	 * @param defenderTerritory:
	 *            Territory to which attacker attack.
	 * @param defender:
	 *            Player who owns defender territory.
	 * @return true if attacker won and occupy attacked territory else return false.
	 */
	public boolean attack(Territory attackerTerritory, Territory defenderTerritory, Player defender,
			PhaseViewModel phaseViewModel) {
		boolean isWon = false;
		List<Integer> attackerDiceRolls;
		List<Integer> defenderDiceRolls;
		while (attackerTerritory.getArmyCount() > 1) {
			phaseViewModel
			.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "dice rolling...");
			attackerDiceRolls = recordDiceRolls(attackerTerritory.getArmyCount() - 1, true);
			defenderDiceRolls = recordDiceRolls(defenderTerritory.getArmyCount(), false);

			Collections.sort(attackerDiceRolls, Collections.reverseOrder());
			Collections.sort(defenderDiceRolls, Collections.reverseOrder());

			int iterations = attackerDiceRolls.size() > defenderDiceRolls.size() ? defenderDiceRolls.size()
					: attackerDiceRolls.size();
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "attacker dice "
					+ attackerDiceRolls.toString() + "\n" + "defender dice " + defenderDiceRolls.toString());

			for (int i = 0; i < iterations; i++) {
				// case where attacker won current dice roll
				if (attackerDiceRolls.get(i) > defenderDiceRolls.get(i)) {
					phaseViewModel
							.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "attacker won " + i + "dice roll.");
					defenderTerritory.setArmyCount(defenderTerritory.getArmyCount() - 1);
				} else {
					phaseViewModel
							.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "defender won " + i + "dice roll.");
					attackerTerritory.setArmyCount(attackerTerritory.getArmyCount() - 1);
				}
			}
			if (defenderTerritory.getArmyCount() == 0) {
				isWon = true;
				break;
			}
		}
		if (isWon) {
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "attacker won.");
			defender.getTerritories().remove(defenderTerritory);
			this.getTerritories().add(defenderTerritory);
			defenderTerritory.setOwner(this);
			return isWon;
		} else {
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "defender won.");
			return isWon;
		}
	}

	private List<Integer> recordDiceRolls(int armySize, boolean isAttacker) {
		Random random = new Random();
		List<Integer> list = new ArrayList();
		int noOfDices;
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
