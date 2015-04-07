package ws.app.network;

import game.obj.card.Card;

import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.Session;

import net.netmarble.lobby.Lobby;
import net.netmarble.lobby.Player;
import net.netmarble.lobby.Room;
import net.netmarble.lobby.Room.RoomState;
import net.netmarble.lobby.factory.PlayerFactory;
import net.netmarble.lobby.factory.RoomFactory;
import net.netmarble.lobby.listener.LobbyUpdateListener;
import net.netmarble.lobby.manager.LobbyManager;
import net.netmarble.lobby.result.Result;
import net.netmarble.test.PokerPlayer;
import net.netmarble.test.PokerRoom;
import net.sf.json.JSONObject;
import Def.web.type.DEF_TYPE;

/*
 *   
 *	 목적 : 메세지 생성
 *   추가 하는 일  :  클라이언트에게 보낼 메세지를 생성한다.  
 *  	     
 */

public class EncodeJson {
	
	private static final AtomicInteger Room = new AtomicInteger(0);

	private static int caseNum = 0;
	
	private JSONObject json;
	private int State;
	public String userId;
	
	
	EncodeJson(){
		this.json = new JSONObject();
	}
	
	public int getState(){
		return State;
	}
	
	public void setState(int State){
		this.State = State;
	}
	
	public String toString(){
		return json.toString();
	}
	
	
	// parsing the Json 
	public String parsing(String msg , Session session , String ID){
  
		String pData = "";
		json = JSONObject.fromObject(msg);
    	
    	setState( Integer.parseInt( json.getString("type")) );
    	
    	switch( getState() ){
     	
			case DEF_TYPE.CONNECT:
				pData = Req_Info_MyID(msg  ,session, ID);	
				break;
    		case DEF_TYPE.REQ_INFO_START:
    			pData = Req_Info_Start(msg  ,session, ID);	
    			break;
    		case DEF_TYPE.REQ_MAKE_ROOM :
    			pData = Req_Make_Room(msg  ,session, ID);	
    			break;
    		case DEF_TYPE.REQ_JOIN_ROOM :
    			pData = Req_Join_Room(msg  ,session, ID);	
    			break;
    		case DEF_TYPE.REQ_GAME_START :
    			pData = Req_Game_Start(msg);
    			break;
    		case DEF_TYPE.REQ_GAME_TURN :
    			pData = Req_Game_Turn(msg);
    			break;
    		case DEF_TYPE.REQ_GAME_ALLC :
    			pData = Req_Game_Allc(msg);
    			break;
    		case DEF_TYPE.REQ_GAME_FIN :
       			pData = Req_Game_Fin(msg);
    			break;	
       		case DEF_TYPE.REQ_GAME_CHANGE  :
       			pData = Req_Game_Change(msg);
    			break;

    			
    	}
    	
    	
    	return pData;
	}
	
	
	public String Req_Info_MyID(String msg , Session session, String ID){
		
		userId =ID;
		
		return "";

	}
	
	
	// Start
	public String Req_Info_Start(String msg , Session session, String ID){
		
	
		int random = (int) (Math.random() * 10)+1;
		 
		String username = json.getString("username");
		String password = json.getString("password");
		
		
		//Lobby
		LobbyManager.getInstance().setLobbyUpdateListener(getLobbyUpdateListener());
		
		Player player = LobbyManager.getInstance().getPlayer(ID);
		//Player newPlayer = PlayerFactory.createPlayer(PokerPlayer.class, ID , session );
		if( player == null )
			player = PlayerFactory.createPlayer(PokerPlayer.class, ID , session , new Integer(100000 * random) );

		
		// connect Lobby
		LobbyManager.getInstance().connectLobby(player);
		
		
		return "waiting";
				
	}
	
	
	//make room 
	public String Req_Make_Room(String msg , Session session, String ID){
		
		String RoomID = "Room" + Room.getAndIncrement();
		
		System.out.println("ID: "+ ID +"\t[Creating Room...]");
		
		Room room = RoomFactory.createRoom(PokerRoom.class, RoomID );

		Player player = LobbyManager.getInstance().getPlayer(ID);
		
		
		LobbyManager.getInstance().createRoom(room, player.getId(), 2, 4);
		
		LobbyManager.getInstance().joinRoom(room.getId(), player.getId());

		return "MakeRoom";
				
	}
	
	//join room
	
	public String Req_Join_Room(String msg , Session session, String ID){
		
		
		String roomId = json.getString("roomId");	
		
		Player player = LobbyManager.getInstance().getPlayer(ID);
		
		
		//join the room
		Result result = LobbyManager.getInstance().joinRoom( roomId , player.getId());
		
		//성공이라면 보낸단.
		if(result.isSuccess() == true){
			System.out.println("ID: "+ ID +"\t[Join Room...]");
			return roomId;
		}
		else {
			System.out.println("Not join ... waiting");
			setState(DEF_TYPE.ACK_INFO_START);
			return "Error" ;
		}
		//성공이 아니라면 로비에 브로드 캐스트 시킨다. 자신을 포함시켜서 
		
		
				
	}
	
