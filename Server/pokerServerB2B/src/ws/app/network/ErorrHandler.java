package ws.app.network;

import net.netmarble.lobby.Player;
import net.netmarble.lobby.Room;
import net.netmarble.lobby.manager.LobbyManager;
import Def.web.type.DEF_TYPE;
/*
 *   
 *	 ���� : ���� �޼��� �ڵ鷯
 *   �߰� �ϴ� ��  :  �����޼����� �߻��� ��� ó���Ѵ�.   
 *  	     
 */
public class ErorrHandler {
	
	
	private int State  = 0 ;
	private boolean RoomIn = false;

	public void process(String Id, int State){
		
		switch(State){
		
		
		case DEF_TYPE.CONNECT:
			break;
		case DEF_TYPE.REQ_INFO_START : 
			Error_default(Id);
			break;
		case DEF_TYPE.REQ_MAKE_ROOM : 
			Error_Room(Id);	
			break;
		case DEF_TYPE.REQ_JOIN_ROOM : 
			Error_change(Id);	
			break;
		case DEF_TYPE.REQ_GAME_START : 
			Error_change(Id);
			break;
		case DEF_TYPE.REQ_GAME_CHANGE :
			//pData = Error_allc(Id);
			break;
		case DEF_TYPE.REQ_GAME_ALLC :
			Error_Turn(Id);
			break;
		case DEF_TYPE.REQ_GAME_TURN :
			Error_Turn(Id);
			break;
   		case DEF_TYPE.REQ_GAME_FIN :
   			Error_Win(Id);
   			break;

		
		}
	}
	
	

	
	public void Error_default(String Id){
		//nothing
		setState(DEF_TYPE.ACK_INFO_START);
	}
	
	public void Error_Room(String Id){

		Player play =LobbyManager.getInstance().getPlayer(Id);
				
		LobbyManager.getInstance().leaveRoom(play.getId());

		Room room = LobbyManager.getInstance().getRoom(play.getRoomId());
		
		if (room != null ){
			setRoomIn(true);
			setState(DEF_TYPE.ACK_INFO_START);

		}
		else{
			System.out.println("Error_NO USER IN Room");
			setState(DEF_TYPE.ACK_MAKE_ROOM);
		}


	}
	

	public void Error_change(String Id ){
		
		
		System.out.println("ErrorChange");
		
		Player play =LobbyManager.getInstance().getPlayer(Id);
		
		System.out.println("ID:"+play.getId());
		System.out.println("RoomID:"+play.getRoomId());
		
		Room tartgetRoom = LobbyManager.getInstance().getRoom(play.getRoomId());
		
		
		//������ �÷��� ���̶��
		if(tartgetRoom.isPlaying() == true && tartgetRoom.ControlGamePlayerCount("getGamePlayerCount") > 1 ){
		System.out.println("beforeChkIn");
		setRoomIn(true);

			if(tartgetRoom.chkIn(true, Id) == true && tartgetRoom.ControlGamePlayerCount("getGamePlayerCount") > 1){
					System.out.println("chkIN");
					//���� �������ΰ���
					LobbyManager.getInstance().pokerCalculate(tartgetRoom.getId());
					setState(DEF_TYPE.REQ_GAME_CHANGE);
					//���� �޼����� ���� �޼����� ������ �ȴ�. 
		
			}
			else if( tartgetRoom.ControlGamePlayerCount("getGamePlayerCount") == 1){
				System.out.println("CHkIN_submit");
				LobbyManager.getInstance().Submit(tartgetRoom.getId());
				setState(DEF_TYPE.ACK_GAME_FIN_DIE);

			}
		
		}
		
		// ������ ���� �÷������� �ƴ϶�� 
		else{
			System.out.println("Error_In Room");
			
			LobbyManager.getInstance().leaveRoom(play.getId());
			
			Room room = LobbyManager.getInstance().getRoom(play.getRoomId());
			
			if (room != null ){
				setRoomIn(true);
				setState(DEF_TYPE.ACK_INFO_START);

			}
			else{
				System.out.println("Error_NO USER IN Room");
				setState(DEF_TYPE.ACK_MAKE_ROOM);
			}

			
		}
		
		
	}
	
	public void Error_Turn(String Id){
		
		Player play =LobbyManager.getInstance().getPlayer(Id);

		Room tartgetRoom = LobbyManager.getInstance().getRoom(play.getRoomId());

		
		if(tartgetRoom.ControlGamePlayerCount("getGamePlayerCount") > 1 )
		{
			System.out.println("Error_Turn");
			setRoomIn(true);
			int StateValue =LobbyManager.getInstance().Batting(tartgetRoom.getId(), play.getId() , -1 , true);
			System.out.println("stateValue : " +StateValue);
			setState(StateValue);
		}
		else{
			System.out.println("Error_Turn_No user in Room");
			setState(DEF_TYPE.ACK_MAKE_ROOM);
		}

	}
	
	
	public void Error_Win(String Id){
		System.out.println("Error_win");
	
		Player play =LobbyManager.getInstance().getPlayer(Id);

		Room tartgetRoom = LobbyManager.getInstance().getRoom(play.getRoomId());
		
		
		//�濡 ���� ����� �����ִٸ�
		if(tartgetRoom.ControlGamePlayerCount("getGamePlayerCount") > 1 ){
			setRoomIn(true);
			LobbyManager.getInstance().leaveRoom(play.getId());
				
		}
		else {
			System.out.println("Error_Win_No user in Room");
			LobbyManager.getInstance().leaveRoom(play.getId());
			setState(DEF_TYPE.ACK_MAKE_ROOM);
			
		}
		//ȥ�� �����ִٸ� 
	
	
	}
	
	
	public void setState(int State){
		this.State = State;
	}
	
	public void setRoomIn(boolean What){
		this.RoomIn = What;
	}
	
	public boolean getRoomIn(){
		return RoomIn;
	}
	
	public int getState(){
		return State;
	}
}
