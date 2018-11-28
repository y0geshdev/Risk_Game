package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.util.Pair;

/**
 * Strategy for benevolent player.
 * 
 * @author Yogesh
 *
 */
public class BenevolentStrategy implements IStrategy, Serializable {

	@Override
	public void reinforcement(Player player, Territory selectedTerritory, int numberOfArmies,
			PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo()+"\nPlayer will place armies on weakest territory.");
		selectedTerritory = findWeakestTerritory(player);
		selectedTerritory.setArmyCount(selectedTerritory.getArmyCount()+player.getArmyCount());
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo()+"\nMoved "+player.getArmyCount()+" to weakest territory("+selectedTerritory.getName()+").");
		player.setArmyCount(0);
	}

	@Override
	public void fortify(Player player, Territory from, Territory to, int armiesToMove, PhaseViewModel phaseViewModel) {
		to = findWeakestTerritory(player);
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nFortifying weakest territory : "+to.getName());
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
			phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo() + "\nFortified "+to.getName()+" with "+armiesToMove+" from "+from.getName());
			from.setArmyCount(1);
			to.setArmyCount(to.getArmyCount() + armiesToMove);
		}

	}

	@Override
	public Pair<Boolean, Integer> attack(Player attacker, Player defender, Territory attackerTerritory,
			Territory defenderTerritory, boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice,
			PhaseViewModel phaseViewModel) {
		phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo()+"\nPlayer will not attack.");
		return new Pair<Boolean, Integer>(Boolean.FALSE, null);
	}
	
	private Territory findWeakestTerritory(Player player) {
		List<Territory> territoryList = player.getTerritories();
		Territory weakestTerritory = territoryList.get(0);
		for (Territory territory : territoryList)
			if (territory.getArmyCount() < weakestTerritory.getArmyCount())
				weakestTerritory = territory;
		return weakestTerritory;
	}

}
