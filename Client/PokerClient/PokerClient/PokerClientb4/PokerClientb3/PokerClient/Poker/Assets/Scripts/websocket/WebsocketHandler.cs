using UnityEngine;
using WebSocketSharp;
using SimpleJSON;
using System.Collections;

public class WebsocketHandler : MonoBehaviour {

	public WebSocket ws;

	public int GameState = 0 ;

	public string  MoveScene = "" ;
	
	void Start(){
		ws = new WebSocket("ws://localhost:8080/pokerServerB2B/websocket/connect"); 
		webConnect ();
	}

	// 서버와 연결 
	public void webConnect () {

		print ("Open socket: " + ws.ReadyState);
		
		print ("Websocket Alive: " + ws.IsAlive);
		
		ws.OnMessage += (sender, e) => {

			print ("From srv: " + e.Data);

			// parsing data
			GameState = JsonDecodeManager.parsedata(e.Data);


		};
		
		ws.OnOpen += (sender, e) => {
			print ("WebSocket-> Open:");
			print ("Open socket-> OnOpen: " + ws.ReadyState);
		};
		
		ws.OnError += (sender, e) => {
			print ("WebSocket-> Error: " + e.Message);	
			print ("Open socket-> OnError: " + ws.ReadyState);
		};
		
		ws.OnClose += (sender, e) => {
			print ("WebSocket-> Close-code: " + e.Code);
			print ("WebSocket-> Close-reason: " + e.Reason);
			print ("Open socket-> OnClose: " + ws.ReadyState);
		};

		Security.PrefetchSocketPolicy ("localhost", 843);

		ws.Connect ();


		// start the waiting
		sendMessage("START");
		
	}



	//close the connection
	void OnDestroy () {
		if (ws != null && ws.ReadyState == WebSocketState.Open)
			ws.Close ();
	}

	// send Message to Server 
	public void sendMessage(string form){

		JsonEncodeManager json = new JsonEncodeManager();

		string msg = json.Encode(form); // 주기적 요청

		ws.Send (msg);

	}






}
