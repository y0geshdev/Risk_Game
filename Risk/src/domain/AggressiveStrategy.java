package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javafx.util.Pair;

/**
 * Strategy for aggressive player.
 * 
 * @author Yogesh
 *
 */
public class AggressiveStrategy implements IStrategy {

	@Override
	public void reinforcement(Player player, Territory selectedTerritory, int numberOfArmies,
			PhaseViewModel phaseViewModel) {

		phaseViewModel.setPhaseInfo("Placing armies for Aggressive player.");
		phaseViewModel.setCurrentPlayer(player.getName());
		selectedTerritory = fetchAttackFromTerritory(player);
		selectedTerritory.setArmyCount(selectedTerritory.getArmyCount() + player.getArmyCount());
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nMoved "+player.getArmyCount()+" armies to strongest territory("
				+ selectedTerritory.getName() + ").");
		player.setArmyCount(0);
	}

	@Override
	public void fortify(Player player, Territory from, Territory to, int armiesToMove, PhaseViewModel phaseViewModel) {
		to = fetchAttackFromTerritory(player);
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nFortifying strongest territory : "+to.getName());
		List<Territory> fortifiableTerritories = new ArrayList<>();

		Queue<Territory> queue = new LinkedList<>();
		queue.add(to);
		Territory t;

		// doing BFS to get all the territory which can fortify selected territory.
		while (queue.size() > 0) {
			t = queue.poll();
			for (Territory neighbours : t.getNeighbourTerritories()) {
				if (neighbours.getOwner() == player && !fortifiableTerritories.contains(neighbours)) {
					fortifiableTerritories.add(neighbours);
					queue.add(neighbours);
				}
			}
		}
		if (fortifiableTerritories.contains(to))
			fortifiableTerritories.remove(to);
		
		from = fortifiableTerritories.size()>0?fortifiableTerritories.get(0):null;
		for(Territory territory : fortifiableTerritories) {
			if(territory.getArmyCount()>from.getArmyCount())
				from = territory;
		}
		
		if(from==null || from.getArmyCount()==1) {
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nNo possible territory to fortify "+to.getName());
			return;
		}else {
			armiesToMove = from.getArmyCount()-1;
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nFortified strongest territory with "+armiesToMove+" from "+from.getName());
			from.setArmyCount(1);
			to.setArmyCount(to.getArmyCount() + armiesToMove);
		}

	}

	@Override
	public Pair<Boolean, Integer> attack(Player attacker, Player defender, Territory attackerTerritory,
			Territory defenderTerritory, boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice,
			PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo("Will attack from strongest territory to it's weakest neighbour.");

		attackerTerritory = fetchAttackFromTerritory(attacker);
		defenderTerritory = fetchAttackToTerritory(attackerTerritory);
		defender = defenderTerritory != null ? defenderTerritory.getOwner() : null;
		phaseViewModel.setPhaseInfo(
				phaseViewModel.getPhaseInfo() + "\nAttacking Territory: " + attackerTerritory.getName());

		if (defenderTerritory == null) {
			phaseViewModel.setPhaseInfo(
					phaseViewModel.getPhaseInfo() + "\nCan't find any territory to attack from selected territory.");
			return new Pair<Boolean, Integer>(Boolean.FALSE, null);
		} else {
			phaseViewModel.setPhaseInfo(
					phaseViewModel.getPhaseInfo() + "\nDefending Territory: " + defenderTerritory.getName());
			boolean isWon = false;
			int remainingAttackingTroops = -1;
			List<Integer> attackerDiceRolls;
			List<Integer> defenderDiceRolls;
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

			// if attacker territory in current attack.
			if (isWon) {
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "attacker won territory.");
				defender.getTerritories().remove(defenderTerritory);
				attacker.getTerritories().add(defenderTerritory);
				defenderTerritory.setOwner(attacker);
				// move all the armies to conquered territory
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nMoved "
						+ (attackerTerritory.getArmyCount() - 1) + " armies from " + attackerTerritory.getName()
						+ " to " + defenderTerritory.getName() + " after conquering it.");
				defenderTerritory.setArmyCount(attackerTerritory.getArmyCount() - 1);
				attackerTerritory.setArmyCount(1);
				return new Pair<>(isWon, remainingAttackingTroops);
			} else {
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "attacker didn't won territory.");
				return new Pair<Boolean, Integer>(isWon, null);
			}
		}

	}

	public Territory fetchAttackFromTerritory(Player player) {
		List<Territory> territoryList = player.getTerritories();
		Territory strongestTerritory = territoryList.get(0);
		for (Territory territory : territoryList)
			if (territory.getArmyCount() > strongestTerritory.getArmyCount())
				strongestTerritory = territory;
		return strongestTerritory;
	}

	public Territory fetchAttackToTerritory(Territory attackerTerritory) {
		List<Territory> attackableTerritory = new ArrayList<>();
		Player attacker = attackerTerritory.getOwner();
		for (Territory territory : attackerTerritory.getNeighbourTerritories()) {
			if (territory.getOwner() != attacker)
				attackableTerritory.add(territory);
		}

		Territory weakestTerritory = attackableTerritory.size() > 0 ? attackableTerritory.get(0) : null;
		for (Territory territory : attackableTerritory)
			if (territory.getArmyCount() < weakestTerritory.getArmyCount())
				weakestTerritory = territory;

		return weakestTerritory;
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
				defenderTerritory.setArmyCount(defenderTerritory.getArmyCount() - 1);
			}
			// case where defender beats attacker current dice roll.
			else {
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
	 * @return a List representing dice outcomes.
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
