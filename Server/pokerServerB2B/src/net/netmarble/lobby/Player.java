package net.netmarble.lobby;

import game.obj.card.Card;

import java.util.ArrayList;

import javax.websocket.Session;

import net.netmarble.lobby.Room;

/*
 *  기존 코드 : LANPARTY 코드 
 *	 목적 : 플레이어
 *   추가 하는 일  : POKERPLAYER에 명시 
 *  	     
 */


abstract public class Player {
	
	public final static String DEFAULT_TYPE = "default_player";
	
	public static final int IN_LOBBY = -1;
	public static final int IN_ROOM = 0;
	
	protected String id = null;
	protected String roomId = null;
	
	protected int place = IN_LOBBY;
	
	abstract public void invite(Room room, Player host);
	
	public String getId() { return this.id; }
	public int getPlace() { return this.place; }
	public String getRoomId() { return this.roomId; }
	
	public void setPlace(int place) { this.place = place; }
	public void setRoomId(String roomId) { this.roomId = roomId; }
	
	public Player(String id) {
		this.id = id;
	}
	
	public abstract Session getSession();
	
	public abstract void newCard();
	public abstract void setCard(Card card);
	public abstract Card getCard(int index);
	public abstract void removeCard(Card card);	
	public abstract void ChangeCard(Card card);
	
	//Reset
	public abstract void ResetPlayer();
	
	
	//금액 관련 
	public abstract Integer getCash();
	public abstract void SetCash(int Cash);
	public abstract void AddCash(int TotalFee);
	
	public abstract void saveCash();	
	public abstract int getBeforeGameCash();
	public abstract void setRemains(int Remains);
	public abstract int getRemain();

	public abstract Integer Paid(int paid);
	public abstract int getPaid();
	public abstract void setPaid(int paid);
	public abstract int getTotalPaid();
	public abstract void AddTotalPaid(int TotalPaid);
	public abstract void setTurnPaid(int GameOrder , int Paid);
	public abstract int GetTurnPaid(int GameOrder);	
	
	//DOING 배팅의 종류 
	public abstract int getDoing();
	public abstract void setDoing(int Doing);
	
	//STATE
	public abstract boolean getSAllintate();
	public abstract void setAllinState(boolean What);
	public abstract boolean getDiedState();
	public abstract void setDiedState(boolean What);


	

}
