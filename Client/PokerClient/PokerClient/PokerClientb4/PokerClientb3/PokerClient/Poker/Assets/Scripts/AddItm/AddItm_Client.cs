using UnityEngine;
using System.Collections;

public class AddItm_Client : MonoBehaviour {

	public GameObject Item;
	public UIGrid grid;

	void Start (){
		grid = GetComponent<UIGrid> (); 
	}
	
	
	public void AddItem()
	{

		for (int i = 0; i <  MainList.getPlayerCount() ; i++)
		{
			GameObject obj = Instantiate(Item, new Vector3(0f, 0f, 0f), Quaternion.identity) as GameObject;
			obj.transform.parent = this.transform	;
			obj.transform.localScale = new Vector3(1f, 1f, 1f);
			obj.GetComponentInChildren<UILabel>().text = (string)MainList.getPlayerList()[i];
		}
		grid.Reposition();

	}

	public void EraseAllItem()
	{
		foreach ( Transform obj in transform ) {
			Destroy(obj.gameObject);
		}

	}

	 




}
