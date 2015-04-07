package net.netmarble.test;

import game.obj.card.Deck;
import game.obj.card.Evaluater;
import game.obj.turn.TurnManger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.netmarble.lobby.Player;
import net.netmarble.lobby.Room;
import net.netmarble.lobby.manager.LobbyManager;
import net.netmarble.lobby.result.Result;
/*
 *   
 *	 목적 : 룸  수정   
 *   추가 하는 일  :  캐쉬관련함수 
 *   			동기화 함수
 *   			게임 턴 관리
 *   			플레이어 족보 계산관리 
 *  	     
 */
public class PokerRoom extends Room {
	
	// card
	protected Deck deck;
	
	// turn
	protected TurnManger turnManager = new TurnManger();
	
	// 족보계산
	public Evaluater evaltor = new Evaluater();
	
	//게임 플레이어 횏수
	protected int ordeyByGame = 3;
	
	//동기화
	protected boolean chkOrder = false;	
	protected int chkBroadCast = 0;
	protected int chkIn  = 0;
	protected Lock Turnlock  = new ReentrantLock();
	protected boolean Turnvalue = false;
	
	//Error
	protected String Waiting ="";
	protected int ErrorPlayerCount = 0;
	
	//게임 플레이어 숫자 
	protected int GamePlayerCount = 0;
	
	//캐쉬정보
	protected int TotalFee = 0 ;
	protected int previousFee = 0;
	
	//레이즈 정보
	protected int raseNum = 0;
	protected final int MaxRaseNumber = 3;
	
	
	public PokerRoom(String roomId) {
		super(roomId);
		// TODO Auto-generated constructor stub
	}
	
	//초기화
	public void ResetTurn(){
		raseNum = 0;
		previousFee = 0;
	}

	
	@Override
	public void RoomReset(){
		
		deck = null;
		ordeyByGame = 3; 
		chkOrder = false;
		
		turnManager = new TurnManger();
		evaltor = new Evaluater();
		TotalFee = 0 ;
		previousFee = 0;
		raseNum = 0;
		chkIn = 0;
		chkBroadCast =0;

	}
	
	//레이즈 관련 
	public void RaseCountUp(){
		++raseNum;
	}
	
	public boolean getRase(){
		if(raseNum == MaxRaseNumber)
			return true;
		return false;
	}

	
	//캐쉬 정보 
	public int getPreviousFee(){
		return previousFee;
	}
	
	public void setPreviousFee(int paid){
		this.previousFee = paid;
	}
	
	public int getTotalFee(){
		return TotalFee;
	}
	
	public void decreaseTotalFee(int Fee){
		this.TotalFee -= Fee;
	}
	
	public int AddTotalFee(int paid){
		TotalFee += paid;
		return TotalFee;
	}
	
	public int DistributeTotalfee(Player Winner){
		
		//해당  위너에게 모든 비용준다.
		Winner.AddCash(TotalFee);
		this.TotalFee = 0;
		
		//하지만 위너가 올인이라고 한다면 해당 올인 정보에 따라 배포한다. 
		
		return TotalFee;
	}
	

	//동기화 
	synchronized public Lock getLock(){
		return Turnlock;
	}
	
	synchronized public void unLock(){
		if(this.Turnvalue==true){
			Turnlock.unlock();
			Turnvalue= false;
		}
		
	}
	
	public void setTrueTurnValue(){
		this.Turnvalue = true;
	}

	
	synchronized public boolean chkBroadCast(String method){
		// 플레이어 숫자만큼 참조했다면 참
		
		if(method.equals("cast")){
		chkBroadCast++;
		if(chkBroadCast == ControlGamePlayerCount("getGamePlayerCount")){
			chkBroadCast = 0;
			return true;

		}
		}
		else if(method.equals("down")){
			chkBroadCast--;
		}
		// 그렇지 않다면 하나를 늘리고 실패 
		
		return false;
	}
	
	synchronized public boolean chkIn(boolean Error , String MyId){
		// 플레이어 숫자만큼 참조했다면 참

		
		chkIn++;
		
		if(chkIn == ControlGamePlayerCount("getGamePlayerCount") && !Error){
			System.out.println("full");

			chkIn = 0;
			return true;

		}
		else if(chkIn == ControlGamePlayerCount("getGamePlayerCount") && Error) {
			

			ControlGamePlayerCount("downGamePlayerCount");
			
			chkBroadCast("down");

			chkIn = 0;
			
			LobbyManager.getInstance().leaveRoom(MyId);
						
			return true;

		}
		else if(Error == true){

			chkIn--;
			
			ControlGamePlayerCount("downGamePlayerCount");

			
			//룸에서 나를 제거한다. 
			LobbyManager.getInstance().leaveRoom(MyId);
			
			return false;
			
		}
		// 그렇지 않다면 하나를 늘리고 실패 
		System.out.println("Defalut");

		return false;
	}
	
	synchronized public int ControlGamePlayerCount(String method){
		if (method.equals("downGamePlayerCount")){
			if(--GamePlayerCount == 1){
				return 1;
			}
			
			return GamePlayerCount;
		}
		else if (method.equals("getGamePlayerCount")){
			return GamePlayerCount;

		}
		else if(method.equals("setGamePlayerCount")){
			GamePlayerCount = this.participants.size();
		}
	
		
		return GamePlayerCount;
	}


	
	
	
	//GameOrder
	public int getOrdeyByGame (){
	 return ordeyByGame;
	}
	
	public void SetOrdeyByGame(int order){
		this.ordeyByGame = order;
		chkOrder = true;
	}
	
	public boolean chkorder(){
		return chkOrder;
	}
	
	public void chkorderDisabe(){
		chkOrder = false;
	}
	
	// card
	@Override
	public Deck newDeck(){ 
		deck = new Deck();
		return deck;
	}
	@Override
	public Deck getDeck(){
		return deck;
	}


	
	
	// turn 

	@Override
	public TurnManger getTurnManger(){
		return turnManager;
	}	

	@Override
	public Result join(Player player) {
		// TODO Auto-generated method stub
		return new Result(Result.OK, "success");
	}

	@Override
	public Result leave(Player player) {
		// TODO Auto-generated method stub
		return new Result(Result.OK, "success");
	}

	@Override
	public Result start(String hostId) {
		// TODO Auto-generated method stub
		return new Result(Result.OK, "success");
	}

	@Override
	public Result finish() {
		// TODO Auto-generated method stub
		return new Result(Result.OK, "success");
	}
	
	@Override
	public Evaluater getEvaltor(){
		return this.evaltor;
	}


	

}
