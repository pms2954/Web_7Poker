package ws.app.network;

import java.io.IOException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.netmarble.lobby.Lobby;
import net.netmarble.lobby.Player;
import net.netmarble.lobby.Room;
import net.netmarble.lobby.manager.LobbyManager;
import Def.web.type.DEF_TYPE;


/*
 *   
 *	 ���� :  ������ ó��
 *   �߰� �ϴ� ��  :  �����Ͽ� ���õ� �������� ó�����ش�.   
 *  	     
 */

@ServerEndpoint(value = "/websocket/connect")
public class WebSocket {

	private static final String USER_PREFIX = "USER";

	private static final AtomicInteger connectionIds = new AtomicInteger(0);


	public final String ID; // client ID
	private Session session; // active connection info
	
	private int State;

	public WebSocket() {
		
		String threadName = "Thread-Name:" + Thread.currentThread().getName();
		
		ID = USER_PREFIX + connectionIds.getAndIncrement();
	}
	
	public int getState(){
		return State;
	}
	
	@OnOpen
	public void start(Session session) {

		// make the json message from connections , room
		String sendMsg = DecodeJson.allocate(DEF_TYPE.CONNECT, ID, "");

		// set the session
		this.session = session;
		
		// log the state
		printState("Start", sendMsg, "NoN");

		// send message
		sendMessage(sendMsg);
	}

	@OnClose
    public void end() throws InterruptedException {
        
		Player play = LobbyManager.getInstance().getPlayer(ID);
		
		String RoomId = play.getRoomId();
		ErorrHandler Error = new ErorrHandler();
        
		
        printState("close" , "NoN" ,"NoN" );
        
        System.out.println("MythreadSTate:"+this.State);
        
        Error.process(ID , this.State);
		
        
        //�뿡 �ִٸ�  ��ȿ� �ο����� �޼��� ����
        if(Error.getRoomIn() == true){
        	//Room 
	        String leaveMsg = DecodeJson.allocate( DEF_TYPE.REQ_lEAVE_Room , ID , "" );
			
	        System.out.println("ErrorStateMsg" + leaveMsg);
			
	        if(leaveMsg != null)
				broadcastRoom(leaveMsg , RoomId , ID);
	
			Thread.sleep(1000);
        }        
		
        // Ư�� ���� ó�� 
		if(Error.getState() != 0 ){
        	String sendMsg = DecodeJson.allocate( Error.getState() , ID , RoomId );
		
	        LobbyManager.getInstance().disconnectLobby(ID);
	        
			Thread.sleep(1000);
	        
			if (LobbyManager.getInstance().getRoom(RoomId) == null ){
				System.out.println("No user in Room : so broadCast Lobby for Room");
				broadcastWaitingforRoom();
			}
			
			else if(sendMsg != null && Error.getState() > DEF_TYPE.ACK_INFO_START){
				System.out.println("ErrorDefaultMsg" + sendMsg);
				broadcastRoom(sendMsg , RoomId , ID);
	        }
			else if(sendMsg != null && Error.getState() == DEF_TYPE.ACK_INFO_START){
				System.out.println("User out in Lobby : so broadCast for Looby");
				broadcastWaiting(sendMsg);
			}
	
		}
		else{
	        LobbyManager.getInstance().disconnectLobby(ID);
		}
		
	
}

		
 
	// ���� ���ǰ� ����� Ŭ���̾�Ʈ�κ��� �޽����� ������ ������ ���ο� �����尡 ����Ǿ� incoming()�� ȣ����
	@OnMessage
	public void incoming(String revMsg) {
		String sendMsg = "";

		if (revMsg == null || revMsg.trim().equals(""))
			return;

		// parsing the client msg
		EncodeJson eJson = new EncodeJson();

		// parsing the revMsg
		String pData = eJson.parsing(revMsg, this.session, ID);
		
	
		// making send Message
		sendMsg = DecodeJson.allocate(eJson.getState(), ID, pData);
		
		this.State = eJson.getState();
		// log
		printState("incoming", sendMsg, revMsg);
		
		//Message
		if(pData == "" )
			sendMessage(sendMsg);
		
		else if(pData.equals("waiting"))
			broadcastWaiting(sendMsg);
		
		else if(pData.equals("MakeRoom")){
			
			sendMessage(sendMsg);
			
			broadcastWaitingforRoom();
		}
		
		else if(pData.equals("Error")){
			
			sendMessage(sendMsg);
			
		}
		else if(sendMsg != null){

			broadcastRoom(sendMsg , pData , eJson.userId);
		}
		

		//Additional Message
		if( eJson.getState() == DEF_TYPE.REQ_GAME_START || eJson.getState() == DEF_TYPE.REQ_GAME_FIN ){
			broadcastWaitingforRoom();
		}
		else if(eJson.getState() == DEF_TYPE.REQ_JOIN_ROOM){
			System.out.println("broadCastWaiting Room");
			broadcastWaitingforRoom();
		}

		

		
		
		
	}

