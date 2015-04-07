package net.netmarble.test;

import game.obj.card.Card;

import java.util.ArrayList;

import javax.websocket.Session;

import net.netmarble.lobby.Player;
import net.netmarble.lobby.Room;
import Def.web.type.DEF_CASH;
/*
 *   
 *	 목적 : 플레이어 수정   
 *   추가 하는 일  :  캐쉬관련함수 
 *   			동기화 함수
 *   			STATE함수 
 *  	     
 */
public class PokerPlayer extends Player {

    private Session session; // active connection info
   
    //게임 전 캐쉬
    private Integer OriginCash = 0;
    
    // 게임진행중 가지고 있는 돈 
    private Integer Cash = 0;
    
    //올인 발생시 차액 
    private Integer remains = 0;
    
    
    //올인 이나 다이시 스테이트 변경 
    private boolean AllinState = false;
    private boolean DiedState = false;
    
    //배팅 형태
    private int Doing = -1; 
    
    //턴 안에서 내가 가지고 있는 금액
    private int myPaid = 0;
       
    //턴마다의 금액 
    private int[] TurnPaid ;
    
    //내가 차감한 모든 금액
    private int myTotalPaid = 0;
    
    //카드 
    private ArrayList<Card> cards;

    public PokerPlayer(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

    public PokerPlayer(String id, Session session , Integer Cash) {
		super(id);
		this.session  = session;
		this.Cash = Cash;
		// TODO Auto-generated constructor stub
	}
    
	@Override
	public void invite(Room room, Player host) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Session getSession(){
		return this.session;
	}
	
	// state
	public boolean getSAllintate(){
		return AllinState;
	}
	
	public void setAllinState(boolean What){
		this.AllinState = What;
	}
	
	// state
	public boolean getDiedState(){
		return DiedState;
	}
	
	public void setDiedState(boolean What){
		this.DiedState = What;
	}
	
	//턴 별로 캐쉬정보 저장
	public void setTurnPaid(int GameOrder , int Paid){
		
		if(TurnPaid == null){
			TurnPaid = new int[3];
			TurnPaid[0] = 0;
			TurnPaid[1] = 0;
			TurnPaid[2] = 0;
			TurnPaid[GameOrder] +=Paid; 
		}
		else 
			TurnPaid[GameOrder] +=Paid; 

	}
	
	public int GetTurnPaid(int GameOrder){
		return TurnPaid[GameOrder]; 
	}
	
	public int getPaid(){
		return myPaid;
	}
	
	public void setPaid(int paid){
		this.myPaid = paid;
	}
	
	@Override
	public Integer Paid(int paid){		
		Cash -= paid;
		return paid;
	}
	
	public int getTotalPaid(){
		return this.myTotalPaid;
	}
	public void AddTotalPaid(int TotalPaid){
		this.myTotalPaid += TotalPaid ;
	}
	
	
	//Doing
	public int getDoing(){
		return this.Doing;
	}
	
	public void setDoing(int Doing){
		this.Doing = Doing;
	}
	
	
	//cash
	@Override
	public Integer getCash(){
		return Cash;
	}
	
	public void SetCash(int Cash){
		this.Cash = Cash;
	}
	
	public void AddCash(int TotalFee){
		this.Cash += TotalFee;
	}
	
	public void saveCash(){
		this.OriginCash = this.Cash;
	}
	
	public int getBeforeGameCash(){
		return this.OriginCash;
	}
	
	public void setRemains(int Remains){
		this.remains =Remains;
	}
	
	public int getRemain(){
		return remains;
	}
	


	//card
	@Override
	public void newCard(){
		cards = new ArrayList<Card>();
	}
	
	@Override
	public void setCard(Card card){
		if(cards == null){
			newCard();
			cards.add(card);
		}
		cards.add(card);
		
	}
	@Override
	public Card getCard(int index){
		return cards.get(index);
	}
	
	public int getPivotCard(Card card){
		for(int index =0; index < cards.size(); index++) 
			if(cards.get(index).getRank() == card.getRank()  && cards.get(index).getSuit() == card.getSuit() ){
				return index;
			}
		return -1;
				
	}
	
	public void removeCard(Card card){
		int index=getPivotCard(card);
		
		cards.remove(index);
		
	}

	
	public void ChangeCard(Card card){
			//지워주고 
			int index=getPivotCard(card);
			cards.remove(index);
			//다시 넣어준다.
			cards.add(card);
	}
	
	
	@Override
	public void ResetPlayer(){
		
		cards = null;
		AllinState = false;
		Doing = -1;
		
	    remains=0;
	    
	    AllinState = false;
	    DiedState = false;
	    
		myPaid = 0;   
	    TurnPaid =null ;
	    myTotalPaid = 0;
	    
	}
	
	


}
