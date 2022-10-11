package me.prisonranksx.hooks;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.track.Track;
import net.luckperms.api.track.TrackManager;

public class EZLuckPerms {

	private static final LuckPerms LUCKPERMS;
	private static final UserManager USER_MANAGER;
	private static final TrackManager TRACK_MANAGER;
	private static final GroupManager GROUP_MANAGER;

	static {
		LUCKPERMS = setupLuckPerms();
		USER_MANAGER = LUCKPERMS.getUserManager();
		TRACK_MANAGER = LUCKPERMS.getTrackManager();
		GROUP_MANAGER = LUCKPERMS.getGroupManager();
	}

	private static LuckPerms setupLuckPerms() {
		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		return provider == null ? null : provider.getProvider();
	}

	@Nullable
	public static LuckPerms getLuckPerms() {
		return LUCKPERMS;
	}

	public static UserManager getUserManager() {
		return USER_MANAGER;
	}

	public static TrackManager getTrackManager() {
		return TRACK_MANAGER;
	}

	public static GroupManager getGroupManager() {
		return GROUP_MANAGER;
	}

	public static User getUser(UUID uniqueId) {
		return USER_MANAGER.getUser(uniqueId);
	}

	public static User getUser(String name) {
		return USER_MANAGER.getUser(name);
	}

	public static User getUserAlways(UUID uniqueId) {
		User user = USER_MANAGER.getUser(uniqueId);
		return user == null ? USER_MANAGER.loadUser(uniqueId).join() : user;
	}

	public static CompletableFuture<User> loadUser(UUID uniqueId) {
		User user = USER_MANAGER.getUser(uniqueId);
		return user != null ? CompletableFuture.completedFuture(user) : USER_MANAGER.loadUser(uniqueId);
	}

	public static Track getTrack(String trackName) {
		return TRACK_MANAGER.getTrack(trackName);
	}

	public static Group getGroup(String groupName) {
		return GROUP_MANAGER.getGroup(groupName);
	}

	public static Group getGroup(InheritanceNode inheritanceNode) {
		return GROUP_MANAGER.getGroup(inheritanceNode.getGroupName());
	}

	public static Node getCachedPermissionData(User user, String permission) {
		return user.getCachedData().getPermissionData().queryPermission(permission).node();
	}

	public static Node getCachedPermissionData(UUID uniqueId, String permission) {
		return getCachedPermissionData(getUser(uniqueId), permission);
	}

	public static Node getCachedPermissionData(String name, String permission) {
		return getCachedPermissionData(getUser(name), permission);
	}

	public static CachedMetaData getCachedMetaData(User user) {
		return user.getCachedData().getMetaData();
	}

	public static CachedMetaData getCachedMetaData(UUID uniqueId) {
		return getCachedMetaData(getUser(uniqueId));
	}

	public static CachedMetaData getCachedMetaData(String name) {
		return getCachedMetaData(getUser(name));
	}

	/**
	 * 
	 * @param @param user to retrieve data from
	 * @return all groups that the player inherits / has
	 */
	public static Collection<Group> getPlayerGroups(User user) {
		Collection<Group> groups = user.getInheritedGroups(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build());
		return groups;
	}

	/**
	 * 
	 * @param uniqueId player uuid
	 * @return all groups that the player inherits / has
	 */
	public static Collection<Group> getPlayerGroups(UUID uniqueId) {
		return getPlayerGroups(USER_MANAGER.getUser(uniqueId));
	}

	/**
	 * 
	 * @param name player name
	 * @return all groups that the player inherits / has
	 */
	public static Collection<Group> getPlayerGroups(String name) {
		return getPlayerGroups(USER_MANAGER.getUser(name));
	}

	/**
	 * 
	 * @param user to retrieve data from
	 * @return player parent group
	 */
	public static Group getPlayerGroup(User user) {
		Collection<Group> groups = user.getInheritedGroups(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build());
		return (groups.toArray(new Group[0])[0]);
	}

