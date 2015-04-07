using UnityEngine;
using System.Collections;

public class AddItm_Room : MonoBehaviour {

	public GameObject Room;
	public UIGrid grid;
	
	
	void Start (){
		grid = GetComponent<UIGrid> (); 
	}


	public void AddItem()
	{

		for (int i = 0; i < ((ArrayList)MainList.getRoomList()).Count  ; i++)
		{
			GameObject obj = Instantiate(Room, new Vector3(0f, 0f, 0f), Quaternion.identity) as GameObject;
			obj.transform.parent = this.transform	;
			obj.transform.localScale = new Vector3(1f, 1f, 1f);
			obj.GetComponentInChildren<UILabel>().text = ((Room)MainList.getRoomList()[i]).printId();
			if(!((Room)MainList.getRoomList()[i]).isJoin()){
				RoomBehavoir roomControl = obj.GetComponentInChildren<RoomBehavoir>();
				UISprite sprite = obj.GetComponentInChildren<UISprite>();
				roomControl.enabled = false;
				roomControl.disableClick();
				sprite.color = Color.red;
			}

		}
		grid.Reposition();

	}

	public void EraseAllItem()
	{
		foreach ( Transform obj in transform ) {
			Destroy(obj.gameObject);
		}
		grid.Reposition();
		
	}

}
