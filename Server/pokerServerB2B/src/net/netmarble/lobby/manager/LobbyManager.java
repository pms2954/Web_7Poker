
package net.netmarble.lobby.manager;

import game.obj.card.Card;
import game.obj.card.Deck;
import game.obj.card.Evaluate;

import java.util.ArrayList;
import java.util.List;

import net.netmarble.lobby.Lobby;
import net.netmarble.lobby.Player;
import net.netmarble.lobby.Room;
import net.netmarble.lobby.Room.RoomState;
import net.netmarble.lobby.listener.LobbyUpdateListener;
import net.netmarble.lobby.result.Result;
import Def.web.type.DEF_CASH;
import Def.web.type.DEF_TYPE;


/*
 *  ���� �ڵ� : LANPARTY �ڵ� 
 *	 ���� : LOBBY ���� �� ���ӷ��� ó��  
 *   �߰� �ϴ� ��  :  �κ� ����, �� ����, �� ����, �� ����  ���� 
 *   			ī�� ����
 *   			����
 *   			�� ���� �� �߰�
 *   			���� ���� 
 *  	     
 */


public final class LobbyManager {
	
	private LobbyUpdateListener listener = null;
	
	// For Lobby Update Listener
	public void setLobbyUpdateListener(LobbyUpdateListener listener) {
		this.listener = listener;
	}
	
	public LobbyUpdateListener getLobbyUpdateListener() {
		return this.listener;
	}
	
	
	
	// getter
	public Room getRoom(String roomId) {
		return Lobby.getInstance().getRoom(roomId);
	}
	
	public Room getRoomByMasterID(String masterId) {
		return Lobby.getInstance().getRoomByMasterID(masterId);
	}
	
	public ArrayList<Room> getRooms() {
		return Lobby.getInstance().getRooms();
	}
	
	public Player getPlayer(String playerID) {
		return Lobby.getInstance().getPlayer(playerID);
	}

	public List<Player> getLobbyPlayersList() {
		return Lobby.getInstance().getLobbyPlayersList();
	}	
	
	
	
	// action
	synchronized public Result connectLobby(Player player) {
		
		Result result = new Result(Result.OK, "success"); 
		Lobby.getInstance().connect(player);
		
		if(listener != null)
			listener.onConnectedToLobby(result, player);
		
		return result;
	}
	
	synchronized public Result disconnectLobby(String id) {
		
		Result result = new Result(Result.OK, "success");
		Lobby.getInstance().disconnect(id);
		
		if(listener != null)
			listener.onDisconnectedToLobby(result, id);
		
		return result;
	}
	