	/**
	 * 
	 * @param uniqueId player uuid
	 * @return player parent group
	 */
	public static Group getPlayerGroup(UUID uniqueId) {
		return getPlayerGroup(USER_MANAGER.getUser(uniqueId));
	}

	/**
	 * 
	 * @param name player name
	 * @return player parent group
	 */
	public static Group getPlayerGroup(String name) {
		return getPlayerGroup(USER_MANAGER.getUser(name));
	}

	/**
	 * 
	 * @param user      to retrieve data from
	 * @param trackName the track to get the groups from
	 * @return groups that the player has on a track
	 */
	public static Set<Group> getPlayerGroups(User user, String trackName) {
		Track track = TRACK_MANAGER.getTrack(trackName);
		Set<Group> groups = user.getNodes(NodeType.INHERITANCE)
				.stream()
				.map(InheritanceNode::getGroupName)
				.filter(track::containsGroup)
				.map(GROUP_MANAGER::getGroup)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return groups;
	}

	/**
	 * 
	 * @param user      to retrieve data from
	 * @param trackName track to get group from
	 * @return first group found within a track that the user has
	 */
	public static Optional<InheritanceNode> getPlayerFirstInheritanceNodeOnTrack(User user, String trackName) {
		return user.getNodes(NodeType.INHERITANCE)
				.stream()
				.filter(node -> isOnTrack(getGroup(node.getGroupName()), trackName))
				.findFirst();
	}

	/**
	 * 
	 * @param uniqueId  player uuid
	 * @param trackName the track to get the groups from
	 * @return player groups on a track
	 */
	public static Set<Group> getPlayerGroups(UUID uniqueId, String trackName) {
		return getPlayerGroups(USER_MANAGER.getUser(uniqueId), trackName);
	}

	/**
	 * 
	 * @param name      player name
	 * @param trackName the track to get the groups from
	 * @return player groups on a track
	 */
	public static Set<Group> getPlayerGroups(String name, String trackName) {
		return getPlayerGroups(USER_MANAGER.getUser(name), trackName);
	}

	/**
	 * 
	 * @param group the group to search for
	 * @return all tracks that has the specified group
	 */
	public static Set<Track> getTracksOfGroup(Group group) {
		Set<Track> tracks = TRACK_MANAGER.getLoadedTracks();
		Set<Track> foundTracks = tracks.stream()
				.filter(track -> track.containsGroup(group))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return foundTracks;
	}

	/**
	 * 
	 * @param groupName the group name to search for
	 * @return all tracks that has the specified group
	 */
	public static Set<Track> getTracksOfGroup(String groupName) {
		return getTracksOfGroup(getGroup(groupName));
	}

	/**
	 * 
	 * @param user to retrieve data from
	 * @return all player groups that aren't inserted into tracks
	 */
	public static Set<Group> getPlayerTracklessGroups(User user) {
		Set<Group> nodes = user.getNodes(NodeType.INHERITANCE)
				.stream()
				.map(NodeType.INHERITANCE::cast)
				.map(InheritanceNode::getGroupName)
				.filter(groupName -> !isOnTrack(groupName))
				.map(GROUP_MANAGER::getGroup)
				.collect(Collectors.toSet());
		return nodes;
	}

	/**
	 * 
	 * @param uniqueId player uuid
	 * @return all player groups that aren't inserted into tracks
	 */
	public static Set<Group> getPlayerTracklessGroups(UUID uniqueId) {
		return getPlayerTracklessGroups(getUser(uniqueId));
	}

	/**
	 * 
	 * @param group     the group to search for
	 * @param trackName track to search in
	 * @return whether the group is found within a track or not
	 */
	public static boolean isOnTrack(Group group, String trackName) {
		Track track = TRACK_MANAGER.getTrack(trackName);
		boolean findTrack = track.containsGroup(group);
		return findTrack;
	}

	/**
	 * 
	 * @param groupName the group name to search for
	 * @param trackName track to search in
	 * @return whether the group is found within a track or not
	 */
	public static boolean isOnTrack(String groupName, String trackName) {
		Track track = TRACK_MANAGER.getTrack(trackName);
		boolean findTrack = track.containsGroup(groupName);
		return findTrack;
	}

