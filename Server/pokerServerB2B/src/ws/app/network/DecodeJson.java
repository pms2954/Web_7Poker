package ws.app.network;

import game.obj.card.Card;

import java.util.List;

import net.netmarble.lobby.Lobby;
import net.netmarble.lobby.Player;
import net.netmarble.lobby.Room;
import net.netmarble.lobby.manager.LobbyManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import Def.web.type.DEF_TYPE;
/*
 *   
 *	 목적 : 메세지 파싱   
 *   추가 하는 일  :  파싱된 메세지를 적합한 함수 호출할수 있도록 조정  
 *  	     
 */
public class DecodeJson{
 	
	
	
	public static String allocate(int State, String ID, String pData){
		String msg = "";

		switch (State)
		{
			case DEF_TYPE.ERROR :
				msg = Ack_Error();
			case DEF_TYPE.CONNECT :
				msg =Ack(ID);
				break;
			case DEF_TYPE.REQ_INFO_START : 
				msg = Ack_Info_Start();
				break;
			case DEF_TYPE.REQ_MAKE_ROOM : 
				msg = Ack_Make_Room(ID );
				break;
			case DEF_TYPE.REQ_JOIN_ROOM : 
				msg = Ack_Join_Room(ID , pData);
				break;
			
			//Gaming 
			case DEF_TYPE.REQ_GAME_START : 
				msg = Ack_Game_Start(ID , pData);
				break;
			case DEF_TYPE.REQ_GAME_TURN : 
				msg = Ack_Game_Turn(ID , pData);
				break;
				
			case DEF_TYPE.ACK_GAME_ALLC : 
				msg = Ack_Game_Allc(ID , pData);
				break;			
			case DEF_TYPE.ACK_GAME_SUB : 
				msg = Ack_Game_Sub(ID , pData);
				break;
			case DEF_TYPE.ACK_GAME_FIN :
				msg = Ack_Join_Room(ID , pData);
				break;
			case DEF_TYPE.ACK_GAME_FIN_DIE :
				msg = Ack_Game_Sub_Die(ID , pData);
				break;

			case DEF_TYPE.ACK_GAME_CHANGE :
				msg = Ack_Game_Change(ID , pData);
				break;

			//Error
       		case DEF_TYPE.ACk_lEAVE_Room :
       			msg = Ack_Leave_Room(ID);
    			break;			
				
				
				


		}
		
		return msg;
	}
	
	
	public static String Ack_Error(){
		JSONObject json =  new JSONObject();
		
		//json.put("type","Error");
		//json.put("leavePlayerId", userId);
		return json.toString();

	}
	
	/* decode json 
     * expected client info using json
     * {
     * 	  "type" : " connect"
     * }
     */
	public static String Ack(String Id){
		JSONObject json =  new JSONObject();
		
		json.put("type","connect");
		json.put("MyId", Id );

		
		return json.toString();
			
	}
	
	/* decode json
	*	{“type”: “Req_Info_start”, 
	*	 “client”: [list client]
	*	 “Room”:  [list Room]
	*	}
	*
	*/
	public static String Ack_Info_Start(){
		
		// type
		JSONObject json =  new JSONObject();
		
		json.put("type", DEF_TYPE.ACK_INFO_START );

		// player Array
		JSONArray jsonA = new JSONArray();
		
		// Plater list
		JSONObject cJson = new JSONObject();
		
		List<Player> players = Lobby.getInstance().getLobbyPlayersList();
		
		
		for(Player player : players) {
			
			cJson.put( "ID" , player.getId() );
			cJson.put( "STATE" , "NoN");
			jsonA.add(cJson);
			
		}
		
		json.put("Player" , jsonA);
		
		
		//초기화 
		cJson.clear();
		jsonA.clear();
		
		List<Room> rooms = Lobby.getInstance().getRooms();
		
		for(Room room : rooms){	
			cJson.put( "ID" , room.getId() );			
			cJson.put( "STATE" , room.getState());
			if (room.getParticipants().size() >= room.getMaxPlayers()) {
				cJson.put( "Full" , "true" );
			}
			jsonA.add(cJson);
		}
		
		json.put("Room" , jsonA);
		//Room list 
		
		return json.toString();
	}
	
