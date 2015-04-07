using UnityEngine;
using System.Collections;

public class AllinBehavoir : MonoBehaviour {

	public WebsocketHandler web;
	public UISprite ButtonImg;
	public UIButton ButtonControl;
	public HandController Root;
	
	// Use this for initialization
	void Start () {
		ButtonImg.enabled = false;
		ButtonControl.isEnabled = false;
	}
	
	public void ButtonEnable(){
		ButtonImg.enabled = true;
		ButtonControl.isEnabled = true;
		
	}
	
	public void ButtonDisable(){
		ButtonControl.isEnabled = false;
		
	}
	
	public void ButtonReset(){
		ButtonImg.enabled = false;
		ButtonControl.isEnabled = false;
	}
	
	void OnClick (){
		GameList.Doing = Def.ALLIN;
		web.sendMessage ("TURN");
		ButtonControl.isEnabled = false;
		Root.AllinButtonDisable ();
	}
}