	//�����
	synchronized public Result createRoom(Room room, String hostId, int minPlayer, int maxPlayer) {
		
		Result result = new Result(Result.OK, "success");
		room.setMasterId(hostId);
		
		room.setMinPlayers(minPlayer);
		room.setMaxPlayers(maxPlayer);
		
		Lobby.getInstance().addRoom(room);
		
		if(listener != null)
			listener.onRoomCreated(result, room);
		
		return result;
	}
	//������
	synchronized public Result joinRoom(String roomId, String playerId) {
		
		Result result = null;

		// load the room
		Room targetRoom = Lobby.getInstance().getRoom(roomId);

		RoomState state = targetRoom.getState();

		List<Player> participants = targetRoom.getParticipants();

		
		Player player = Lobby.getInstance().getPlayer(playerId);

		if (player == null) {
			result = new Result(Result.FAIL, "no exist the player in lobby");	

		} else if (state.equals(RoomState.PLAYING_ROOM)) {

			result = new Result(Result.NOW_PLAYING, "now playing");
		} else {

			if (participants.size() >= targetRoom.getMaxPlayers()) {

				result = new Result(Result.ENOUGH_PLAYER, "enough player");
				
			} else if (participants.contains(player)) {

				result = new Result(Result.EXIST_PLAYER, "the player is already existed!");
			} else {
				player.setRoomId(targetRoom.getId());
				player.setPlace(Player.IN_ROOM);
				
				result = targetRoom.join(player);
				
				if(result == null)
					result = new Result(Result.FAIL, "Not yet assigned a result!!");
			}
		}

		if(result.getResponse() == Result.OK) {

				participants.add(player);
			
		}

		if( listener != null )
			listener.onPlayerJoined(result, player.getId(), targetRoom);

		return result;
	}
	// �� ������
	synchronized public Room leaveRoom(String playerId) {
		System.out.println("into leaveRoom");

		// load the room
		Player player = Lobby.getInstance().getPlayer(playerId);
		
		String roomId = player.getRoomId();
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		
		List<Player> participants = targetRoom.getParticipants();
		
		Result result = targetRoom.leave(player);
		
		if(result == null)
			result = new Result(Result.FAIL, "Not yet assigned a result!!");
		
		if (result.getResponse() == Result.OK) {
				//���� �÷��̾ �����̶�� 
				if(player.getRoomId().equals(targetRoom.getId())){
					
					String RoomId ="";
					//���� ������ �ٸ�������� �ѱ��. 
					for (Player play : participants){
						if(play != player){		
							RoomId = play.getId();
							targetRoom.setMasterId(RoomId);
							System.out.println("change room maker :" + RoomId);
							break;
						}
					}
					

				}
				
				//player.setRoomId(null);
				player.setPlace(Player.IN_LOBBY);
				participants.remove(player);
				System.out.println("remove player");
			
		}

		if (listener != null)
			listener.onPlayerLeft(result, player.getId(), targetRoom);

		if (participants.isEmpty()) {
			Lobby.getInstance().removeRoom(targetRoom);
			targetRoom = null;

			if (listener != null)
				listener.onRoomRemoved(result, roomId);
			
		}

		return targetRoom;
	}
	
	//�÷��� ����
	public Result playGame(String roomId, String hostId) {
		
		Result result = null;
		
		// load the room
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		List<Player> participants = targetRoom.getParticipants();

		Deck deck = targetRoom.newDeck();
		
		//���� �ݾ��� ������ �д�. 
		for (Player play: participants){
				play.saveCash();
		}
		
		targetRoom.ControlGamePlayerCount("setGamePlayerCount");
		
		
		if(participants.size() < targetRoom.getMaxPlayers()) {
			
			result = new Result(Result.NOT_ENOUGH_PLAYER, "not enough player");
			
		} else if (!targetRoom.getMasterId().equals(hostId)) {
			
			result = new Result(Result.NO_HOST_PLAYER, "no host player");
			
		} else {
			
			result = targetRoom.start(hostId);
			
			if(result == null)
				result = new Result(Result.FAIL, "Not yet assigned a result!!");
		}
		
		if(result.getResponse() == Result.OK) {
			targetRoom.setState(RoomState.PLAYING_ROOM);
		}
		
		if( listener != null )
			listener.onGameStarted(result, targetRoom.getState(), targetRoom);
		
		
		
		
		return result;
	}
	
	
	
	//ī�� ����
	public int AllocateCard (String roomId , int CNum ){
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		
		List<Player> participants = targetRoom.getParticipants();
		
		targetRoom.getTurnManger().resetFixedTurn();

		
		for(Player player : participants){	
			
			if(player.getDiedState() == false){
				for ( int index = 0 ; index < CNum ; index++){
	
					Card card = targetRoom.getDeck().drawFromDeck();
					// ������ ī������ �˷��ش�.
					card.setId(player.getId());
					// �÷��̾�� ī�带 �ش�.
					player.setCard(card);
					// turn�� �����Ѵ�.
					player.setPaid(0);
				}
			}
		}
		
		//ī�� ������ turn�� ����� �ش�.
		if(targetRoom.getOrdeyByGame() >= 4){
			openCard(roomId);
		}
		else{
			targetRoom.SetOrdeyByGame(4);
			targetRoom.chkorderDisabe();
		}
		
		return DEF_TYPE.ACK_GAME_TURN;
		
	}

