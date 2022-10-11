package me.prisonranksx.executors;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import me.prisonranksx.components.RequirementsComponent.RequirementEvaluationResult;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.holders.Rank;
import me.prisonranksx.holders.User;
import me.prisonranksx.reflections.UniqueId;

public interface IRankupExecutor {

	static final Set<UUID> AUTO_RANKUP_PLAYERS = new HashSet<>();

	public static Set<UUID> getAutoRankupPlayers() {
		return AUTO_RANKUP_PLAYERS;
	}

	public static boolean switchAutoRankup(Player player) {
		UUID uniqueId = UniqueId.getUUID(player);
		return AUTO_RANKUP_PLAYERS.contains(uniqueId) ? AUTO_RANKUP_PLAYERS.remove(uniqueId)
				: AUTO_RANKUP_PLAYERS.add(uniqueId);
	}

	public static boolean switchAutoRankup(Player player, boolean enable) {
		UUID uniqueId = UniqueId.getUUID(player);
		return enable ? AUTO_RANKUP_PLAYERS.add(uniqueId) : AUTO_RANKUP_PLAYERS.remove(uniqueId);
	}

	public static boolean isAutoRankup(Player player) {
		return AUTO_RANKUP_PLAYERS.contains(UniqueId.getUUID(player));
	}

	public enum RankupResult {

		FAIL_NO_PERMISSION(false),
		FAIL_LAST_RANK(false),
		FAIL_NOT_ENOUGH_BALANCE(false),
		FAIL_REQUIREMENTS_NOT_MET(false),
		FAIL_OTHER(false),
		SUCCESS(true);

		private boolean success;
		private RequirementEvaluationResult requirementEvaluationResult;
		private User userResult;
		private double doubleResult = -1;
		@Nullable
		private String stringResult;
		@Nullable
		private Rank rankResult;

		RankupResult(boolean success) {
			this.success = success;
		}

		public boolean isSuccessful() {
			return success;
		}

		public RankupResult getResult() {
			return this;
		}

		@Nullable
		public RequirementEvaluationResult getRequirementEvaluationResult() {
			return requirementEvaluationResult;
		}

		public RankupResult withRequirementEvaluation(
				@Nullable RequirementEvaluationResult requirementEvaluationResult) {
			this.requirementEvaluationResult = requirementEvaluationResult;
			return this;
		}

		@Nullable
		public String getStringResult() {
			return stringResult;
		}

		public RankupResult withString(@Nullable String stringResult) {
			this.stringResult = stringResult;
			return this;
		}

		public double getDoubleResult() {
			return doubleResult;
		}

		public RankupResult withDouble(double doubleResult) {
			this.doubleResult = doubleResult;
			return this;
		}

		@Nullable
		public Rank getRankResult() {
			return rankResult;
		}

		public RankupResult withRank(@Nullable Rank rankResult) {
			this.rankResult = rankResult;
			return this;
		}

		public User getUserResult() {
			return userResult;
		}

		public RankupResult withUser(User userResult) {
			this.userResult = userResult;
			return this;
		}

	}

	/**
	 * Performs a check of all the neccessary requirements on a player to rankup
	 * <ul>
	 * Conditions include:
	 * <li>Permissions
	 * <li>Last Rank
	 * <li>Balance
	 * <li>PlaceholderAPI string and number requirements
	 * </ul>
	 * 
	 * @param player check if specified player can rankup or not
	 * @return RankupResult with the reason of rankup failure or success
	 */
	RankupResult canRankup(Player player);

	/**
	 * Performs a check of all the neccessary requirements on a player to rankup
	 * <ul>
	 * Conditions include:
	 * <li>Permissions
	 * <li>Last Rank
	 * <li>Balance
	 * <li>PlaceholderAPI string and number requirements
	 * </ul>
	 * 
	 * @param player  check if specified player can rankup or not
	 * @param balance balance to check
	 * @return RankupResult with the reason of rankup failure or success
	 */
	RankupResult canRankup(Player player, double balance);

