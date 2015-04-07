package net.netmarble.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*
 *  기존 코드 : LANPARTY 코드 
 *	 목적 : 로비
 *  하는 일  : 플레이어 , 룸 관리
 *  	     
 */

public final class Lobby {
	
	private HashMap<String, Player> players = new HashMap<>(50);
	private HashMap<String, Room> rooms = new HashMap<>(50);

	public void connect(Player player) {
		players.put(player.getId(), player);
	}

	public void disconnect(String playerId) {
		players.remove(playerId);
	}
	
	public void addRoom(Room room) {
		rooms.put(room.getId(), room);
	}
	
	public void removeRoom(Room room) {
		rooms.remove(room.getId());
	}

	public Room getRoom(String roomId) {
		return rooms.get(roomId);
	}

	public Room getRoomByMasterID(String masterId) {
		ArrayList<Room> rooms = getRooms();

		for (Room room : rooms) {
			if (room.getMasterId().equals(masterId))
				return room;
		}

		return null;
	}

	public ArrayList<Room> getRooms() {
		ArrayList<Room> collectedRooms = new ArrayList<>();

		Iterator<String> iterator = rooms.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			collectedRooms.add(rooms.get(key));
		}

		return collectedRooms;
	}
	
	public Player getPlayer(String playerID) {
		return players.get(playerID);
	}

	
	public List<Player> getLobbyPlayersList() {
		ArrayList<Player> lobbyPlayers = new ArrayList<>();

		Iterator<String> iterator = players.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			if (players.get(key).getPlace() == Player.IN_LOBBY)
				lobbyPlayers.add(players.get(key));
		}

		return lobbyPlayers;
	}
	
	/**
	 * Singleton Class, Lobby
	 */
	private static Lobby mInst = null;
	
	public synchronized static Lobby getInstance() {
		
		if(mInst == null) {
			mInst = new Lobby();
		}
		
		return mInst;
	}
}
