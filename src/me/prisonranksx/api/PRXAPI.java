package me.prisonranksx.api;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.executors.IRankupExecutor;
import me.prisonranksx.executors.IRankupExecutor.RankupResult;

public class PRXAPI {

	private static PrisonRanksX plugin = PrisonRanksX.getInstance();

	public static PrisonRanksX getInstance() {
		return plugin = PrisonRanksX.getInstance();
	}

	public static RankupResult rankup(Player player) {
		return plugin.getRankupExecutor().rankup(player);
	}

	public static RankupResult rankup(Player player, boolean silent) {
		return plugin.getRankupExecutor().rankup(player, silent);
	}

	public static RankupResult rankup(Player promoter, Player target) {
		return plugin.getRankupExecutor().rankup(promoter, target);
	}

	public static RankupResult forceRankup(Player player) {
		return plugin.getRankupExecutor().forceRankup(player);
	}

	public static CompletableFuture<RankupResult> maxRankup(Player player) {
		return plugin.getRankupExecutor().maxRankup(player);
	}

	public static CompletableFuture<RankupResult> maxRankup(Player player, String lastRank) {
		return plugin.getRankupExecutor().maxRankup(player, lastRank);
	}

	public static boolean toggleAutoRankup(Player player) {
		return plugin.getRankupExecutor().toggleAutoRankup(player);
	}

	public static boolean toggleAutoRankup(Player player, boolean enable) {
		return plugin.getRankupExecutor().toggleAutoRankup(player, enable);
	}

	public static boolean isAutoRankup(Player player) {
		return plugin.getRankupExecutor().isAutoRankupEnabled(player);
	}

	public static RankupResult canRankup(Player player) {
		return plugin.getRankupExecutor().canRankup(player);
	}

	public static Set<UUID> getAutoRankupPlayers() {
		return IRankupExecutor.AUTO_RANKUP_PLAYERS;
	}

}