	//���ýý���
	public int Batting (String roomId , String hostId , int Doing , boolean Error){
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		Player player = Lobby.getInstance().getPlayer(hostId);
		int  returnValue = 0;
		
		//LOCK
		targetRoom.getLock().lock();

		targetRoom.setTrueTurnValue();
		
		//���� ������ �� 
		if (Error == true){
			if(targetRoom.getTurnManger().playerExist(hostId) == true){
				//�������϶�  �ϰ� ť ������ ������� 
				targetRoom.getTurnManger().ToString();
				targetRoom.getTurnManger().DieQueue(player);
				targetRoom.getTurnManger().DieFixedTurn(player);
				targetRoom.getTurnManger().ToString();

			}
			else {
				//���� ���ʰ� �ƴ� ��� �ϸ� ���� 
				targetRoom.getTurnManger().DieFixedTurn(player);
				returnValue = DEF_TYPE.ACK_GAME_TURN;
			}
			
			
			if (targetRoom.ControlGamePlayerCount("downGamePlayerCount") == 1 )
				returnValue = Submit_GiveUp(roomId);

			LobbyManager.getInstance().leaveRoom(hostId);//player�� ���ش�.
			

		}
		else {
			// ���������� �ƴ� �������� �����϶� 
			System.out.println("Batting");
			
			player.setDoing(Doing);
			
			targetRoom.getTurnManger().ToString();
			
			// �÷��̾� �ش� �ൿ���� ���� ĳ�� �谨�� �ǽ��Ѵ�.
			int CallValue =targetRoom.getPreviousFee();
			
			int RasePaid = 0;
			
			//��� ���� Ȯ�� �α� 
			//System.out.println("before");
			//System.out.println("Doing :" + player.getDoing());
			//System.out.println("���ݾ� :" + player.getPaid());
			//System.out.println("�� �ѱݾ� :" + player.getTotalPaid());
			//System.out.println("Total :" + targetRoom.getTotalFee());
			//System.out.println("Call�ݾ� :" + targetRoom.getPreviousFee());
	
			switch(Doing){
				case DEF_CASH.BBING:
	
					player.Paid(DEF_CASH.BBINGVALUE);
					player.AddTotalPaid(DEF_CASH.BBINGVALUE);
					player.setTurnPaid(targetRoom.getOrdeyByGame()-5 , DEF_CASH.BBINGVALUE);
					player.setPaid(DEF_CASH.BBINGVALUE);
					targetRoom.setPreviousFee(DEF_CASH.BBINGVALUE);
					targetRoom.AddTotalFee(DEF_CASH.BBINGVALUE);
					break;
				case DEF_CASH.CALL:
					System.out.println("Call");
	
					// ���� �´� �ݾ��� ���ϰ� 
					// �������� ��밡 �̹� ���λ��¶�� 
					if(player.getSAllintate() == true){
						//nothing
					}
					else{
						int fee = 	CallValue -player.getPaid();
						player.Paid(fee );
						player.AddTotalPaid(fee);
						player.setTurnPaid(targetRoom.getOrdeyByGame()-5 , fee);
			
						player.setPaid(CallValue);
						targetRoom.AddTotalFee(fee);
					}
					break;
				// �׳� �Ѿ	
				case DEF_CASH.CHECK:
					System.out.println("Chk");
	
					break;
				//���� �ݾ� �ι� 
				case DEF_CASH.DADDANG:
					System.out.println("Ddang");
	
					RasePaid =  CallValue*2 -player.getPaid() ; 
					
					player.Paid(RasePaid);
					player.AddTotalPaid(RasePaid);
					player.setTurnPaid(targetRoom.getOrdeyByGame()-5 , RasePaid);
		
					
					player.setPaid(CallValue * 2);
					targetRoom.setPreviousFee(CallValue * 2);
					targetRoom.AddTotalFee(RasePaid);
		
					targetRoom.RaseCountUp();
					targetRoom.getTurnManger().putQueue(player);
					break;
					
				case DEF_CASH.DIE:
					System.out.println("Die");
	
					// ���� Ȱ��ȭ 
					player.setDiedState(true);
					
					//Turn���� ���ش�.
					targetRoom.getTurnManger().DieFixedTurn(player);
					
					//���� �Ѹ� ���� ���   
					if (targetRoom.ControlGamePlayerCount("downGamePlayerCount") == 1){
						Submit_GiveUp(roomId);
					}
					
					break;
					
				case DEF_CASH.HALF:
	
					RasePaid =  CallValue -player.getPaid() + targetRoom.getTotalFee()/2; 
					
					player.Paid( RasePaid);
					player.AddTotalPaid(RasePaid);
					player.setTurnPaid(targetRoom.getOrdeyByGame()-5 , RasePaid);
		
		
					player.setPaid(CallValue + targetRoom.getTotalFee()/2);
					targetRoom.setPreviousFee(CallValue +targetRoom.getTotalFee()/2);
					targetRoom.AddTotalFee(RasePaid);
					targetRoom.RaseCountUp();
					targetRoom.getTurnManger().putQueue(player);
		
					break;
					
				case DEF_CASH.ALLIN :
					System.out.println("Allin");
	
					player.setAllinState(true);
					int LastCash = 	player.getCash();
					
					// cash�� 0���� �����.
					player.Paid(LastCash);
					
					player.AddTotalPaid(LastCash);
					player.setTurnPaid(targetRoom.getOrdeyByGame()-5 , LastCash);
					
					if(LastCash > targetRoom.getPreviousFee()){
						targetRoom.setPreviousFee(LastCash);
						targetRoom.getTurnManger().putQueue(player);
					}
					
					targetRoom.AddTotalFee(LastCash);
					
					
					break;
				
			}

		}

		//ť�� ���̻��� ���� ���ٸ� 
		if(targetRoom.getTurnManger().isQueueEmpty()){
			System.out.println("Quee Empty");

			int order = targetRoom.getOrdeyByGame();
			targetRoom.SetOrdeyByGame(++order);
			
			//7�� �������� ī�带 �����Ѵ�.
			//7���� ��� �ѷ����� ���������� ���ڸ� ������.
			if (order <= 7)
				returnValue = AllocateCard(roomId , 1);
			else{ 	
				returnValue = Submit(roomId);
			
			}
					
		}

		//System.out.println("after");
		//System.out.println("Doing :" + player.getDoing());
		//System.out.println("���ݾ� :" + player.getPaid());
		//System.out.println("�� �ѱݾ� :" + player.getTotalPaid());
		//System.out.println("Total :" + targetRoom.getTotalFee());
		//System.out.println("Call�ݾ� :" + targetRoom.getPreviousFee());
		
		return returnValue;


		
		//AllocateCard�ÿ� �ʱ�ȭ 
			
	}
	
