using UnityEngine;
using System.Collections;

public class StartButtonBehavior : MonoBehaviour {

	public WebsocketHandler web;
	public UISprite ButtonImg;
	public UIButton ButtonControl;

	// Use this for initialization
	void Start () {
		ButtonImg.enabled = false;
		ButtonControl.isEnabled = false;
	}

	public void ButtonEnable(){
		ButtonImg.enabled = true;
		ButtonControl.isEnabled = true;

	}


	void OnClick (){
		web.sendMessage ("STARTGAME");
		ButtonImg.enabled = false;
		ButtonControl.isEnabled = false;

	}
}
