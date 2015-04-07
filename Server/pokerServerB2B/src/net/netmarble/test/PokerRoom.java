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
 *	 ���� : ��  ����   
 *   �߰� �ϴ� ��  :  ĳ�������Լ� 
 *   			����ȭ �Լ�
 *   			���� �� ����
 *   			�÷��̾� ���� ������ 
 *  	     
 */
public class PokerRoom extends Room {
	
	// card
	protected Deck deck;
	
	// turn
	protected TurnManger turnManager = new TurnManger();
	
	// �������
	public Evaluater evaltor = new Evaluater();
	
	//���� �÷��̾� Ë��
	protected int ordeyByGame = 3;
	
	//����ȭ
	protected boolean chkOrder = false;	
	protected int chkBroadCast = 0;
	protected int chkIn  = 0;
	protected Lock Turnlock  = new ReentrantLock();
	protected boolean Turnvalue = false;
	
	//Error
	protected String Waiting ="";
	protected int ErrorPlayerCount = 0;
	
	//���� �÷��̾� ���� 
	protected int GamePlayerCount = 0;
	
	//ĳ������
	protected int TotalFee = 0 ;
	protected int previousFee = 0;
	
	//������ ����
	protected int raseNum = 0;
	protected final int MaxRaseNumber = 3;
	
	
	public PokerRoom(String roomId) {
		super(roomId);
		// TODO Auto-generated constructor stub
	}
	
	//�ʱ�ȭ
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
	
	//������ ���� 
	public void RaseCountUp(){
		++raseNum;
	}
	
	public boolean getRase(){
		if(raseNum == MaxRaseNumber)
			return true;
		return false;
	}

	
	//ĳ�� ���� 
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
		
		//�ش�  ���ʿ��� ��� ����ش�.
		Winner.AddCash(TotalFee);
		this.TotalFee = 0;
		
		//������ ���ʰ� �����̶�� �Ѵٸ� �ش� ���� ������ ���� �����Ѵ�. 
		
		return TotalFee;
	}
	

	//����ȭ 
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
		// �÷��̾� ���ڸ�ŭ �����ߴٸ� ��
		
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
		// �׷��� �ʴٸ� �ϳ��� �ø��� ���� 
		
		return false;
	}
	
	synchronized public boolean chkIn(boolean Error , String MyId){
		// �÷��̾� ���ڸ�ŭ �����ߴٸ� ��

		
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

			
			//�뿡�� ���� �����Ѵ�. 
			LobbyManager.getInstance().leaveRoom(MyId);
			
			return false;
			
		}
		// �׷��� �ʴٸ� �ϳ��� �ø��� ���� 
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