	/**
	 * 
	 * @param InheritanceNode the group node to search for
	 * @param trackName       track to search in
	 * @return whether the group is found within a track or not
	 */
	public static boolean isOnTrack(InheritanceNode inheritanceNode, String trackName) {
		Track track = TRACK_MANAGER.getTrack(trackName);
		boolean findTrack = track.containsGroup(inheritanceNode.getGroupName());
		return findTrack;
	}

	/**
	 * 
	 * @param group the group to search for
	 * @return whether the group is found within a track or not
	 */
	public static boolean isOnTrack(Group group) {
		Set<Track> tracks = TRACK_MANAGER.getLoadedTracks();
		boolean findTrack = tracks.stream().anyMatch(track -> track.containsGroup(group));
		return findTrack;
	}

	/**
	 * 
	 * @param groupName the group name to search for
	 * @return whether the group is found within a track or not
	 */
	public static boolean isOnTrack(String groupName) {
		Set<Track> tracks = TRACK_MANAGER.getLoadedTracks();
		boolean findTrack = tracks.stream().anyMatch(track -> track.containsGroup(groupName));
		return findTrack;
	}

	/**
	 * 
	 * @param inheritanceNode the group node to search for
	 * @return whether the group is found within a track or not
	 */
	public static boolean isOnTrack(InheritanceNode inheritanceNode) {
		Set<Track> tracks = TRACK_MANAGER.getLoadedTracks();
		boolean findTrack = tracks.stream().anyMatch(track -> track.containsGroup(inheritanceNode.getGroupName()));
		return findTrack;
	}

	/**
	 * @author Evan#6000
	 * @param user      the user that is obtained from UserManager
	 * @param trackName track name to get the group from
	 * @return the group with the highest weight that the player has, on a track
	 */
	public static Optional<Group> getPlayerHighestGroupOnTrack(User user, String trackName) {
		Track track = getTrack(trackName);
		SortedSet<Node> allNodes = user.getDistinctNodes();
		if (!allNodes.isEmpty()) {
			return allNodes.stream()
					.filter(NodeType.INHERITANCE::matches)
					.map(NodeType.INHERITANCE::cast)
					.map(InheritanceNode::getGroupName)
					.map(GROUP_MANAGER::getGroup)
					.filter(Objects::nonNull)
					.filter(track::containsGroup)
					.max(Comparator.comparingInt(g -> g.getWeight().orElse(0)));
		}
		return Optional.empty();
	}

	/**
	 * @author Evan#6000
	 * @param uniqueId  player uuid
	 * @param trackName track name to get the group from
	 * @return the group with the highest weight that the player has, on a track
	 */
	public static Optional<Group> getPlayerHighestGroupOnTrack(UUID uniqueId, String trackName) {
		return getPlayerHighestGroupOnTrack(getUser(uniqueId), trackName);
	}

