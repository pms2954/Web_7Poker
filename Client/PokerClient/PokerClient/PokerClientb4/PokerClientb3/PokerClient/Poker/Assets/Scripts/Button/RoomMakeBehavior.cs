using UnityEngine;
using System.Collections;

public class RoomMakeBehavior : MonoBehaviour {

	public WebsocketHandler web ;

	void Start(){
	
	}

	void OnClick ()
	{
		web.sendMessage ("ROOM_MAKE");
	}
}