	/*
	* { “type” “ : “ACK_MAKE_ROOM” 
	*	“RoomInfo”  : RoomName
	*	“RoomState” : State
	*	“Player“ : [ “id” : id  , “Cash”: cash ]
	* }
	*/	

	
	public static String  Ack_Make_Room(String ID){
		System.out.println("ACk_MAKE_ROOM");

		Player play = LobbyManager.getInstance().getPlayer(ID);
		
		Room room = LobbyManager.getInstance().getRoom(play.getRoomId());
		
		JSONObject json =  new JSONObject();

		if(room !=  null){
			
			json.put( "type", "2");
			json.put( "roomId", room.getId() );
			json.put( "roomMasterId", room.getMasterId());
			json.put( "Cash", play.getCash());
			json.put( "roomState", room.getState());
		}
		
		// RoomMaker
		//Player player = Lobby.getInstance().getPlayer( ID );
		
		//json.put( "RoomMakerId" , player.getId() );
		//json.put( "Cash" , player.getCash() );		
				

		return json.toString();
	}
	
	
	
	
	
	public static String  Ack_Leave_Room(String ID ){
		
		
		Player play = LobbyManager.getInstance().getPlayer(ID);
		
		Room room = LobbyManager.getInstance().getRoom(play.getRoomId());
		
		JSONObject json =  new JSONObject();

		json.put( "type", "20");

		//participant Info
		
		json.put("leavePlayer", ID);
		
		json.put("roomMasterId", room.getMasterId());
		
		/*
		if( room.ControlGamePlayerCount("getGamePlayerCount") ==1 ){
			json.put("Restart", "true");
		}
		*/
		play.setRoomId(null);
		
		room.unLock();
		
		return json.toString();
		

	}
	
	
	public static String  Ack_Join_Room(String ID , String pData){
	
		Room room = LobbyManager.getInstance().getRoom(pData);
		
		JSONObject json =  new JSONObject();

		json.put( "type", "3");

		//participant Info
		
		json.put( "gaemOrder", room.getOrdeyByGame() );
		json.put( "MasterId", room.getMasterId());

		JSONArray jsonA = new JSONArray();
		
		JSONObject cJson = new JSONObject();
		
		for(Player play : room.getParticipants()) {
			cJson.put("Player" , play.getId());
			cJson.put ("Cash",   play.getCash());
			jsonA.add(cJson);
		}

		json.put("PlayerList" , jsonA);
		
		return json.toString();
		

	}
	
	public static String Ack_Game_Start(String ID , String pData){
		System.out.println("Start");

		Room room = LobbyManager.getInstance().getRoom(pData);

		JSONObject json =  new JSONObject();

		json.put( "type", "4");
		
		json.put("TotalFee", room.getTotalFee());
		//participant Info
		
		JSONArray jsonA = new JSONArray();
		
		JSONObject cJson = new JSONObject();

		//4장 배포 
		for(Player play : room.getParticipants()) {
			cJson.put("Player" , play.getId());
			cJson.put ("Cash",   play.getCash());
			for (int index = 1 ; index < 5 ; index++){
				Card card = play.getCard(index);
				cJson.put( "CardRank"+index, card.getRank());
				cJson.put( "CardSuit"+index, card.getSuit());
			}
			jsonA.add(cJson);
		}

		json.put("PlayerList" , jsonA);

		return json.toString();

	}
	
	
	public static String Ack_Game_Allc(String ID , String pData){
		System.out.println("Allc");
		Room room = LobbyManager.getInstance().getRoom(pData);
		
		JSONObject json =  new JSONObject();

		if(room.chkBroadCast("cast")){
	
			String TurnId = room.getTurnManger().removeQueue().getId();

			json.put("type","7");
			
			json.put("TotalFee", room.getTotalFee());
			
			json.put("GameOrder", room.getOrdeyByGame());
			
			json.put( "turn", TurnId); 
			
			//participant Info
			
			JSONArray jsonA = new JSONArray();
			
			JSONObject cJson = new JSONObject();
	
			
			for(Player play : room.getParticipants()) {
				cJson.put("Player" , play.getId());
				cJson.put ("Cash",   play.getCash());
				for (int index = room.getOrdeyByGame() ; index < room.getOrdeyByGame()+1 ; index++){
					Card card = play.getCard(index);
					cJson.put( "CardRank"+index, card.getRank());
					cJson.put( "CardSuit"+index, card.getSuit());
				}
				jsonA.add(cJson);
			}
	
			json.put("PlayerList" , jsonA);
	
			return json.toString();
		}
		
		return null;
		
	}
	
