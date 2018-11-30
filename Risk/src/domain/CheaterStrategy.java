package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

/**
 * Strategy for cheater player.
 * 
 * @author Yogesh
 *
 */
public class CheaterStrategy implements IStrategy, Serializable {

	/**
	 * Value of maximum possible army count per territory.
	 */
	private int MAX_POSSIBLE_ARMY = 10000;

	/**
	 * {@inheritDoc} This method doubles the armies in all the players territories.
	 */
	@Override
	public void reinforcement(Player player, Territory selectedTerritory, int numberOfArmies,
			PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nReinforcing for Cheater player.");
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nPlayer will double armies in all territory.");

		for (Territory territory : player.getTerritories()) {
			if (territory.getArmyCount() * 2 >= MAX_POSSIBLE_ARMY) {
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\n" + territory.getName()
						+ " army count is changed from " + territory.getArmyCount() + " to " + MAX_POSSIBLE_ARMY);
				territory.setArmyCount(MAX_POSSIBLE_ARMY);
			} else {
				phaseViewModel.setPhaseInfo(
						phaseViewModel.getPhaseInfo() + "\n" + territory.getName() + " army count is changed from "
								+ territory.getArmyCount() + " to " + (2 * territory.getArmyCount()));
				territory.setArmyCount(2 * territory.getArmyCount());
			}

		}
		player.setArmyCount(0);
	}

	/**
	 * {@inheritDoc} This method will double the armies in all territories that have
	 * neighbors belonging to other players.
	 */
	@Override
	public void fortify(Player player, Territory from, Territory to, int armiesToMove, PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nFortification for Cheater player.");
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo()
				+ "\nPlayer will fortify all territory which have neighbors occupied by other players.");

		for (Territory possibleToTerritory : player.getTerritories()) {
			for (Territory neighbourTerritory : possibleToTerritory.getNeighbourTerritories()) {
				if (neighbourTerritory.getOwner() != player) {
					phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nfortifying "
							+ possibleToTerritory.getName() + " having neighbor " + neighbourTerritory.getName() + "("
							+ neighbourTerritory.getOwner().getName() + ").");

					if (possibleToTerritory.getArmyCount() * 2 >= MAX_POSSIBLE_ARMY) {
						possibleToTerritory.setArmyCount(MAX_POSSIBLE_ARMY);
					} else {
						possibleToTerritory.setArmyCount(2 * possibleToTerritory.getArmyCount());
					}
					break;
				}
			}
		}
	}

	/**
	 * {@inheritDoc} Attack will conquer all the neighbor territories of this player
	 * and place 1 army in every conquered territory.
	 */
	@Override
	public Pair<Boolean, Integer> attack(Player attacker, Player defender, Territory attackerTerritory,
			Territory defenderTerritory, boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice,
			PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nAttacking for Cheater player.");
		List<Territory> conqueredTerritories = new ArrayList<>();
		phaseViewModel
				.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nPlayer try conquer all the neighbouring territories.");

		for (Territory possibleAttackerTerritory : attacker.getTerritories()) {
			for (Territory possibleDefenderTerritory : possibleAttackerTerritory.getNeighbourTerritories()) {

				if (possibleDefenderTerritory.getOwner() != attacker) {
					phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nattacked from "
							+ possibleAttackerTerritory.getName() + " and won " + possibleDefenderTerritory.getName());
					possibleDefenderTerritory.getOwner().getTerritories().remove(possibleDefenderTerritory);
					possibleAttackerTerritory.setArmyCount(1);
					possibleDefenderTerritory.setOwner(attacker);
					conqueredTerritories.add(possibleDefenderTerritory);
				}
			}
		}
		attacker.getTerritories().addAll(conqueredTerritories);

		if (conqueredTerritories.size() > 0)
			return new Pair<Boolean, Integer>(Boolean.TRUE, null);
		else {
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nDidn't found any territory to conquer.");
			return new Pair<Boolean, Integer>(Boolean.FALSE, null);
		}
	}

}