	/**
	 * Clears all the previous groups and then changes player parent group to the
	 * specified group.
	 * 
	 * @param uniqueId player uuid
	 * @param group    the desired group
	 */
	public static CompletableFuture<Void> setPlayerGroup(UUID uniqueId, Group group) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data().clear(NodeType.INHERITANCE::matches);
			Node node = InheritanceNode.builder(group).build();
			user.data().add(node);
			user.setPrimaryGroup(group.getName());
		});
	}

	/**
	 * Gives the player the specified group
	 * 
	 * @param uniqueId player uuid
	 * @param group    the desired group
	 */
	public static CompletableFuture<Void> addPlayerGroup(UUID uniqueId, Group group) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Node node = InheritanceNode.builder(group).build();
			user.data().add(node);
		});
	}

	/**
	 * Changes player group on a specific server to the desired group
	 * after removing past group nodes that is included in the specified server
	 * 
	 * @param uniqueId   player uuid
	 * @param group      the desired group
	 * @param serverName server to change player group on
	 */
	public static CompletableFuture<Void> setPlayerServerGroup(UUID uniqueId, Group group, String serverName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data()
					.clear(NodeType.INHERITANCE
							.predicate(oldNode -> oldNode.getContexts().contains("server", serverName)));
			Node node = InheritanceNode.builder(group).withContext("server", serverName).build();
			user.data().add(node);
			user.setPrimaryGroup(group.getName());
		});
	}

	/**
	 * Changes player group with a specific context to the desired group
	 * after removing past group nodes that has the same context name
	 * 
	 * @param uniqueId     player uuid
	 * @param group        the desired group
	 * @param contextName  name of context to modify
	 * @param contextValue the value to set for the context
	 */
	public static CompletableFuture<Void> setPlayerContextGroup(UUID uniqueId, Group group, String contextName,
			String contextValue) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data()
					.clear(NodeType.INHERITANCE.predicate(oldNode -> oldNode.getContexts().containsKey(contextName)));
			Node node = InheritanceNode.builder(group).withContext(contextName, contextValue).build();
			user.data().add(node);
			user.setPrimaryGroup(group.getName());
		});
	}

	/**
	 * Gives the player the specified group with server context
	 * 
	 * @param uniqueId   player uuid
	 * @param group      the desired group
	 * @param serverName server to change player group on
	 */
	public static CompletableFuture<Void> addPlayerServerGroup(UUID uniqueId, Group group, String serverName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Node node = InheritanceNode.builder(group).withContext("server", serverName).build();
			user.data().add(node);
		});
	}

	/**
	 * Clears all of the player previous groups and then changes player parent group
	 * to the specified group.
	 * 
	 * @param uniqueId     player uuid
	 * @param group        the desired group
	 * @param ignoreTracks if true, ignores clearing groups that are found within
	 *                     tracks i.e clears all groups but groups within tracks
	 */
	public static CompletableFuture<Void> setPlayerGroup(UUID uniqueId, Group group, boolean ignoreTracks) {
		if (!ignoreTracks) return setPlayerGroup(uniqueId, group);
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data().clear(NodeType.INHERITANCE.predicate(trackless -> !isOnTrack(trackless.getGroupName())));
			Node node = InheritanceNode.builder(group).build();
			user.data().add(node);
			user.setPrimaryGroup(group.getName());
		});
	}

	/**
	 * Changes player group on a specific server to the desired group
	 * after removing past group nodes that is included in the specified server
	 * 
	 * @param uniqueId     player uuid
	 * @param group        the desired group
	 * @param serverName   server to change player group on
	 * @param ignoreTracks if true, ignores clearing groups that are found within
	 *                     tracks i.e clears all groups but groups within tracks
	 */
	public static CompletableFuture<Void> setPlayerServerGroup(UUID uniqueId, Group group, boolean ignoreTracks,
			String serverName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data()
					.clear(NodeType.INHERITANCE
							.predicate(oldNode -> oldNode.getContexts().contains("server", serverName))
							.and(node -> !isOnTrack(((InheritanceNode) node).getGroupName())));
			Node node = InheritanceNode.builder(group).withContext("server", serverName).build();
			user.data().add(node);
			user.setPrimaryGroup(group.getName());
		});
	}

	/**
	 * Changes player group to the desired group on a specfic track taken from
	 * trackName
	 * after removing past group nodes that is included in the specified track.
	 * 
	 * @param uniqueId  player uuid
	 * @param group     the desired group
	 * @param trackName track to change player group on
	 */
	public static CompletableFuture<Void> setPlayerGroup(UUID uniqueId, Group group, String trackName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data().clear(NodeType.INHERITANCE.predicate(foundNode -> isOnTrack(foundNode, trackName)));
			Node node = InheritanceNode.builder(group).build();
			user.data().add(node);
		});
	}

	/**
	 * Changes player group on a specific server to the desired group on a specfic
	 * track taken from trackName
	 * after removing past group nodes that is included in the specified track and
	 * has the serverName as a context.
	 * 
	 * @param uniqueId   player uuid
	 * @param group      the desired group
	 * @param trackName  track to change player group on
	 * @param serverName server to change player group on
	 */
	public static CompletableFuture<Void> setPlayerServerGroup(UUID uniqueId, Group group, String trackName,
			String serverName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data()
					.clear(NodeType.INHERITANCE.predicate(oldNode -> isOnTrack(oldNode, trackName))
							.and(oldNode -> oldNode.getContexts().contains("server", serverName)));
			Node node = InheritanceNode.builder(group).withContext("server", serverName).build();
			user.data().add(node);
		});
	}

	/**
	 * 
	 * @param uniqueId  player uuid
	 * @param trackName the track to remove player group from
	 */
	public static CompletableFuture<Void> deletePlayerGroups(UUID uniqueId, String trackName) {
		Track track = getTrack(trackName);
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data().clear(NodeType.INHERITANCE.predicate(node -> track.containsGroup(node.getGroupName())));
		});
	}

	/**
	 * @param uniqueId   player uuid
	 * @param trackName  the track to remove player group from
	 * @param serverName server to remove player group from
	 */
	public static CompletableFuture<Void> deletePlayerServerGroups(UUID uniqueId, String trackName, String serverName) {
		Track track = getTrack(trackName);
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data()
					.clear(NodeType.INHERITANCE.predicate(node -> track.containsGroup(node.getGroupName()))
							.and(node -> node.getContexts().contains("server", serverName)));
		});
	}

	/**
	 * 
	 * @param uniqueId   player uuid
	 * @param trackName  the track to remove player group from
	 * @param groupNames groupNames to check for that are inside the track
	 */
	public static CompletableFuture<Void> deletePlayerGroups(UUID uniqueId, String trackName,
			Collection<String> groupNames) {
		Track track = getTrack(trackName);
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Predicate<Node> condition = NodeType.INHERITANCE.predicate(node -> track.containsGroup(node.getGroupName()))
					.and(node -> groupNames.contains(((InheritanceNode) node).getGroupName()));
			user.data().clear(condition);
		});
	}

	/**
	 * @param uniqueId   player uuid
	 * @param trackName  the track to remove player group from
	 * @param groupNames groupNames to check for that are inside inside the track
	 *                   and has the serverName as a context
	 * @param serverName server to remove player group from
	 */
	public static CompletableFuture<Void> deletePlayerServerGroups(UUID uniqueId, String trackName,
			Collection<String> groupNames, String serverName) {
		Track track = getTrack(trackName);
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data()
					.clear(NodeType.INHERITANCE.predicate(node -> track.containsGroup(node.getGroupName()))
							.and(node -> node.getContexts().contains("server", serverName))
							.and(node -> groupNames.contains(((InheritanceNode) node).getGroupName())));
		});
	}

	/**
	 * @param uniqueId player uuid
	 */
	public static CompletableFuture<Void> deletePlayerGroups(UUID uniqueId) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data().clear(NodeType.INHERITANCE::matches);
		});
	}

	/**
	 * @param uniqueId   player uuid
	 * @param serverName server to remove player group from
	 */
	public static CompletableFuture<Void> deletePlayerServerGroups(UUID uniqueId, String serverName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data()
					.clear(NodeType.INHERITANCE.predicate(node -> node.getContexts().contains("server", serverName)));
		});
	}

	/**
	 * @param uniqueId     player uuid
	 * @param ignoreTracks ignores clearing player groups that are found within
	 *                     tracks
	 */
	public static CompletableFuture<Void> deletePlayerGroups(UUID uniqueId, boolean ignoreTracks) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data().clear(NodeType.INHERITANCE.predicate(trackless -> !isOnTrack(trackless.getGroupName())));
		});
	}

	/**
	 * @param uniqueId     player uuid
	 * @param ignoreTracks ignores clearing player groups that are found within
	 *                     tracks
	 * @param serverName   server to remove player group from
	 */
	public static CompletableFuture<Void> deletePlayerServerGroups(UUID uniqueId, boolean ignoreTracks,
			String serverName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data()
					.clear(NodeType.INHERITANCE.predicate(node -> !isOnTrack(node.getGroupName()))
							.and(node -> node.getContexts().contains("server", serverName)));
		});
	}

	/**
	 * 
	 * @param uniqueId  player uuid
	 * @param groupName groupName to remove
	 */
	public static CompletableFuture<Void> deletePlayerGroup(UUID uniqueId, String groupName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data().clear(NodeType.INHERITANCE.predicate(node -> node.getGroupName().equals(groupName)));
		});
	}

	/**
	 * 
	 * @param uniqueId   player uuid
	 * @param groupName  groupName to remove
	 * @param serverName server to remove player group from
	 */
	public static CompletableFuture<Void> deletePlayerServerGroup(UUID uniqueId, String groupName, String serverName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Collection<InheritanceNode> groupNodes = user.getNodes(NodeType.INHERITANCE);
			Node nodeToRemove = null;
			for (InheritanceNode node : groupNodes) {
				if (node.getContexts().contains("server", serverName) && node.getGroupName().equals(groupName)) {
					nodeToRemove = node;
					break;
				}
			}
			if (nodeToRemove != null) user.data().remove(nodeToRemove);
		});
	}

	/**
	 * 
	 * @param uniqueId  player uuid
	 * @param groupName groupName to search for
	 * @param trackName track to remove group from
	 */
	public static CompletableFuture<Void> deletePlayerGroup(UUID uniqueId, String groupName, String trackName) {
		Track track = getTrack(trackName);
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Collection<InheritanceNode> groupNodes = user.getNodes(NodeType.INHERITANCE);
			groupNodes.stream()
					.filter(node -> isOnTrack(node, track.getName()))
					.filter(node -> node.getGroupName().equals(groupName))
					.findFirst()
					.ifPresent(user.data()::remove);
		});
	}

	/**
	 * 
	 * @param uniqueId   player uuid
	 * @param groupName  groupName to remove
	 * @param serverName server to remove player group from
	 * @param trackName  track to remove group from
	 */
	public static CompletableFuture<Void> deletePlayerServerGroup(UUID uniqueId, String groupName, String trackName,
			String serverName) {
		Track track = getTrack(trackName);
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Collection<InheritanceNode> groupNodes = user.getNodes(NodeType.INHERITANCE);
			Node nodeToRemove = null;
			for (InheritanceNode node : groupNodes) {
				if (track.containsGroup(groupName) && node.getContexts().contains("server", serverName)
						&& node.getGroupName().equals(groupName)) {
					nodeToRemove = node;
					break;
				}
			}
			if (nodeToRemove != null) user.data().remove(nodeToRemove);
		});
	}

	/**
	 * @param group group to remove players from
	 */
	public static CompletableFuture<Void> removePlayersFromGroup(Group group) {
		NodeMatcher<InheritanceNode> nodeMatcher = NodeMatcher.key((InheritanceNode) group);
		return USER_MANAGER.searchAll(nodeMatcher)
				.thenAcceptAsync(map -> map.forEach(
						(uuid, node) -> USER_MANAGER.modifyUser(uuid, user -> user.data().remove((Node) node))));
	}

	/**
	 * Gives the specified permission to the given player
	 * 
	 * @param uniqueId   player uuid
	 * @param permission the permission to give
	 */
	public static CompletableFuture<Void> givePermission(UUID uniqueId, String permission) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Node node = PermissionNode.builder(permission).build();
			user.data().add(node);
		});
	}

	/**
	 * Gives the specified permission to the given player temporarily
	 * 
	 * @param uniqueId       player uuid
	 * @param permission     the permission to give
	 * @param expiryDuration how long will the permission stay
	 * @param timeUnit       the time unit the expiry duration is processed in
	 */
	public static CompletableFuture<Void> givePermission(UUID uniqueId, String permission, long expiryDuration,
			TimeUnit timeUnit) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Node node = PermissionNode.builder(permission).expiry(expiryDuration, timeUnit).build();
			user.data().add(node);
		});
	}

	/**
	 * Remove the specified permission from the given player
	 * 
	 * @param uniqueId   player uuid
	 * @param permission the permission to remove
	 */
	public static CompletableFuture<Void> removePermission(UUID uniqueId, String permission) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data().clear(NodeType.PERMISSION.predicate(node -> node.getPermission().equalsIgnoreCase(permission)));
		});
	}

	/**
	 * Gives the specified permission in a specific server to the given player
	 * 
	 * @param uniqueId   player uuid
	 * @param permission the permission to give
	 * @param serverName the name of the server to give the permission in
	 */
	public static CompletableFuture<Void> givePermission(UUID uniqueId, String permission, String serverName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Node node = PermissionNode.builder(permission).withContext("server", serverName).build();
			user.data().add(node);
		});
	}

	/**
	 * Gives the specified permission in a specific server to the given player
	 * temporarily
	 * 
	 * @param uniqueId       player uuid
	 * @param permission     the permission to give
	 * @param serverName     the name of the server to give the permission in
	 * @param expiryDuration how long will the permission stay
	 * @param timeUnit       the time unit the expiry duration is processed in
	 */
	public static CompletableFuture<Void> givePermission(UUID uniqueId, String permission, String serverName,
			long expiryDuration, TimeUnit timeUnit) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Node node = PermissionNode.builder(permission)
					.withContext("server", serverName)
					.expiry(expiryDuration, timeUnit)
					.build();
			user.data().add(node);
		});
	}

	/**
	 * Removes the specified permission in a specific server from the given player
	 * 
	 * @param uniqueId   player uuid
	 * @param permission the permission to remove
	 * @param serverName the name of the server to remove the permission in
	 */
	public static CompletableFuture<Void> removePermission(UUID uniqueId, String permission, String serverName) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data()
					.clear(NodeType.PERMISSION.predicate(node -> node.getPermission().equalsIgnoreCase(permission))
							.and(node -> node.getContexts().contains("server", serverName)));
		});
	}

	/**
	 * Gives the specified permission with a context to the given player
	 * 
	 * @param uniqueId     player uuid
	 * @param permission   the permission to give
	 * @param contextName  name of context to modify
	 * @param contextValue the value to set the context key to
	 */
	public static CompletableFuture<Void> givePermission(UUID uniqueId, String permission, String contextName,
			String contextValue) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Node node = PermissionNode.builder(permission).withContext(contextName, contextValue).build();
			user.data().add(node);
		});
	}

	/**
	 * Gives the specified permission with a to the given player temporarily
	 * 
	 * @param uniqueId       player uuid
	 * @param permission     the permission to give
	 * @param contextName    name of context to modify
	 * @param contextValue   the value to set the context key to
	 * @param expiryDuration how long will the permission stay
	 * @param timeUnit       the time unit the expiry duration is processed in
	 */
	public static CompletableFuture<Void> givePermission(UUID uniqueId, String permission, String contextName,
			String contextValue, long expiryDuration, TimeUnit timeUnit) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			Node node = PermissionNode.builder(permission)
					.withContext(contextName, contextValue)
					.expiry(expiryDuration, timeUnit)
					.build();
			user.data().add(node);
		});
	}

	/**
	 * Removes the specified permission with a context from the given player
	 * 
	 * @param uniqueId     player uuid
	 * @param permission   the permission to remove
	 * @param contextName  name of context to modify
	 * @param contextValue the value to set the context key to
	 */
	public static CompletableFuture<Void> removePermission(UUID uniqueId, String permission, String contextName,
			String contextValue) {
		return USER_MANAGER.modifyUser(uniqueId, (User user) -> {
			user.data()
					.clear(NodeType.PERMISSION.predicate(node -> node.getPermission().equalsIgnoreCase(permission))
							.and(node -> node.getContexts().contains(contextName, contextValue)));
		});
	}

}
