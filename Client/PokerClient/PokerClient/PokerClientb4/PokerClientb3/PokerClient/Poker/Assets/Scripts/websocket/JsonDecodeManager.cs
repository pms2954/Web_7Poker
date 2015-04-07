using UnityEngine;
using System.Collections;
using SimpleJSON;


public class JsonDecodeManager {
	
	public static int parsedata(string data){

		JSONNode json = JSON.Parse (data);

		int GameState = 0;

		int json_type = json["type"].AsInt; // type


		switch ( json_type ) {
				
				
				//Main
				case 0 : //Ack_MyId
						GameState = Ack_Info_MyId(json);
						break;
				case 1:	 // Ack_Info_Start
						GameState = Ack_Info_Start(json);
						break;
				case 2: //ACK_ROOM_MAKE
						GameState = Ack_Make_Room(json);
						break;
				case 3: //ACK_RooM_Join
						GameState = Ack_Join_Room(json);
						break;				
				//Game
				case 4: //ACK_STart_Game
						GameState = Ack_Start_Game(json);
						break;
				case 5: //ACK_Game_Turn
						GameState = Ack_Game_Turn(json);
						break;
				case 7: //ACK_Game_Allc
						GameState = Ack_Game_Allc(json);
						break;
				case 11:
						GameState = Ack_Game_Fin(json);
						break;
				
				//Game ChangeCard
				case 15:
						GameState = Ack_Game_Change(json);
						break;
				//Error
				case 20:
						GameState = Ack_Game_leave(json);
						break;
			}

		return GameState;
	}


	// CLIENT AND ROOM INFO 
	public static int Ack_Info_MyId(JSONNode json){
	
		GameList.MyPlayerId = json["MyId"].Value;

		return  0; // Main
	}



	// CLIENT AND ROOM INFO 
	public static int Ack_Info_Start(JSONNode json){

		MainList.Initialize_MainList ();
	

		for(int cCount = 0; json["Player"][cCount] != null ; cCount++ ){
			MainList.getPlayerList().Add(json["Player"][cCount]["ID"].Value);
		}

		for (int rCount = 0; json["Room"][rCount] != null; rCount++) {
			Room room;

			if(json ["Room"] [rCount] ["Full"]!= null)
				room = new Room (json ["Room"] [rCount] ["ID"].Value , json ["Room"] [rCount] ["STATE"].Value , true);
			else 
				room = new Room (json ["Room"] [rCount] ["ID"].Value , json ["Room"] [rCount] ["STATE"].Value , false);

			MainList.getRoomList().Add (room);
		}

		return  0; // Main
	}

	//MAKE ROOM
	public static int Ack_Make_Room(JSONNode json){
	
		Player player = new Player (json ["roomMasterId"].Value, json ["roomState"].Value, json ["Cash"].AsInt);

		GameList.PlayList.Add ( json ["roomMasterId"].Value , player);
		GameList.RoomdId = json ["roomId"].Value;
		GameList.RoomMasterId = json["roomMasterId"].Value;
		GameList.UserConunt++;


		return 1 ;  //Make
	}
	
	//JOIN ROOM 
	public static int Ack_Join_Room(JSONNode json){

		int MyCount = 0;
		//order 
		GameList.GameOrder =  json["gaemOrder"].AsInt;
		GameList.RoomMasterId = json["MasterId"].Value;

		for (int rCount = 0; json["PlayerList"][rCount] != null; rCount++) {

			Player player;

			string Id = json["PlayerList"][rCount]["Player"].Value;
			int cash  = json ["PlayerList"][rCount]["Cash"].AsInt;
			string MyId = GameList.MyPlayerId;
			// Player exist
			if ( GameList.PlayList.Contains ( Id )  == true){
				player = (Player)GameList.PlayList[Id];
				player.setId(Id);
				player.setCash(cash);
			}
			// Player not exist
			else{
				if(MyId.Equals(Id)){

					player = new Player ( Id ,  null , cash );

					GameList.PlayList.Add ( Id , player);
					MyCount = rCount;
					GameList.UserConunt++;

				}	
				else {
						for (int index = 0 ; index < 3 ; index++ ){
							if( GameList.OtherPlayerId[index] == null )
							{
								GameList.OtherPlayerId[index] = Id;
								player = new Player ( Id ,  "N" , cash );
								GameList.PlayList.Add ( Id , player);
								GameList.UserConunt++;
								index = 4;
							}

					}
				}
			}
		}

		if (MyCount == 1) {
			GameList.OtherPlayerId[2] = GameList.OtherPlayerId[0];
			GameList.OtherPlayerId[0] =null;

		} else if (MyCount == 2) {
			GameList.OtherPlayerId[2] = GameList.OtherPlayerId[1];
			GameList.OtherPlayerId[1] = GameList.OtherPlayerId[0];
			GameList.OtherPlayerId[0] = null;
		}

		//new Scenece
		return 2 ;  //Join
	}