	public String Req_Game_Start(String msg){
		
		String roomId = json.getString("roomId");
		String hostId = json.getString("hostId");
		

		Result result =LobbyManager.getInstance().playGame( roomId , hostId );
		
		// start check
		if (result.isSuccess() == true){
			LobbyManager.getInstance().AllocateCard(roomId , 4);
			LobbyManager.getInstance().PaidAll(roomId);
		}
		else {
			// send Message Failed 
		}

		return roomId;
	}
	
	
	public String Req_Game_Turn(String msg){
		System.out.println("RTurn");

		String roomId = json.getString("roomId");
		String hostId = json.getString("hostId");
		
		int doing  = json.getInt("doing");
		
		Room targetRoom = LobbyManager.getInstance().getRoom(roomId);
		
		LobbyManager.getInstance().Batting(roomId ,hostId , doing  , false);
		
		//플레이어가 1명을 제외한 나머지가 다이라면 
		if(targetRoom.ControlGamePlayerCount("getGamePlayerCount") == 1){
			this.State = DEF_TYPE.ACK_GAME_FIN_DIE;
		}
		
		if(targetRoom.getOrdeyByGame() > 7){
			this.State = DEF_TYPE.ACK_GAME_SUB;

		}
		
		return roomId;
	}
	
	
	public String Req_Game_Allc(String msg){
		System.out.println("Rallc");

		String playerId = json.getString("playerId");
		String roomId = json.getString("RoomId");
		
		Room targetRoom = Lobby.getInstance().getRoom(roomId);

		//버린 카드를 다시 넣는다.
		if(targetRoom.chkIn(false,"")){
			int order = targetRoom.getOrdeyByGame();
			targetRoom.SetOrdeyByGame(order+1);
			targetRoom.chkorderDisabe();			
			LobbyManager.getInstance().AllocateCard(roomId, 1);		
		}

		return roomId;
	}
	
	
	public String Req_Game_Fin(String msg){
		
		String roomId = json.getString("RoomId");

		Room targetRoom = Lobby.getInstance().getRoom(roomId);

		//Room reset
		targetRoom.RoomReset();
		
		//player reset 
		for ( Player player:targetRoom.getParticipants()){
			player.ResetPlayer();
		}
		
		LobbyManager.getInstance().finishGame(roomId);

		return roomId;
	}
	
	
	public String Req_Game_Change(String msg){
		System.out.println("RFin");

		// 카드 정보를 받고 각 플레이어의 카드 정보를 수정한다.
		
		// 마지막 전달자가 재 정렬한 카드 정보 4장을 브로드 캐스트 한다. 
		String playerId = json.getString("playerId");
		String roomId = json.getString("RoomId");
				

		Room targetRoom = Lobby.getInstance().getRoom(roomId);
		Player player = Lobby.getInstance().getPlayer(playerId);


		Card opCard= new Card (json.getInt("OpCardS"), json.getInt("OpCardR"));
		Card tCard= new Card (json.getInt("TCardS"), json.getInt("TCardR"));
		

		player.removeCard(tCard);
		player.ChangeCard(opCard);
		
		
		// 총 게이머 숫자 만큼 들어온다면 
		if(targetRoom.chkIn( false , "")){

			LobbyManager.getInstance().pokerCalculate(roomId);
		}
		
		//만약에 모두 처리되었지만 , 사용자가 1명인영우 
		if(targetRoom.ControlGamePlayerCount("getGamePlayerCount") == 1){
			this.State = DEF_TYPE.ACK_GAME_FIN_DIE;
		}
		return roomId;
	}
	
	
	public static LobbyUpdateListener getLobbyUpdateListener() {
		return new LobbyUpdateListener() {

			@Override
			public void onConnectedToLobby(Result result, Player player) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDisconnectedToLobby(Result result, String id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onRoomCreated(Result result, Room room) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onRoomRemoved(Result result, String id) {
				System.out.println("ID: " + id + "\tSTATE: Room destroyed");
				
			}

			@Override
			public void onPlayerJoined(Result result, String userId, Room room) {
				//System.out.println(userId);
			}

			@Override
			public void onPlayerLeft(Result result, String playerId, Room room) {
				System.out.println("ID: "+ playerId +"\tSTATE:Player left");
				
			}

			@Override
			public void onGameStarted(Result result, RoomState state, Room room) {
				//System.out.println(">>Play Game Result : " + result.getMessage());
				//System.out.println(">> Room Info");
				//System.out.println("room id : " + room.getId());
				//System.out.println("host id : " + room.getMasterId());
				//System.out.println("min player : " + room.getMinPlayers());
				//System.out.println("max player : " + room.getMaxPlayers());
				//System.out.println("state : " + room.getState());
			}

			@Override
			public void onGameFinished(Result result, RoomState state, Room room) {
				//System.out.println(">>Finish Game Result : " + result.getMessage());
				//System.out.println(">> Room Info");
				//System.out.println("room id : " + room.getId());
				//System.out.println("host id : " + room.getMasterId());
				//System.out.println("min player : " + room.getMinPlayers());
				//System.out.println("max player : " + room.getMaxPlayers());
				//System.out.println("state : " + room.getState());
				
			}
		};
	}

}
