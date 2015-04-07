using UnityEngine;
using System.Collections;

public class RoomBehavoir : MonoBehaviour {

	WebsocketHandler web ;

	public bool flag = true;
	
	void OnClick(){

		if (flag == true) {
			//component connect 
			web = transform.parent.parent.parent.GetComponent<WebsocketHandler> ();
	
			string RoomId = GetComponentInChildren<UILabel> ().text;

			GameList.RoomdId = RoomId;

			web.sendMessage ("JOIN_ROOM");
		}

	}

	public void disableClick(){
		flag = false;
	}

}