	//ó���� �ǵ��� ����. 
	public void PaidAll(String roomId){
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		
		List<Player> participants = targetRoom.getParticipants();

		// ��� ������ ���� ���� ����. 
		for (Player player : participants){
			targetRoom.AddTotalFee(player.Paid(DEF_CASH.BBINGVALUE));
		}
		
	}
	
	
	//��� ����ڰ� ���̸� ������ ��� , ������ ���� �����Ѵ�. 
	public int Submit_GiveUp(String roomId){
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		
		List<Player> participants = targetRoom.getParticipants();

		for (Player play : participants){
			//not me and not died
			if( !play.getDiedState()){
				targetRoom.DistributeTotalfee(play);
			}

		}
		
		return DEF_TYPE.ACK_GAME_FIN_DIE;
		
	}
	
	//���� ī��Ʈ�� ������ ��� ī�� ������ ����ϰ� �¸��ڸ� �����Ѵ�. 
	public int Submit(String roomId){
		System.out.println("submit");
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		
		List<Player> participants = targetRoom.getParticipants();

		for(Player play : participants){
			//�� ���� ���⸦ �ִ´�.
			if(play.getDiedState()== false){
				Evaluate eval = new Evaluate(play);
				eval.foundCard(targetRoom.getOrdeyByGame());
				targetRoom.getEvaltor().putPlayer(eval);
			}
		}
		
		//�� ���� ���� sort
		System.out.println("before All card sort");
		//targetRoom.getEvaltor().ToString();
		
		targetRoom.getEvaltor().Sort();
		System.out.println("after All card sort");

		//targetRoom.getEvaltor().ToString();
	
		//�¸��ڸ� �Ǻ� 
		String playerId = targetRoom.getEvaltor().getFirshIndex();
			
		//�ش� �÷��̾�� ���� �ش�. 
		Player player = Lobby.getInstance().getPlayer(playerId);
	
		if(player.getSAllintate() == false)
			targetRoom.DistributeTotalfee(player);
		

		//�ش��÷��̾ �����̶�� �ش� ������ ����ؼ� �й��Ѵ�.(������ ��� )
		else{

			for ( Player other :participants){
				//not me and not died
				if(other != player  && !other.getDiedState()){
					int Remains = other.getTotalPaid() - player.getTotalPaid();
					//���� �ݾ� 
					
					if (Remains >  0)
					{
						other.AddCash(Remains);
						// remains�����.
						other.setRemains(Remains);
						// ���� �ݾ� ����
						targetRoom.decreaseTotalFee(Remains);
					}
				}
			}
			//���� �ݾ� �־��ֱ� 
			targetRoom.DistributeTotalfee(player);
			
		}
		
		
		return DEF_TYPE.ACK_GAME_SUB;
	}
	
