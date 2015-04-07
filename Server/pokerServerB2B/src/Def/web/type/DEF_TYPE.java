package Def.web.type;

public class DEF_TYPE {
	
	//Connect
	public static final int  CONNECT = 0;
	
	//Error
	public static final int ERROR = 100;
	
	//START
	public static final int REQ_INFO_START = 1;
	public static final int ACK_INFO_START = 1;
	
	//Room
	public static final int REQ_MAKE_ROOM = 2;
	public static final int ACK_MAKE_ROOM = 2;

	//Join
	public static final int REQ_JOIN_ROOM = 3;
	public static final int ACK_JOIN_ROOM = 3;
	
	//Game START
	public static final int REQ_GAME_START = 4;
	public static final int ACK_GAME_START = 4;
	
	//Turn
	public static final int REQ_GAME_TURN = 5;
	public static final int ACK_GAME_TURN = 5;
	
	//CARD allocate
	public static final int REQ_GAME_ALLC = 7;
	public static final int ACK_GAME_ALLC = 7;

	//CARD submit
	public static final int REQ_GAME_SUB  = 10;
	public static final int ACK_GAME_SUB =  10;
	
	//Game Fin
	public static final int REQ_GAME_FIN  = 12;
	public static final int ACK_GAME_FIN =  12;
	
	//Game Fin because All Die
	public static final int REQ_GAME_FIN_DIE  = 13;
	public static final int ACK_GAME_FIN_DIE =  13;
	
	//Card Change
	public static final int REQ_GAME_CHANGE = 15;
	public static final int ACK_GAME_CHANGE = 15;
	
	//Leave Room for connection lose
	public static final int REQ_lEAVE_Room = 20;
	public static final int ACk_lEAVE_Room = 20;
	

	
	
}