	@OnError
	public void onError(Throwable t) throws Throwable {
		System.err.println("Socket Error: " + t.toString());

	}

	// ���� �������κ��� ������ �޼��� ������ �� ��ο��� ������.

	private static void broadcastRoom(String msg , String RoomId, String userID) {

		Room room = LobbyManager.getInstance().getRoom(RoomId);
		Session session;
		
		for(Player play : room.getParticipants()) {
			session = play.getSession();

			try {
					session.getBasicRemote().sendText(msg);
				
			} catch (IOException e) {
				// �޽��� ���� �߿� ������ �߻�(Ŭ���̾�Ʈ ������ �ǹ���)�ϸ� �ش� Ŭ���̾�Ʈ�� �������� �����Ѵ�
				System.err.println("IO Error: Failed to send message to client:"+ e);
			}
		}
		
		
	}

	
	private static void broadcastWaiting(String msg) {

		List<Player> players = Lobby.getInstance().getLobbyPlayersList();
		Session session;


		for(Player play : players ) {
			
			session = play.getSession();

			try {
					session.getBasicRemote().sendText(msg);
				
			} catch (IOException e) {
				// �޽��� ���� �߿� ������ �߻�(Ŭ���̾�Ʈ ������ �ǹ���)�ϸ� �ش� Ŭ���̾�Ʈ�� �������� �����Ѵ�
				try {
					// ���� ����
					session.close();
				} catch (IOException e1) {
					// Ignore
				}
				// �� Ŭ���̾�Ʈ�� ������ ��� �̿��ڿ��� �˸���
		
			}
		}
		
		
	}

	private static void broadcastWaitingforRoom() {

		List<Player> players = Lobby.getInstance().getLobbyPlayersList();
		Session session;
		String sendMsg = DecodeJson.allocate( DEF_TYPE.REQ_INFO_START , "" , "");

		for(Player play : players ) {
			
			session = play.getSession();

			try {
					session.getBasicRemote().sendText(sendMsg);
				
			} catch (IOException e) {
				// �޽��� ���� �߿� ������ �߻�(Ŭ���̾�Ʈ ������ �ǹ���)�ϸ� �ش� Ŭ���̾�Ʈ�� �������� �����Ѵ�
				System.err.println("IO Error: Failed to send message to client:"+ e);
				
				try {
					// ���� ����
					session.close();
				} catch (IOException e1) {
					// Ignore
				}
				// �� Ŭ���̾�Ʈ�� ������ ��� �̿��ڿ��� �˸���
			}
		}
		
		
	}
	
	
	// ���� �������� �޼����� ������.
	private void sendMessage(String msg) {
		try {
			session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
			// �޽��� ���� �߿� ������ �߻�(Ŭ���̾�Ʈ ������ �ǹ���)�ϸ� �ش� Ŭ���̾�Ʈ�� �������� �����Ѵ�
			System.err.println("Error: Failed to send message to client:" + e);
			try {
				// ���� ����
				session.close();
			} catch (IOException e1) {
				// Ignore
			}
		}
	}

	// ���� ������ ������ �ֿܼ� �ѷ��ش�.
	public void printState(String state, String sendMsg, String recvMsg) {
		String threadName = "Thread-Name:" + Thread.currentThread().getName();
		// INfo about network
		System.out.println("ID: " + ID + "\tSTATE: " + state + "\tThread: "
				+ threadName + "\treceive Message :" + recvMsg
				+ "\tsendMessage : " + sendMsg);
		// Room info

	}

}