	//ACK_START_GAME
	public static int Ack_Start_Game(JSONNode json){

		GameList.TotalFee = json["TotalFee"].AsInt;

		for (int rCount = 0; json["PlayerList"][rCount] != null ; rCount++) {

			Player player;

			string Id = json["PlayerList"][rCount]["Player"].Value;
			int cash  = json ["PlayerList"][rCount]["Cash"].AsInt;

			player = (Player)GameList.PlayList[Id];
			player.setId(Id);
			player.setCash(cash);

			for (int index  = 1 ; index < 5 ; index++){
				int suit = json ["PlayerList"][rCount]["CardSuit"+index].AsInt;
				int rank = json ["PlayerList"][rCount]["CardRank"+index].AsInt;
				player.setCard( index - 1 , new Card(suit, rank));
			}
		}
		return 4; // START_GAMe
	
	}


	//ACK_START_GAME
	public static int Ack_Game_Turn(JSONNode json){

	
		if (json ["subType"] != null) {
		
			string TurnId = json["turn"];
			
			GameList.Turn = TurnId;

				
			GameList.GameOrder = json["gaemOrder"].AsInt;
			
			int order = GameList.GameOrder;

			string playerId = json ["playerId"].Value;
			
			if( GameList.PlayList.Contains(playerId) == true){

				Player LastCallPlayer = (Player)GameList.PlayList [playerId];

				LastCallPlayer.setDoing (json ["doing"].AsInt);

				LastCallPlayer.AddTotalFee (json["TotalPaid"].AsInt);

				LastCallPlayer.setCash (json ["Cash"].AsInt);
				

				if (json ["State"] != null) {
					LastCallPlayer.setPlayState(json ["State"].Value);
					
				}
			}
			
			for (int rCount = 0; json["PlayerList"][rCount] != null ; rCount++) {
				
				Player player;
				
				string Id = json["PlayerList"][rCount]["Player"].Value;
				int cash  = json ["PlayerList"][rCount]["Cash"].AsInt;

				if( GameList.PlayList.Contains(Id) == true){
					player = (Player)GameList.PlayList[Id];
					player.setId(Id);
					player.setCash(cash);
					int suit = json ["PlayerList"][rCount]["CardSuit"+order].AsInt;
					int rank = json ["PlayerList"][rCount]["CardRank"+order].AsInt;
					player.setCard( order - 1  , new Card(suit, rank));
				}
				
			}
			return 6; 


		} else {

			GameList.Turn = json["turn"];
		
			GameList.CallFee = json ["CallValue"].AsInt;
		
			GameList.TotalFee = json ["TotalFee"].AsInt;
		

			if (json ["first"] != null) {

				return 8;

			} else {

				GameList.canDo = json ["CanDoing"].Value; //Restrict or Default

				string playerId = json ["playerId"].Value;

				if( GameList.PlayList.Contains(playerId) == true){

					Player player = (Player)GameList.PlayList [playerId];

					player.setDoing (json ["doing"].AsInt);

					player.setCurrentFee (json ["Fee"].AsInt);

					player.setCash (json ["Cash"].AsInt);

					player.AddTotalFee (json ["TotalPaid"].AsInt);

					if (json ["State"] != null) {
						player.setPlayState (json ["State"].Value);

					}
				}

				return 5; // START_GAMe_Turn
			}
		}

	}


	

