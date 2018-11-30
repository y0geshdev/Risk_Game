package domain;

import javafx.util.Pair;

/**
 * Interface for player strategy.
 * 
 * @author Yogesh
 *
 */
public interface IStrategy {
	/**
	 * It's a method which implements reinforcement phase logic.
	 * 
	 * @param player:
	 *            Player for which reinforcement is to be done.
	 * @param selectedTerritory:
	 *            Territory to which reinforcement is to be done.
	 * @param numberOfArmies:
	 *            Number of armies to move.
	 * @param phaseViewModel:
	 *            PhaseViewModel instance.
	 */
	public void reinforcement(Player player, Territory selectedTerritory, int numberOfArmies,
			PhaseViewModel phaseViewModel);

	/**
	 * It's a method which implements fortification phase logic.
	 * 
	 * @param player:
	 *            Player for which fortification is done.
	 * @param from:
	 *            From which territory armies are to be moved.
	 * @param to:
	 *            To which territory armies are to be moved.
	 * @param armiesToMove:
	 *            Number of armies to move.
	 * @param phaseViewModel:
	 *            instance of phaseViewModel.
	 */
	public void fortify(Player player, Territory from, Territory to, int armiesToMove, PhaseViewModel phaseViewModel);

	/**
	 * It's a method which implements attack phase logic.
	 * 
	 * @param attacker:
	 *            Attacking player.
	 * @param defender:
	 *            Defender player.
	 * @param attackerTerritory:
	 *            Attacking territory.
	 * @param defenderTerritory:
	 *            Defending territory.
	 * @param isAllOutMode:
	 *            Boolean to represent if it is all out mode or normal mode.
	 * @param totalAttackerDice:
	 *            Total number of dice which attacker will roll.
	 * @param totalDefenderDice:
	 *            Total number of dice which defender will roll.
	 * @param phaseViewModel:
	 *            instance of phase view model.
	 * @return boolean indicated whether attacker won
	 *         attack or not and integer represents minimum number of armies which
	 *         he has to move if he won.
	 */
	public Pair<Boolean, Integer> attack(Player attacker, Player defender, Territory attackerTerritory,
			Territory defenderTerritory, boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice,
			PhaseViewModel phaseViewModel);
}