	public static String Ack_Game_Turn(String ID , String pData){
		System.out.println("Turn");
		
		Room room = LobbyManager.getInstance().getRoom(pData);
		
		Player player = LobbyManager.getInstance().getPlayer(ID);
		
		
		JSONObject json =  new JSONObject();
		
	
		if( room.chkorder() == false ){
			
			String TurnId = room.getTurnManger().removeQueue().getId();

			Player turnPlayer = LobbyManager.getInstance().getPlayer(TurnId);

			json.put( "type", "5");
			
			json.put( "turn", TurnId); 
			
			json.put("CallValue", room.getPreviousFee());
			
			json.put("TotalFee", room.getTotalFee());
			

			if (room.getRase() == true){
				json.put("CanDoing", "Restrict");
				}
			else{
				json.put("CanDoing", "Default");
			}
			
			json.put("playerId", ID);
			
			json.put("doing",  player.getDoing());
			
			if ( player.getSAllintate() == true){
				json.put("State" , "Allin");
			}
			else if(player.getDiedState() == true){
				json.put("State" , "Died");
			}
			
			json.put("Fee", player.getPaid());

			json.put("TotalPaid", player.getTotalPaid());
			
			json.put("Cash", player.getCash());

			//total fee 
			room.unLock();
		
		}
		// turn 종료되어 카드를 배포할때 
		else {
			
			String TurnId = room.getTurnManger().removeQueue().getId();

			json.put( "type", "5");
			
			json.put("subType", "CardAllc");
	
			json.put("turn", TurnId);
			
			json.put("gaemOrder", room.getOrdeyByGame());	
			
			//이전 doing 정보 
			json.put("playerId", ID);
			
			json.put("doing",  player.getDoing());
						
			json.put("TotalPaid", player.getTotalPaid());
			
			json.put("Cash", player.getCash());
			
			if ( player.getSAllintate() == true){
				json.put("State" , "Allin");
			}
			else if(player.getDiedState() == true){
				json.put("State" , "Died");
			}

			JSONArray jsonA = new JSONArray();
			
			JSONObject cJson = new JSONObject();


			for(Player play : room.getParticipants()) {
				cJson.put("Player" , play.getId());
				cJson.put ("Cash",   play.getCash());

				int index = room.getOrdeyByGame() ;

				if(play.getDiedState() == false){

					Card card = play.getCard(index);

					cJson.put( "CardRank"+index, card.getRank());
					cJson.put( "CardSuit"+index, card.getSuit());



				}

				jsonA.add(cJson);
				cJson.put( "CardRank"+index, -1);
				cJson.put( "CardSuit"+index, -1);


			}

			json.put("PlayerList" , jsonA);

			
			//턴이 종료 , 턴 정보를 리셋한다. 
			room.chkorderDisabe();
			room.ResetTurn();
			//room.getTurnManger().resetFixedTurn();
			System.out.println(room.getLock().toString());
			room.unLock();

		}

		return json.toString();

	}
	
