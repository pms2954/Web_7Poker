package net.netmarble.lobby.listener;

import net.netmarble.lobby.Player;
import net.netmarble.lobby.Room;
import net.netmarble.lobby.Room.RoomState;
import net.netmarble.lobby.result.Result;

/*
 *  ���� �ڵ� : LANPARTY �ڵ� 
 *	 ���� : LOBBY���� �������̽�
 *   �߰� �ϴ� ��  :  �߰����� ���� 
 *  	     
 */
public interface LobbyUpdateListener {
	public abstract void onConnectedToLobby(Result result, Player player);
	public abstract void onDisconnectedToLobby(Result result, String id);
	public abstract void onRoomCreated(Result result, Room room);
	public abstract void onRoomRemoved(Result result, String id);
	public abstract void onPlayerJoined(Result result, String userId, Room room);
	public abstract void onPlayerLeft(Result result, String playerId, Room room);
	public abstract void onGameStarted(Result result, RoomState state, Room room);
	public abstract void onGameFinished(Result result, RoomState state, Room room);
}
