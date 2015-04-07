package game.obj.turn;

import game.obj.card.Card;
import game.obj.card.CardRank;
import net.netmarble.lobby.Player;

/*
 *	 목적 : 플레이어 턴 정보  
 *  하는 일  : 턴 정보 관리 
 *  	       
 */

public class Turn
{

	private String playerId;
	
	public Turn( String Id){
		this.playerId =Id;
	}
	
	public String getId(){
		return playerId;
	}
	
	
	public String ToString(){
		return this.playerId +"\n";
	}
	
}

