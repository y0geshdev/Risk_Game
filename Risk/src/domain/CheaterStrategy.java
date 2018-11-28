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

	@Override
	public void reinforcement(Player player, Territory selectedTerritory, int numberOfArmies,
			PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nPlayer will double armies in all territory.");
		for (Territory territory : player.getTerritories()) {
			phaseViewModel.setPhaseInfo(
					phaseViewModel.getPhaseInfo() + "\n" + territory.getName() + " army count is changes from "
							+ territory.getArmyCount() + " to " + (2 * territory.getArmyCount()));
			territory.setArmyCount(2 * territory.getArmyCount());
		}

	}

	@Override
	public void fortify(Player player, Territory from, Territory to, int armiesToMove, PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nPlayer will fortify all territory which have neighbors occupied by other players.");
		for (Territory possibleToTerritory : player.getTerritories()) {
			for (Territory neighbourTerritory : possibleToTerritory.getNeighbourTerritories()) {
				if (neighbourTerritory.getOwner() != player) {
					phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nfortifying "
							+ possibleToTerritory.getName() + " having neighbor " +neighbourTerritory.getName()+"("+neighbourTerritory.getOwner().getName()+")." );
					possibleToTerritory.setArmyCount(2*possibleToTerritory.getArmyCount());
					break;
				}
			}
		}
	}

	@Override
	public Pair<Boolean, Integer> attack(Player attacker, Player defender, Territory attackerTerritory,
			Territory defenderTerritory, boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice,
			PhaseViewModel phaseViewModel) {
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