	//ACK_Game_Allc
	public static int Ack_Game_Allc(JSONNode json){
		
		GameList.GameOrder = json ["GameOrder"].AsInt;

		string playerId = json ["playerId"].Value;

		GameList.Turn = json ["turn"].Value;
		
		//이전에 남아있던 마지막 콜 유저 정보 갱신 
				
		for (int rCount = 0; json["PlayerList"][rCount] != null; rCount++) {
			
			Player player;
			
			string Id = json["PlayerList"][rCount]["Player"].Value;
			int cash  = json ["PlayerList"][rCount]["Cash"].AsInt;
			
			player = (Player)GameList.PlayList[Id];
			player.setId(Id);
			player.setCash(cash);
			
			for (int index  = GameList.GameOrder ; index < GameList.GameOrder+1 ; index++){
				int suit = json ["PlayerList"][rCount]["CardSuit"+index].AsInt;
				int rank = json ["PlayerList"][rCount]["CardRank"+index].AsInt;
				player.setCard( index - 1 , new Card(suit, rank));
			}
			
			
		}
		return 7; // START_GAMe
		
	}


	public static int Ack_Game_Fin(JSONNode json){
		
		string Winner = json["winner"];
		
		GameList.Winner = Winner;
		//이전 doing 정보 


		string playerId = json ["playerId"].Value;

		if (GameList.PlayList.Contains (playerId) == true) {

			Player LastCallPlayer = (Player)GameList.PlayList [playerId];
		
			LastCallPlayer.setDoing (json ["doing"].AsInt);
		
			LastCallPlayer.AddTotalFee (json ["TotalPaid"].AsInt);
		
			LastCallPlayer.setCash (json ["Cash"].AsInt);
		
			if (json ["State"] != null) {
				LastCallPlayer.setPlayState (json ["State"].Value);
			
			}

		}
		for (int rCount = 0; json["PlayerList"][rCount] != null; rCount++) {
			
			Player player;
			
			string Id = json ["PlayerList"] [rCount] ["Player"].Value;
			int cash = json ["PlayerList"] [rCount] ["Cash"].AsInt;
			int beforeCash = json ["PlayerList"] [rCount] ["beforeCash"].AsInt;
			int reamins = json ["PlayerList"] [rCount] ["remains"].AsInt;
			
			player = (Player)GameList.PlayList [Id];
			player.setCash(cash);
			player.setBeforeCash (beforeCash);
			player.setReamains (reamins);

		}
		return 11; 
		
	}

	


	//ACK_START_GAME
	public static int Ack_Game_Change(JSONNode json){
		
		if (json ["subType"] != null) {

			Player player;
			
			string Id = json ["Player"].Value;

			player = (Player)GameList.PlayList [Id];

			for(int index  = 1; index < 4; index++) {
				int suit = json ["CardSuit" + index].AsInt;
				int rank = json ["CardRank" + index].AsInt;
				player.setCard (index - 1, new Card (suit, rank));
			}
			// 마지막 카드는 비워버린다. 
			player.setCard( 3, new Card(0,0));
			return 16;

		} 
		else {

			string TurnId = json["Turn"].Value;
			
			GameList.Turn = TurnId;
	
			GameList.GameOrder = json["GameOrder"].AsInt;

			for (int rCount = 0; json["PlayerList"][rCount] != null; rCount++) {
			
				Player player;


				string Id = json ["PlayerList"] [rCount] ["Player"].Value;
				int cash = json ["PlayerList"] [rCount] ["Cash"].AsInt;
			
				player = (Player)GameList.PlayList [Id];

				player.setId (Id);

				player.setCash (cash);

				for (int index  = 1; index < 5; index++) {
					int suit = json ["PlayerList"] [rCount] ["CardSuit" + index].AsInt;
					int rank = json ["PlayerList"] [rCount] ["CardRank" + index].AsInt;
					player.setCard (index - 1, new Card (suit, rank));



				}
			}


			return 15; // START_GAMe
		}
		
	}
	//ACk_GAME_Leave
	public static int  Ack_Game_leave(JSONNode json){
	
		for (int index  =0; index < 3; index++) {
			if(GameList.LeavePlayerID[index] == null){
				GameList.LeavePlayerID[index] = json ["leavePlayer"].Value;
				break;
			}

		}

		GameList.RoomMasterId = json["roomMasterId"].Value;

		return 20;
	}






}

