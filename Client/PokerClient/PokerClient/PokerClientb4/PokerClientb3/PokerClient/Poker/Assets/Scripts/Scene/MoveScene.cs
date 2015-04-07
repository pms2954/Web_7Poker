using UnityEngine;
using System.Collections;

public class MoveScene : MonoBehaviour {

	// Use this for initialization
	public GameObject T;
	
	public WebsocketHandler web;

	public GameController Game;
	
	public RootController Root;

	void Start () {

	}
	// Update is called once per frame
	void Update () {

		switch(web.GameState){	
		
		case 0: 
			T.SetActive(true);
			Root.AddItm();
			break;	
		
		case 1: //Make
			T.SetActive(false);
			Game.Make();
			break;
		
		case 2: //Join
			T.SetActive(false);
			Game.Join();
			break;

		case 4: //Start
			Game.StartGame();
			break;

		case 5: //Turn
			Game.Turn();
			break;

		case 6: // After LastTurnAllocateCard
			Game.AllocateCard();
			break;

		case 7: 
			Game.AllocateCard();
			break;

		case 11: // Winner
			Game.Winner();
			break;

		case 15:
			Game.ChoiceCard();
			break;

		case 16: 
			Game.refreshCard();
			break;
		case 20:
			Game.leavePlayer();
			break;
		
		
		}

		web.GameState = -1;

	}
	
}
