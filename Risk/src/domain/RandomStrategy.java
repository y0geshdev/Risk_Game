package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javafx.util.Pair;

/**
 * Strategy for random player.
 * 
 * @author Yogesh
 *
 */
public class RandomStrategy implements IStrategy, Serializable {

	/**
	 * {@inheritDoc} This method will select a random territory to reinforce.
	 */
	@Override
	public void reinforcement(Player player, Territory selectedTerritory, int numberOfArmies,
			PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nPlacing armies for Random player.");
		phaseViewModel.setCurrentPlayer(player.getName());
		selectedTerritory = player.getTerritories().get(randomNumber(player.getTerritories().size()));
		selectedTerritory.setArmyCount(selectedTerritory.getArmyCount() + player.getArmyCount());
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nMoved " + player.getArmyCount()
				+ " armies to randomly selected territory(" + selectedTerritory.getName() + ").");
		player.setArmyCount(0);

	}

	/**
	 * {@inheritDoc} This method will select a randomly selected territory to
	 * fortify from a randomly selected territory.
	 */
	@Override
	public void fortify(Player player, Territory from, Territory to, int armiesToMove, PhaseViewModel phaseViewModel) {
		to = player.getTerritories().get(randomNumber(player.getTerritories().size()));
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nFortifying random territory : " + to.getName());
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
		from = fortifiableTerritories.size() == 0 ? null
				: fortifiableTerritories.get(randomNumber(fortifiableTerritories.size()));

		if (from == null) {
			phaseViewModel
					.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nNo possible territory to fortify " + to.getName());
			return;
		} else {
			if (from.getArmyCount() == 1) {
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nrandom territory to move armies from is "
						+ to.getName() + ".\nBut can't move armies as it contains only 1 army.");
				return;
			} else {
				armiesToMove = from.getArmyCount() - 1;
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nFortified " + to.getName() + " with "
						+ armiesToMove + " from " + from.getName());
				from.setArmyCount(1);
				to.setArmyCount(to.getArmyCount() + armiesToMove);
			}
		}

	}

	/**
	 * {@inheritDoc} This attack strategy will attack random number of time on a
	 * territory which is also random and the attack is done from random territory.
	 * Each attack will be normal mode attack and maximum number of dice will be
	 * rolled from both attacker and defender. If attacker conquered the territory
	 * before doing all the attacks then attack phase will be completed.
	 */
	@Override
	public Pair<Boolean, Integer> attack(Player attacker, Player defender, Territory attackerTerritory,
			Territory defenderTerritory, boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice,
			PhaseViewModel phaseViewModel) {

		boolean isWon = false;
		int remainingAttackingTroops = -1;
		List<Integer> attackerDiceRolls;
		List<Integer> defenderDiceRolls;

		// decide number of times to attack, attacker territory and defender territory.
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nAttacking with random strategy.");
		int numberOfAttacks = randomNumber(10) + 1;
		attackerTerritory = attacker.getTerritories().get(randomNumber(attacker.getTerritories().size()));
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nNo. of attacks: " + numberOfAttacks);
		phaseViewModel.setPhaseInfo(
				phaseViewModel.getPhaseInfo() + "\nRandom attacker territory: " + attackerTerritory.getName());

		List<Territory> defendingTerritoriesList = new ArrayList<>();
		for (Territory territory : attacker.getTerritories()) {
			if (territory.getOwner() != attacker)
				defendingTerritoriesList.add(territory);
		}
		defenderTerritory = defendingTerritoriesList.size() == 0 ? null
				: defendingTerritoriesList.get(randomNumber(defendingTerritoriesList.size()));

		// If there is not territory to attack then return else do normal mode attack
		// numberOfAttacks times.
		if (defenderTerritory == null) {
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nThere is no territory to attack from "
					+ attackerTerritory.getName());
			return new Pair<Boolean, Integer>(isWon, null);
		} else {
			phaseViewModel.setPhaseInfo(
					phaseViewModel.getPhaseInfo() + "\nRandom defender territory: " + defenderTerritory.getName());
			for (int i = 0; i < numberOfAttacks; i++) {
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + i + " attack...");

				if (attackerTerritory.getArmyCount() == 1) {
					phaseViewModel.setPhaseInfo(
							phaseViewModel.getPhaseInfo() + "\nCan't attack as it have only one army left.");
				} else {
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
				attacker.getTerritories().add(defenderTerritory);
				defenderTerritory.setOwner(attacker);
				// move remaining attacking armies to conquered territory
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nMoved " + remainingAttackingTroops
						+ " armies from " + attackerTerritory.getName() + " to " + defenderTerritory.getName()
						+ " after conquering it.");
				defenderTerritory.setArmyCount(remainingAttackingTroops);
				attackerTerritory.setArmyCount(attackerTerritory.getArmyCount() - remainingAttackingTroops);
				return new Pair<>(isWon, remainingAttackingTroops);
			} else {
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + "attacker didn't won territory.");
				return new Pair<Boolean, Integer>(isWon, null);
			}
		}
	}

	private int randomNumber(int max) {
		Random random = new Random();
		return random.nextInt(max);
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
	 *            Instance of PhaseViewModel class to update information for
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