	//���� ī�� ��� 
	public void openCard (String roomId){
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		List<Player> participants = targetRoom.getParticipants();

		int GameOrder = targetRoom.getOrdeyByGame()-1;

		for(Player play : participants){
			//�� ���� ���⸦ �ִ´�.
			if(play.getDiedState()== false){
				Evaluate eval = new Evaluate(play);
				eval.foundOpenCard( targetRoom.getOrdeyByGame());
				targetRoom.getEvaltor().putPlayer(eval);

			}
		}
		//�� ���� ���� sort
		
		System.out.println("opnecard before sorting");
		targetRoom.getEvaltor().ToString();

		targetRoom.getEvaltor().Sort();

		System.out.println("openCard After sorting");
		targetRoom.getEvaltor().ToString();

		String playerId = targetRoom.getEvaltor().getPlayer().getId();

		targetRoom.getTurnManger().MakeFixedTurn(playerId);

		int pivot = targetRoom.getIndexParticipant(playerId);

		//�������� �ð�������� ��������
		ArrayList<String> TurnPlayerId = targetRoom.getPlayerId(pivot);
			
		//���࿡ ī�尡 �ʹ� 4�� �����̰ų� , ��ü���� 4���� ��쿡�� ť�� ���� ù���� �ϸ� �־��ش�.
		if (targetRoom.getOrdeyByGame() == 3 || targetRoom.getOrdeyByGame() == 4 ){
			targetRoom.getTurnManger().onlyoneQueue();

		}
		else{

			//�׿ܿ��� �� �־��ش�.
			//Queue�� �ֱ� 
			int order  =  TurnPlayerId.size() -1 ;
			
			for (int index  =0 ; index < order; index++){
				targetRoom.getTurnManger().MakeFixedTurn(TurnPlayerId.remove(0));
			}

			targetRoom.getTurnManger().initQueue();
			
		}
		
		targetRoom.getEvaltor().clear();
		
		System.out.println("findout Queue State");
		targetRoom.getTurnManger().ToString();

			
	}

	//���������� ���� �����Ѵ�. 
	public void pokerCalculate(String roomId){
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);		
		// ���� ī�� ���
		AllocateCard(roomId , 1);
	}
	
	//���� ���� 
	public Result finishGame(String roomId) {
		
		// load the room
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		Result result = targetRoom.finish();
		
		if(result == null)
			result = new Result(Result.FAIL, "Not yet assigned a result!!");
		
		if(result.getResponse() == Result.OK) {
			targetRoom.setState(RoomState.WAITING_ROOM);
		}
		
		if( listener != null )
			listener.onGameFinished(result, targetRoom.getState(), targetRoom);
		
		return result;
	}
	
	// Singleton, Room Manager
	private static LobbyManager mInst = null;
	
	public synchronized static LobbyManager getInstance() {
		if ( mInst == null ) {
			mInst = new LobbyManager();
		}
		
		return mInst;
	}
}