	/*
	public static String Ack_Game_Open(String ID , String pData){
		
		System.out.println("Open");

		Room room = LobbyManager.getInstance().getRoom(pData);
		
		JSONObject json =  new JSONObject();
		
		//나머지 쓰레는 별일 안한다.
		
		if(room.chkBroadCast()){
			json.put("type", "5");
			json.put("turn", room.getTurnManger().removeQueue().getId());
			json.put("first" ,"true");
			json.put("CallValue", room.getPreviousFee());
			json.put("TotalFee", room.getTotalFee());
			
			
			return json.toString();

		}
		
		return null;
	
	}
	*/
	
	
	public static String Ack_Game_Sub(String ID , String pData){
		System.out.println("Sub");

		Room room = LobbyManager.getInstance().getRoom(pData);
		
		JSONObject json =  new JSONObject();
		
		// turn 이 있을때
		
					
			//족보로 승리 
			String Id = room.getEvaltor().getPlayer().getId();

			Player player = LobbyManager.getInstance().getPlayer(ID);

			json.put("type", "11");
			
			json.put("winner" , Id);
			
			//player
			//이전 doing 정보 
			json.put("playerId", ID);
			
			json.put("doing",  player.getDoing());
						
			json.put("TotalPaid", player.getTotalPaid());
			
			json.put("Cash", player.getCash());
			
			if ( player.getSAllintate() == true){
				json.put("State" , "Allin");
			}
			else if(player.getDiedState() == true){
				json.put("State" , "Died");
			}


			JSONArray jsonA = new JSONArray();
			
			JSONObject cJson = new JSONObject();

			
			for(Player play : room.getParticipants()) {
				cJson.put("Player" , play.getId());
				cJson.put ("Cash" ,  play.getCash());
				cJson.put( "beforeCash" , play.getBeforeGameCash());
				cJson.put( "remains" , play.getRemain());
				jsonA.add(cJson);

			}

			json.put("PlayerList" , jsonA);			
			
			room.unLock();

			//턴이 종료 , 턴 정보를 리셋한다. 
			return json.toString();
		
		
	}
	
	public static String Ack_Game_Sub_Die(String ID , String pData){
		System.out.println("Sub_die");

		Room room = LobbyManager.getInstance().getRoom(pData);
		
		Player player = LobbyManager.getInstance().getPlayer(ID);

		
		JSONObject json =  new JSONObject();
		
		json.put("type", "11");

		
		//이전 doing 정보 
		json.put("playerId", ID);
		
		json.put("doing",  player.getDoing());
					
		json.put("TotalPaid", player.getTotalPaid());
		
		json.put("Cash", player.getCash());
		
		if ( player.getSAllintate() == true){
			json.put("State" , "Allin");
		}
		else if(player.getDiedState() == true){
			json.put("State" , "Died");
		}
		
		JSONArray jsonA = new JSONArray();
		
		JSONObject cJson = new JSONObject();

		
		for(Player play : room.getParticipants()) {
			if(play.getDiedState() == false)
				json.put("winner", play.getId());
	
			
			cJson.put("Player" , play.getId());
			cJson.put ("Cash" ,  play.getCash());
			cJson.put( "beforeCash" , play.getBeforeGameCash());
			cJson.put( "remains" , play.getRemain());
			jsonA.add(cJson);

		}

		json.put("PlayerList" , jsonA);			
		//턴이 종료 , 턴 정보를 리셋한다. 
		room.unLock();

		return json.toString();

		
	}
	
	public static String Ack_Game_Change(String ID , String pData){
		System.out.println("Change");

		Room room = LobbyManager.getInstance().getRoom(pData);
		
		Player player = LobbyManager.getInstance().getPlayer(ID);
		

		JSONObject json =  new JSONObject();

		if(room.chkBroadCast("cast")){
			json.put( "type", "15");
			
			json.put( "Turn", room.getTurnManger().removeQueue().getId());
			json.put ("GameOrder" , room.getOrdeyByGame());
			//participant Info
			
			JSONArray jsonA = new JSONArray();
			
			JSONObject cJson = new JSONObject();
	
			//모두 4장 배포 정보를 뿌린다. 
			for(Player play : room.getParticipants()) {
				cJson.put("Player" , play.getId());
				cJson.put ("Cash",   play.getCash());
				for (int index = 1 ; index < 5 ; index++){
					Card card = play.getCard(index);
					cJson.put( "CardRank"+index, card.getRank());
					cJson.put( "CardSuit"+index, card.getSuit());
				}
				jsonA.add(cJson);
			}
	
			json.put("PlayerList" , jsonA);

		return json.toString();
		}
		else {
			json.put( "type", "15");
			
			json.put( "subType", "choice");

			//바뀐 유저의 카드 정보를 뿌린다.
			json.put("Player" , ID);
			for (int index = 1 ; index < 4 ; index++){
				Card card = player.getCard(index);
				json.put( "CardRank"+index, card.getRank());
				json.put( "CardSuit"+index, card.getSuit());
			}
			
			return json.toString();
		}

	}
	
}
