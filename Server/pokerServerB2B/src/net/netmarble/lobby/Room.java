package net.netmarble.lobby;

import game.obj.card.Deck;
import game.obj.card.Evaluater;
import game.obj.turn.TurnManger;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

import net.netmarble.lobby.result.Result;
/*
 *  기존 코드 : LANPARTY 코드 
 *	 목적 : 룸
 *   추가 하는 일  : POKERROOM에 명시 
 *  	     
 */
public abstract class Room {
	
	public final static String DEFAULT_TYPE = "default_room";
	
	public final static int DEFAULT_MAX_PLAYER = 4;
	public final static int DEFAULT_MIN_PLAYER = 0;
	
	public enum RoomState {
		WAITING_ROOM(-1, "Waiting Room"),
		PLAYING_ROOM(0, "Playing Room");
		
		private int state;
		private String message;
		
		public int getState() {
			return this.state;
		}
		
		public String getMessage() {
			return this.message;
		}
		
		private RoomState(int state, String message) {
			this.state = state;
			this.message = message;
		}
	}
	
	protected String id = null;
	protected String masterId = null;
	
	protected int maxPlayers = DEFAULT_MAX_PLAYER;
	protected int minPlayers = DEFAULT_MIN_PLAYER;
	
	protected RoomState state = RoomState.WAITING_ROOM;
	
	protected ArrayList<Player> participants = new ArrayList<>();
	

	public String getId() { return this.id; }
	public String getMasterId() { return this.masterId; }
	public RoomState getState() { return this.state; }
	public ArrayList<Player> getParticipants() { return this.participants; }
	
	public int getIndexParticipant(String playerId){
		int pivot = 0;
		for(int index  = 0; index < participants.size() ; index ++){
			if (participants.get(index).getId().equals(playerId)){
				pivot =index;
				break;
			}
		}
		return pivot;
	}
	
	public ArrayList<String> getPlayerId(int pivot){
		ArrayList<String> player = new ArrayList();
		System.out.println("getplayerIdpivot :" + pivot);

		int index = pivot;
		
		do{
				index++;
				if(index == participants.size())
					index =0;
				
				//Die유저는 제외한다. 
				System.out.println("getplayerIdIndex :" + index);

				if(participants.get(index).getDiedState() == false){
					player.add(participants.get(index).getId());
				}

				
		}while(index != pivot);
		
		System.out.println(player);
		return player;

	}
	
	public int getMaxPlayers() { return this.maxPlayers; }
	public int getMinPlayers() { return this.minPlayers; }
	
	public void setId(String id) { this.id = id; }
	public void setMasterId(String masterId) { this.masterId = masterId; }
	public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
	public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }
	public void setState(RoomState state) { this.state = state; }
	
	public boolean isPlaying() {
		if(state.equals(RoomState.PLAYING_ROOM)) return true;
		else return false;
	}

	public abstract Result join(Player player);
	public abstract Result leave(Player player) ;
	
	public abstract Result start(String hostId) ;
	public abstract Result finish() ;
	
	public Room(String roomId) {
		this.id = roomId;
	}
	
	// card
	public abstract Deck newDeck();
	public abstract Deck getDeck();

		
	//pokerTurn
	public abstract TurnManger getTurnManger();
	public abstract int getOrdeyByGame ();
	public abstract void SetOrdeyByGame(int order);
	public abstract boolean chkorder();
	public abstract void chkorderDisabe();
	
	//POKER 족보 계산
	public abstract Evaluater getEvaltor();
	
	//초기화
	public abstract void RoomReset();
	
	//동기화 문제
	public abstract Lock getLock();
	public abstract void unLock();
	public abstract void setTrueTurnValue();
	public abstract boolean chkBroadCast(String method);
	public abstract boolean chkIn(boolean Error, String Id);
	public abstract int ControlGamePlayerCount(String method);


	//TotalFee
	public abstract int getTotalFee();
	public abstract int AddTotalFee(int paid);
	public abstract int DistributeTotalfee(Player Winner);
	public abstract void decreaseTotalFee(int Fee);
	
	public abstract void ResetTurn();
	
	public abstract int getPreviousFee();
	public abstract void setPreviousFee(int paid);
		
	//레이즈 카운트 
	public abstract void RaseCountUp();
	public abstract boolean getRase();
	
	


}
