using UnityEngine;
using System.Collections;
using SimpleJSON;

public class JsonEncodeManager  {

	JSONClass eJson;


	public JsonEncodeManager(){
		eJson = new JSONClass(); 
	}


	public string Encode(string type ){
	
		// 로그인 
		if (type.Equals ("START")) {
			eJson.Add ("type", "1");
			eJson.Add ("username", "temp");
			eJson.Add ("password", "temp");

		}

		//방 만들기 
		else if (type.Equals ("ROOM_MAKE")) {
			eJson.Add ("type", "2");

		}

		//최초 접속 
		else if (type.Equals ("MYID")) {
			eJson.Add ("type", "0");
			
		} 

		// 방 접속 
		else if (type.Equals ("JOIN_ROOM")) {
			eJson.Add ("type", "3");
			eJson.Add ("roomId", GameList.RoomdId);
		} 

		//게임 시작 
		else if (type.Equals ("STARTGAME")) {
			eJson.Add ("type", "4");
			eJson.Add ("roomId", GameList.RoomdId);
			eJson.Add ("hostId", GameList.MyPlayerId);
		}

		// 턴 넘기기 
		else if (type.Equals ("TURN")) {
			eJson.Add ("type", "5");
			eJson.Add ("roomId", GameList.RoomdId);
			eJson.Add ("hostId", GameList.MyPlayerId);	
			eJson.Add ("doing", GameList.Doing.ToString ());	

		} 

		//오픈 카드 전송 
		else if (type.Equals ("AllocateCard")) {		
			eJson.Add ("type", "7");
			eJson.Add ("playerId", GameList.MyPlayerId);
			eJson.Add ("RoomId", GameList.RoomdId);
		} 

		/*
		// 카드 족보 전송 
		else if (type.Equals ("SUBITCARD")) {

			eJson.Add ("type", "10");
			eJson.Add ("flag", GameList.flag.ToString ());
			eJson.Add ("playerId", GameList.MyPlayerId);
			eJson.Add ("RoomId", GameList.RoomdId);
			for (int index = 0; index < 5; index ++) {
				eJson.Add ("card" + index + "R", GameList.value [index].CardRank.ToString ());
				eJson.Add ("card" + index + "S", GameList.value [index].CardSuit.ToString ());
			}
		} 
		*/

		//끝
		else if (type.Equals ("finish")) {		
			eJson.Add ("type", "12");
			eJson.Add ("RoomId", GameList.RoomdId);
		} 


		//카드 교체
		else if (type.Equals ("ChangeCard")) {
			eJson.Add ("type", "15");
			eJson.Add ("playerId", GameList.MyPlayerId);
			eJson.Add ("RoomId", GameList.RoomdId);

			eJson.Add ("OpCardR", GameList.Choice.CardRank.ToString());
			eJson.Add ("OpCardS", GameList.Choice.CardSuit.ToString());
			eJson.Add ("TCardR", GameList.Trash.CardRank.ToString());
			eJson.Add ("TCardS", GameList.Trash.CardSuit.ToString());
		
		}


		return eJson.ToString();
	}




}
