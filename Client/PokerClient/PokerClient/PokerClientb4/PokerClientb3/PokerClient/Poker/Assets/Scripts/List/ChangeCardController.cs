using UnityEngine;
using System.Collections;

public class ChangeCardController : MonoBehaviour {

	public int Count  = 0;
	
	//카드를 보여준다. 
	public GameObject[] cards;

	public GameObject C;

	public WebsocketHandler web;


	// Use this for initialization
	void Start () {
		//Reset ();
	}

	public void Reset(){
		
		
		for (int i =0; i< cards.Length; i++) {
			CardController cardController = cards [i].GetComponent<CardController> ();
			cardController.cardIndex = 0;
			cardController.HideCard ();
		}

		Count = 0;
	}


	//카드 4장 추가
	public void AddCards(){

		Player play = (Player)GameList.PlayList [GameList.MyPlayerId];
		
		int rank;

		for (int index =0; index < 4; index++) {
			
			CardController cardController = cards [index].GetComponent<CardController> ();

			//card 정보 변환
			if(play.getCard(index).CardRank == 1)
				rank = 12;
			else 
				rank = play.getCard(index).CardRank - 2 ;


			cardController.cardIndex = ((play.getCard(index).CardSuit - 1) * 13) + rank ;

			cardController.ShowFace();

		}
	}


	// 카드를 모두 보여준다.
	public void ShowCards(){
		AddCards ();
	}

	public void Choice(int CardIndex){
		//카드를 추가한다.
		int suit = CardIndex / 13 + 1 ;
		
		int rank = CardIndex % 13 ;


		if(rank == 12)
			rank =1;
		else 
			rank +=2;
	

		//openCard 
		if (this.Count == 1) {
			GameList.Choice = new Card (suit, rank);
			Debug.Log ("suit:"+suit +"  rank :"+rank);
			Debug.Log ("suit:"+GameList.Choice.CardSuit +"  rank :"+GameList.Choice.CardRank);

		}
		//trashCard
		else if (this.Count ==2)
			GameList.Trash = new Card(suit, rank);

	

	}




	//서버에 메세지를 보내낟 
	// 버리는 카드는 무엇이고 , 오픈할 카드는 무엇인지를 선택한다. 
	public void SendMessage(){
		web.sendMessage ("ChangeCard");
		Reset ();
		C.SetActive (false);
	}




}