	/**
	 * 
	 * @param player player to toggle auto rankup for
	 * @return new state
	 */
	boolean toggleAutoRankup(Player player);

	/**
	 * 
	 * @param player player to toggle auto rankup for
	 * @param enable forcefully enable / disable regardless of player auto rankup
	 *               state
	 * @return new state
	 */
	boolean toggleAutoRankup(Player player, boolean enable);

	/**
	 * 
	 * @param player player to check auto rankup state for
	 * @return whether auto rankup is enabled or not
	 */
	boolean isAutoRankupEnabled(Player player);

	/**
	 * 
	 * @param player player to promote to the next rank
	 * @return RankupResult that notifies you of the outcome of the promotion,
	 *         whether it failed or succeeded
	 */
	RankupResult rankup(Player player);

	/**
	 * 
	 * @param player player that will promote target to the next rank
	 * @param target target to be promoted
	 * @return RankupResult that notifies you of the outcome of the promotion,
	 *         whether it failed or succeeded
	 */
	RankupResult rankup(Player player, Player target);

	/**
	 * 
	 * @param player player to promote to the next rank
	 * @param silent prevents messages from being sent on promotion failure. This
	 *               method is used by auto rankup
	 * @return RankupResult that notifies you of the outcome of the promotion,
	 *         whether it failed or succeeded
	 */
	RankupResult rankup(Player player, boolean silent);

	/**
	 * Forcefully promotes a player to the next rank
	 * 
	 * @param player player to promote to the next rank without checks
	 * @return RankupResult only results in FAIL_LAST_RANK and SUCCESS
	 */
	RankupResult forceRankup(Player player);

	/**
	 * 
	 * @param player to perform max rankup for
	 * @return A completable future of RankupResult that notifies you of the last
	 *         outcome of the
	 *         promotion, whether it failed or succeeded
	 */
	CompletableFuture<RankupResult> maxRankup(Player player);

	/**
	 * 
	 * @param player   to perform max rankup for
	 * @param lastRank last rank player is allowed to reach or null to ignore
	 * @return A completable future of RankupResult that notifies you of the last
	 *         outcome of the
	 *         promotion, whether it failed or succeeded
	 */
	CompletableFuture<RankupResult> maxRankup(Player player, @Nullable String lastRank);

	/**
	 * 
	 * @param rank   to execute components of
	 * @param player to perform components on
	 */
	void executeComponents(Rank rank, Player player);

	/**
	 * 
	 * @param player      player to include in the event
	 * @param cause       the cause of the rankup
	 * @param result      rankup outcome
	 * @param updatedRank rank that player will get promoted to
	 * @return false if event is cancelled, true otherwise
	 */
	boolean callRankUpdateEvent(Player player, RankUpdateCause cause, RankupResult result,
			@Nullable String updatedRank);

	/**
	 * 
	 * @param player      player to include in the event
	 * @param result      rankup outcome
	 * @param updatedRank rank that player will get promoted to
	 * @return false if event is cancelled, true otherwise
	 */
	boolean callAsyncAutoRankupEvent(Player player, RankupResult result, @Nullable String updatedRank);

	/**
	 * 
	 * @param player          player to include in the event
	 * @param rankupFrom      rank player is going to rankup from
	 * @param ranksToBePassed ranks that player will go through
	 * @return false if event is cancelled, ture otherwise
	 */
	boolean callPreRankupMaxEvent(Player player, Rank rankupFrom, Set<Rank> ranksToBePassed);

	/**
	 * 
	 * @param player       player to include in the event
	 * @param lastResult   last rankup result of the max rankup
	 * @param fromRank     rank player started ranking up from
	 * @param toRank       rank player landed at
	 * @param totalRankups amount of ranks player has gone through
	 * @param takenBalance how much balance was taken from player
	 * @param limited      whether max rankup is set to stop at specific rank rather
	 *                     than last rank
	 */
	void callAsyncRankupMaxEvent(Player player, RankupResult lastResult, String fromRank, String toRank,
			int totalRankups, double takenBalance, boolean limited);

}
