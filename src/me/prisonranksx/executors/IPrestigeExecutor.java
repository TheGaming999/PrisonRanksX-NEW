package me.prisonranksx.executors;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import me.prisonranksx.components.RequirementsComponent.RequirementEvaluationResult;
import me.prisonranksx.events.PrestigeUpdateCause;

public interface IPrestigeExecutor {

	enum PrestigeResult {

		FAIL_NO_PERMISSION(false),
		FAIL_LAST_PRESTIGE(false),
		FAIL_NOT_LAST_RANK(false),
		FAIL_NOT_ENOUGH_BALANCE(false),
		FAIL_REQUIREMENTS_NOT_MET(false),
		FAIL_EVENT_CANCEL(false),
		FAIL_OTHER(false),
		SUCCESS(true);

		private boolean success;
		private RequirementEvaluationResult requirementEvaluationResult;

		PrestigeResult(boolean success) {
			this.success = success;
		}

		public boolean isSuccessful() {
			return success;
		}

		public PrestigeResult getResult() {
			return this;
		}

		@Nullable
		public RequirementEvaluationResult getRequirementEvaluationResult() {
			return requirementEvaluationResult;
		}

		public PrestigeResult setRequirementEvaluationResult(
				@Nullable RequirementEvaluationResult requirementEvaluationResult) {
			this.requirementEvaluationResult = requirementEvaluationResult;
			return this;
		}

	}

	/**
	 * Performs a check of all the neccessary requirements on a player to prestige
	 * <ul>
	 * Conditions include:
	 * <li>Permissions
	 * <li>Last Prestige
	 * <li>Last Rank, see {@linkplain #canPrestige(Player, boolean)} to skip this
	 * condition
	 * <li>Balance
	 * <li>PlaceholderAPI string and number requirements
	 * </ul>
	 * 
	 * @param player check if specified player can prestige or not
	 * @return RankupResult with the reason of prestige failure or success
	 */
	PrestigeResult canPrestige(Player player);

	/**
	 * Performs a check of all the neccessary requirements on a player to prestige
	 * <ul>
	 * Conditions include:
	 * <li>Permissions
	 * <li>Last Prestige
	 * <li>Last Rank if {@code skipLastRankCheck} is false
	 * <li>Balance
	 * <li>PlaceholderAPI string and number requirements
	 * </ul>
	 * 
	 * @param player            check if specified player can prestige or not
	 * @param skipLastRankCheck whether last rank should be checked or not, this is
	 *                          normally used with prestige max
	 * @return RankupResult with the reason of prestige failure or success
	 */
	PrestigeResult canPrestige(Player player, boolean skipLastRankCheck);

	/**
	 * 
	 * @param player player to toggle auto prestige for
	 * @return new state
	 */
	boolean toggleAutoPrestige(Player player);

	/**
	 * 
	 * @param player player to toggle auto prestige for
	 * @param enable forcefully enable / disable regardless of player auto prestige
	 *               state
	 * @return new state
	 */
	boolean toggleAutoPrestige(Player player, boolean enable);

	/**
	 * 
	 * @param player player to check auto prestige state for
	 * @return whether auto prestige is enabled or not
	 */
	boolean isAutoPrestigeEnabled(Player player);

	/**
	 * 
	 * @param player player to promote to the next prestige
	 * @return RankupResult that notifies you of the outcome of the promotion,
	 *         whether it failed or succeeded
	 */
	PrestigeResult prestige(Player player);

	/**
	 * 
	 * @param player player to promote to the next prestige
	 * @param silent prevents messages from being sent on promotion failure. This
	 *               method is used by auto prestige
	 * @return RankupResult that notifies you of the outcome of the promotion,
	 *         whether it failed or succeeded
	 */
	PrestigeResult prestige(Player player, boolean silent);

	/**
	 * Forcefully prestiges a player to the next prestige
	 * 
	 * @param player player to promote to the next prestige without checks
	 * @return RankupResult only FAIL_LAST_PRESTIGE and SUCCESS
	 */
	PrestigeResult forcePrestige(Player player);

	/**
	 * 
	 * @param player to perform max prestige for
	 * @return RankupResult that notifies you of the last outcome of the
	 *         promotion, whether it failed or succeeded
	 */
	PrestigeResult maxPrestige(Player player);

	/**
	 * 
	 * @param player          player to include in the event
	 * @param cause           the cause of the prestige
	 * @param result          prestige outcome
	 * @param updatedPrestige prestige that player will get prestiged to
	 * @param currentPrestige prestige that player will prestige from
	 * @param successful      whether player gonna successfully prestige or not
	 * @return false if event is cancelled, true otherwise
	 */
	boolean callPrestigeUpdateEvent(Player player, PrestigeUpdateCause cause, PrestigeResult result,
			@Nullable String updatedPrestige, @Nullable String currentPrestige, boolean successful);

	/**
	 * 
	 * @param player          player to include in the event
	 * @param cause           the cause of the prestige
	 * @param result          prestige outcome
	 * @param updatedPrestige prestige that player will get prestiged to
	 * @param currentPrestige prestige that player will prestige from
	 * @param successful      whether player gonna successfully prestige or not
	 * @return false if event is cancelled, true otherwise
	 */
	boolean callAsyncAutoPrestigeEvent(Player player, PrestigeUpdateCause cause, PrestigeResult result,
			@Nullable String updatedPrestige, @Nullable String currentPrestige, boolean successful);

	boolean callPrePrestigeMaxEvent(Player player);

	void callAsyncPrestigeMaxEvent(Player player, PrestigeResult lastResult, String fromPrestige, String toPrestige,
			int totalPrestiges, double takenBalance);

}
