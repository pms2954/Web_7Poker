using UnityEngine;
using System.Collections;
using System.Collections.Generic;
/*
	클래스 목적 : PLAYER 관리 
	하는 일 :  -
 */
public class Player  {
	
	private string playerId;
	private string playerState;

	private int doing = -1 ;
	private int Totalfee = 0;
	private int CureentFee = 0;

	private int playerCash;
	private int beforeCash;
	private int remains;

	private Card[] card;
	
	public Player(string id , string state , int cash ){

		this.playerId = id;
		this.playerState = null;
		this.playerCash = cash;
		this.card = new Card[7];
		beforeCash = 0;
		remains = 0;

	}

	public void resetForTurn(){
		doing = -1 ;
		CureentFee = 0;
	}

	public void reset(){
		playerState = "";
		doing = -1 ;
		CureentFee = 0;
		beforeCash = 0;
		remains = 0;
		card = new Card[7];
	}


	public void setDoing(int Doing){
		this.doing = Doing;
	}

	public int getDoing(){
		return doing;
	}
	//Fee
	public void AddTotalFee(int TotalFee){
		Totalfee= TotalFee;
	}
	
	public int getTotalFee(){
		return Totalfee;
	}

	public void setCurrentFee(int Fee){
		this.CureentFee = Fee;
	}
	
	public int getCurrentFee(){
		return CureentFee;
	}
	


	//Cash
	public int printCash(){
		return playerCash;

	}
	public void setCash(int Cash){
		playerCash = Cash;
	}

	public void setBeforeCash(int beforecash){
		this.beforeCash = beforecash;
	}
	public void setReamains(int remains){
		this.remains = remains;
	}

	public int getMyprofit(){
		return  this.playerCash - this.beforeCash;
	}

	public bool remainexist(){
		if (this.remains > 0) {
			return true;
		}
		return false;
	}



	//ID and STATE
	public string printId(){
		return playerId;
	}
	
	public string printState(){
		return playerState;
	}
	

	public void setId(string Id){
		playerId = Id;
	}

	public void setPlayState(string State){
		playerState = State;
	}


	//CArd
	public void setCard(int index , Card card){
		this.card [index] = card;
	}
	
	public Card getCard(int index){
		if (card [index] == null)
			return null;

		return card [index];
	}

}

/*
	클래스 목적 : CARD 객체  관리 
	하는 일 :  -
 */

public class Card  {

	public int CardSuit ;
	public int CardRank ;
	
	public Card(int CardSuit, int  CardRank){
		this.CardSuit = CardSuit;
		this.CardRank = CardRank;
	}



	 
}
