using UnityEngine;
using System.Collections;

public class RootController : MonoBehaviour {

	GameObject obj;
	public AddItm_Client waitingClient;
	public AddItm_Room Room;

	public void AddItm(){

		//기존의 것을 지운다.
		waitingClient.EraseAllItem ();
		Room.EraseAllItem ();
		//대기자를 만든다.
		waitingClient.AddItem ();
		Room.AddItem ();
		//배열을 초기화 
		((ArrayList)MainList.getPlayerList()).Clear ();
		((ArrayList)MainList.getRoomList()).Clear ();

	
	}


}
