using UnityEngine;
using System.Collections;

public class GameList  {

	//Mine
	public static string MyPlayerId = null;

	//Turn 
	public static string Turn = null;

	//Game Order
	public static int GameOrder = 0;

	//others
	public static string[] OtherPlayerId = new string[3];

	//levePlayer
	public static string[] LeavePlayerID = new string[3];

	//RoomId
	public static string RoomdId = null;

	public static string RoomMasterId = null;


	//UserCount
	public static int UserConunt = 0 ;

	//List 
	public static Hashtable PlayList = new Hashtable();

	//Evalutate 
	public static Card[] value = new Card[7]; 

	//Evaluate open Card
	public static Card[] openvalue = new Card[4];

	//flag for Evaluate
	public static int flag  = 0 ;

	//flag for open Evaluate
	public static int openflag = 0;

	//Winner Info 
	public static string Winner ="";

	//Total Fee
	public static int TotalFee = 0 ;

	//Call Fee
	public static int CallFee = 0;

	//Currnent Doing
	public static int Doing = -1;

	//Candoing
	public static string canDo = "";

	//choice card
	public static Card Choice = null;

	//버리는 카드 
	public static Card Trash = null;

	public static bool RestartFlag = false;

	//reset All Things for restart the game 
	public static void GameListReset(){

		Turn =null;
		GameOrder = 0;
		flag = 0;;
		value = null;
		openvalue = null;
		openflag = 0;
		Winner = null;

		TotalFee = 0 ;
		CallFee = 0;

		Doing = -1;
		canDo = "";

		Choice = null;
		Trash = null;
		LeavePlayerID = new string[3];
		RestartFlag = false;

	}

	public static void TurnReset(){
		Doing = -1;
		canDo = "";
	}



}
