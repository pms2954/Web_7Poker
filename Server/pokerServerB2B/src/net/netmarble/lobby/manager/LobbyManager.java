
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
 *  기존 코드 : LANPARTY 코드 
 *	 목적 : LOBBY 관리 및 게임로직 처리  
 *   추가 하는 일  :  로비 조인, 방 생성, 방 조인, 방 삭제  관리 
 *   			카드 배포
 *   			배팅
 *   			턴 생성 및 추가
 *   			승자 결정 
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
	
	//방생성
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
	//방조인
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
	// 방 나가기
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
				//만약 플레이어가 방장이라면 
				if(player.getRoomId().equals(targetRoom.getId())){
					
					String RoomId ="";
					//방장 권한을 다른사람에게 넘긴다. 
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
	
	//플레이 게임
	public Result playGame(String roomId, String hostId) {
		
		Result result = null;
		
		// load the room
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		List<Player> participants = targetRoom.getParticipants();

		Deck deck = targetRoom.newDeck();
		
		//이전 금액을 저장해 둔다. 
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
	
	
	
	//카드 배포
	public int AllocateCard (String roomId , int CNum ){
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		
		List<Player> participants = targetRoom.getParticipants();
		
		targetRoom.getTurnManger().resetFixedTurn();

		
		for(Player player : participants){	
			
			if(player.getDiedState() == false){
				for ( int index = 0 ; index < CNum ; index++){
	
					Card card = targetRoom.getDeck().drawFromDeck();
					// 누구의 카드인지 알려준다.
					card.setId(player.getId());
					// 플레이어에게 카드를 준다.
					player.setCard(card);
					// turn을 생성한다.
					player.setPaid(0);
				}
			}
		}
		
		//카드 배포후 turn을 만들어 준다.
		if(targetRoom.getOrdeyByGame() >= 4){
			openCard(roomId);
		}
		else{
			targetRoom.SetOrdeyByGame(4);
			targetRoom.chkorderDisabe();
		}
		
		return DEF_TYPE.ACK_GAME_TURN;
		
	}

	//배팅시스템
	public int Batting (String roomId , String hostId , int Doing , boolean Error){
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		Player player = Lobby.getInstance().getPlayer(hostId);
		int  returnValue = 0;
		
		//LOCK
		targetRoom.getLock().lock();

		targetRoom.setTrueTurnValue();
		
		//에러 로직일 때 
		if (Error == true){
			if(targetRoom.getTurnManger().playerExist(hostId) == true){
				//내차례일때  턴과 큐 순서를 모두제거 
				targetRoom.getTurnManger().ToString();
				targetRoom.getTurnManger().DieQueue(player);
				targetRoom.getTurnManger().DieFixedTurn(player);
				targetRoom.getTurnManger().ToString();

			}
			else {
				//나의 차례가 아닌 경우 턴만 제거 
				targetRoom.getTurnManger().DieFixedTurn(player);
				returnValue = DEF_TYPE.ACK_GAME_TURN;
			}
			
			
			if (targetRoom.ControlGamePlayerCount("downGamePlayerCount") == 1 )
				returnValue = Submit_GiveUp(roomId);

			LobbyManager.getInstance().leaveRoom(hostId);//player를 없앤다.
			

		}
		else {
			// 에러로직인 아닌 정상적인 접근일때 
			System.out.println("Batting");
			
			player.setDoing(Doing);
			
			targetRoom.getTurnManger().ToString();
			
			// 플레이어 해당 행동으로 인한 캐쉬 삭감을 실시한다.
			int CallValue =targetRoom.getPreviousFee();
			
			int RasePaid = 0;
			
			//계산 내역 확인 로그 
			//System.out.println("before");
			//System.out.println("Doing :" + player.getDoing());
			//System.out.println("낸금액 :" + player.getPaid());
			//System.out.println("낸 총금액 :" + player.getTotalPaid());
			//System.out.println("Total :" + targetRoom.getTotalFee());
			//System.out.println("Call금액 :" + targetRoom.getPreviousFee());
	
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
	
					// 이전 냈던 금액을 제하고 
					// 콜이지만 상대가 이미 올인상태라면 
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
				// 그냥 넘어감	
				case DEF_CASH.CHECK:
					System.out.println("Chk");
	
					break;
				//이전 금액 두배 
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
	
					// 다이 활성화 
					player.setDiedState(true);
					
					//Turn에서 없앤다.
					targetRoom.getTurnManger().DieFixedTurn(player);
					
					//오직 한명만 남는 경우   
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
					
					// cash를 0으로 만든다.
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

		//큐에 더이상의 턴이 없다면 
		if(targetRoom.getTurnManger().isQueueEmpty()){
			System.out.println("Quee Empty");

			int order = targetRoom.getOrdeyByGame();
			targetRoom.SetOrdeyByGame(++order);
			
			//7장 이전에는 카드를 배포한다.
			//7장이 모두 뿌려지면 마지막에는 승자를 가린다.
			if (order <= 7)
				returnValue = AllocateCard(roomId , 1);
			else{ 	
				returnValue = Submit(roomId);
			
			}
					
		}

		//System.out.println("after");
		//System.out.println("Doing :" + player.getDoing());
		//System.out.println("낸금액 :" + player.getPaid());
		//System.out.println("낸 총금액 :" + player.getTotalPaid());
		//System.out.println("Total :" + targetRoom.getTotalFee());
		//System.out.println("Call금액 :" + targetRoom.getPreviousFee());
		
		return returnValue;


		
		//AllocateCard시에 초기화 
			
	}
	
	//처음에 판돈을 낸다. 
	public void PaidAll(String roomId){
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		
		List<Player> participants = targetRoom.getParticipants();

		// 모든 참가자 들이 삥을 낸다. 
		for (Player player : participants){
			targetRoom.AddTotalFee(player.Paid(DEF_CASH.BBINGVALUE));
		}
		
	}
	
	
	//모든 사용자가 다이를 선택한 경우 , 게임을 강제 종료한다. 
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
	
	//게임 카운트가 끝나면 모든 카드 족보를 계산하고 승리자를 결정한다. 
	public int Submit(String roomId){
		System.out.println("submit");
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		
		List<Player> participants = targetRoom.getParticipants();

		for(Player play : participants){
			//각 족보 계산기를 넣는다.
			if(play.getDiedState()== false){
				Evaluate eval = new Evaluate(play);
				eval.foundCard(targetRoom.getOrdeyByGame());
				targetRoom.getEvaltor().putPlayer(eval);
			}
		}
		
		//각 넣은 것을 sort
		System.out.println("before All card sort");
		//targetRoom.getEvaltor().ToString();
		
		targetRoom.getEvaltor().Sort();
		System.out.println("after All card sort");

		//targetRoom.getEvaltor().ToString();
	
		//승리자를 판별 
		String playerId = targetRoom.getEvaltor().getFirshIndex();
			
		//해당 플레이어에게 돈을 준다. 
		Player player = Lobby.getInstance().getPlayer(playerId);
	
		if(player.getSAllintate() == false)
			targetRoom.DistributeTotalfee(player);
		

		//해당플레이어가 올인이라면 해당 내역을 계산해서 분배한다.(올인의 경우 )
		else{

			for ( Player other :participants){
				//not me and not died
				if(other != player  && !other.getDiedState()){
					int Remains = other.getTotalPaid() - player.getTotalPaid();
					//남은 금액 
					
					if (Remains >  0)
					{
						other.AddCash(Remains);
						// remains남긴다.
						other.setRemains(Remains);
						// 남은 금액 차감
						targetRoom.decreaseTotalFee(Remains);
					}
				}
			}
			//남은 금액 넣어주기 
			targetRoom.DistributeTotalfee(player);
			
		}
		
		
		return DEF_TYPE.ACK_GAME_SUB;
	}
	
	//모픈 카드 계산 
	public void openCard (String roomId){
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		List<Player> participants = targetRoom.getParticipants();

		int GameOrder = targetRoom.getOrdeyByGame()-1;

		for(Player play : participants){
			//각 족보 계산기를 넣는다.
			if(play.getDiedState()== false){
				Evaluate eval = new Evaluate(play);
				eval.foundOpenCard( targetRoom.getOrdeyByGame());
				targetRoom.getEvaltor().putPlayer(eval);

			}
		}
		//각 넣은 것을 sort
		
		System.out.println("opnecard before sorting");
		targetRoom.getEvaltor().ToString();

		targetRoom.getEvaltor().Sort();

		System.out.println("openCard After sorting");
		targetRoom.getEvaltor().ToString();

		String playerId = targetRoom.getEvaltor().getPlayer().getId();

		targetRoom.getTurnManger().MakeFixedTurn(playerId);

		int pivot = targetRoom.getIndexParticipant(playerId);

		//나머지를 시계방향으로 가져오기
		ArrayList<String> TurnPlayerId = targetRoom.getPlayerId(pivot);
			
		//만약에 카드가 초반 4장 배포이거나 , 교체이후 4장인 경우에는 큐에 제일 첫번쨰 턴만 넣어준다.
		if (targetRoom.getOrdeyByGame() == 3 || targetRoom.getOrdeyByGame() == 4 ){
			targetRoom.getTurnManger().onlyoneQueue();

		}
		else{

			//그외에는 다 넣어준다.
			//Queue에 넣기 
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

	//무조건으로 한장 배포한다. 
	public void pokerCalculate(String roomId){
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);		
		// 오픈 카드 계산
		AllocateCard(roomId , 1);
	}
	
	//게임 종료 
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
