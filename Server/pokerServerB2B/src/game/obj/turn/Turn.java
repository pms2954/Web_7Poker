package game.obj.turn;

import game.obj.card.Card;
import game.obj.card.CardRank;
import net.netmarble.lobby.Player;

/*
 *	 ���� : �÷��̾� �� ����  
 *  �ϴ� ��  : �� ���� ���� 
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

