package net.netmarble.lobby.result;
/*
 *  기존 코드 : LANPARTY 코드 
 *	 목적 : 룸 처리 결과  
 *   추가 하는 일  :  수정사항 없음 
 *  	     
 */
public class Result {
	// errorCode
	public final static int OK 									= 0x00000000;
	public final static int FAIL								= 0xffffffff;
	
	// roomErrorCode
	public static final int ENOUGH_PLAYER 					= 0x00019001;
	public static final int NOT_ENOUGH_PLAYER 				= 0x00019002;
	public static final int NOW_PLAYING 					= 0x00019003;
	public static final int NO_HOST_PLAYER 					= 0x00019004;
	public static final int EXIST_PLAYER 					= 0x00019005;
	
	private final int response;
	private final String message;
	
	public Result(int response, String message) {
		this.response = response;
		this.message = message;
	}
	
	public int getResponse() {
		return response;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isSuccess() {
		return (response == OK);
	}
}
