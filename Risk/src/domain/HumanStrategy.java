package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.util.Pair;

/**
 * Strategy for human player.
 * 
 * @author Yogesh
 *
 */
public class HumanStrategy implements IStrategy,Serializable {

	/**
	 * {@inheritDoc}
	 * This method reinforce selected territory with selected number of armies.
	 */
	@Override
	public void reinforcement(Player player, Territory selectedTerritory, int numberOfArmies,
			PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nReinforcing for Human player.");
		selectedTerritory.setArmyCount(selectedTerritory.getArmyCount() + numberOfArmies);
		player.setArmyCount(player.getArmyCount() - numberOfArmies);
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo()+"\n"+String.valueOf(numberOfArmies) + " armies moved to " + selectedTerritory.getName());
	}

	/**
	 * {@inheritDoc}
	 * This method fortify selected territory by moving entered number of armies from selected territory.
	 */
	@Override
	public void fortify(Player player, Territory from, Territory to, int armiesToMove, PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nFortification for Human player.");
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nMoved " + armiesToMove + " to " + to.getName()
				+ " from " + from.getName());
		from.setArmyCount(from.getArmyCount() - armiesToMove);
		to.setArmyCount(to.getArmyCount() + armiesToMove);

	}

	/**
	 * {@inheritDoc}
	 * This method perform attack from attacker to defender territory.
	 * 
	 */
	@Override
	public Pair<Boolean, Integer> attack(Player attacker, Player defender, Territory attackerTerritory,
			Territory defenderTerritory, boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice,
			PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nAttacking for Human player.");
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
			attacker.getTerritories().add(defenderTerritory);
			defenderTerritory.setOwner(attacker);
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
