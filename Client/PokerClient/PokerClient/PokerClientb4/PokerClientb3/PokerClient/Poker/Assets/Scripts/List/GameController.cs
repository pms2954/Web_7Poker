using UnityEngine;
using System.Collections;

public class GameController : MonoBehaviour {


	public HandController Mine;
	public HandController oth0;
	public HandController oth1;
	public HandController oth2;

	public UILabel TotalFee;
	public UILabel CallFee;

	public WebsocketHandler web;

	public ChangeCardController chg;
	public GameObject C;

	public bool Action = false;
	public bool lAction = false;
	//Make
	public void Make(){
		Mine.AddMe (GameList.MyPlayerId);
	}


	//Join
	public void Join(){
		// ID 가 존재한다면 

		if (GameList.MyPlayerId != null)
			Mine.AddMe (GameList.MyPlayerId );

		if (GameList.OtherPlayerId[0] != null)
			oth0.AddMe (GameList.OtherPlayerId[0]);

		if (GameList.OtherPlayerId[1] != null)
			oth1.AddMe (GameList.OtherPlayerId[1]);

		if (GameList.OtherPlayerId[2] != null)
			oth2.AddMe (GameList.OtherPlayerId[2]);

		
	}
	//Start
	public void StartGame(){
		if (GameList.MyPlayerId != null)
			Mine.StartReset ();
		if (GameList.OtherPlayerId [0] != null)
			oth0.StartReset ();
		
		if (GameList.OtherPlayerId[1] != null)
			oth1.StartReset ();
		
		if (GameList.OtherPlayerId[2] != null)
			oth2.StartReset ();
		// 모든 곳에서 돈을 뿌린다.
		StartCoroutine("DistributeCard");
		// 모든 곳에 카드를 한장 씩  뿌린다.
	}

	public void leavePlayer(){
		StartCoroutine ("leaveAction");
	}

	IEnumerator leaveAction(){
		lAction = true;
		while (Action == true) {
			yield return new WaitForSeconds (0.2f);
		}

		for(int index  =0 ; index<3  ; index++ ){
			if(GameList.LeavePlayerID[index]!=  null){
				if (GameList.OtherPlayerId [0] != null && GameList.OtherPlayerId [0].Equals (GameList.LeavePlayerID[index])) {
					oth0.leveForReset ();
					oth0.resetCardPosition();
					GameList.OtherPlayerId [0] = null;
				}
			
				if (GameList.OtherPlayerId [1] != null && GameList.OtherPlayerId [1].Equals (GameList.LeavePlayerID[index])) {
					oth1.leveForReset ();
					oth1.resetCardPosition();

					GameList.OtherPlayerId [1] = null;
				
				}
			
				if (GameList.OtherPlayerId [2] != null && GameList.OtherPlayerId [2].Equals (GameList.LeavePlayerID[index])) {
					oth2.leveForReset ();
					oth2.resetCardPosition();

					GameList.OtherPlayerId [2] = null;
				
				}
				GameList.UserConunt--;	
				GameList.LeavePlayerID[index]=null;
			}
		}
		lAction = false;
	}


	//카드 던지기 액션 
	IEnumerator DistributeCard()
	{
		int Count = 0;
		int Turn = 0;
		int CardDisNum = 0;

		Action = true;

		//룸아이디가 같은 유저가 선이게 선을 정한다. 
		//존재하는 유저중 방아이디를 가진 유저에게 선을 준다. 

		//내가 방장이면 
		if(GameList.RoomMasterId.Equals(GameList.MyPlayerId)){
			Turn  = 3;
		}
		// 다른 사람이 방장이면 
		else if  (GameList.OtherPlayerId[0] != null && GameList.RoomMasterId.Equals(GameList.OtherPlayerId[0])) {
			Turn  = 0;
		}
		else if (GameList.OtherPlayerId[1] != null && GameList.RoomMasterId.Equals(GameList.OtherPlayerId[1]) ) {
			Turn  = 1;
		}
		else if (GameList.OtherPlayerId[2] != null && GameList.RoomMasterId.Equals(GameList.OtherPlayerId[2]) ) {
			Turn  = 2;
		}

		while (true) {

			yield return new WaitForSeconds(0.3f);

			bool flag = false;
			// 선 순서로부터 하나씩 유저 에 접근한다.  
			// 카드를 오픈한다. 
			if (GameList.MyPlayerId != null && Turn  == 3) {
				Mine.DisbuteCard();
				Turn = 0;
			}

			else if (GameList.OtherPlayerId[0] != null && Turn  == 0 ){
				oth0.DisbuteCard ();
				Turn = 1;
			}
			else if(GameList.OtherPlayerId[0] == null && Turn  == 0  ){
				Turn = 1;
				flag = true;
				
			}
		
			else if (GameList.OtherPlayerId[1] != null && Turn  == 1){
				oth1.DisbuteCard ();
				Turn = 2;
			}
			else if(GameList.OtherPlayerId[1] == null && Turn  == 1){
				Turn = 2;
				flag = true;

			}

			else if (GameList.OtherPlayerId[2] != null && Turn  == 2){
				oth2.DisbuteCard ();
				Turn =3 ;
			}
			else if(GameList.OtherPlayerId[2] == null && Turn  == 2){
				Turn = 3;
				flag = true;

			}


			if(Count == GameList.UserConunt-1){
				Count  = 0;
				if(CardDisNum == 4){
					StartCoroutine("ShowCard");
					break;
				}
				CardDisNum ++;


			}

			if(flag == false)
				Count++;

			flag=false;
		}

	}

	// 카드 4장 보여주기 
	IEnumerator ShowCard(){

		yield return new WaitForSeconds(1.5f);

		if (GameList.MyPlayerId != null) {
			Mine.refreshCard ();
			Mine.Poker();
		}
		
		if (GameList.OtherPlayerId[0] != null)
			oth0.refreshCard ();
		
		if (GameList.OtherPlayerId[1] != null)
			oth1.refreshCard ();
		
		if (GameList.OtherPlayerId[2] != null)
			oth2.refreshCard ();

		TotalFee.text = GameList.TotalFee.ToString ();

		C.SetActive(true);
		chg.ShowCards();

		Action = false;
	}

	//카드 재배치 
	public void refreshCard(){

		if (GameList.MyPlayerId != null) {
			Mine.refreshCard ();
			Mine.Poker();
		}
		
		if (GameList.OtherPlayerId[0] != null)
			oth0.refreshCard ();
		
		if (GameList.OtherPlayerId[1] != null)
			oth1.refreshCard ();
		
		if (GameList.OtherPlayerId[2] != null)
			oth2.refreshCard ();
		
		
		TotalFee.text = GameList.TotalFee.ToString ();
		
	
	}

	//카드 선택이후 배포 
	public void ChoiceCard(){
		//배포되엇던 4번째 카드를 이전 장소로 다시 회수 한다. 
		TotalFee.text = GameList.TotalFee.ToString ();


		if (GameList.MyPlayerId != null) {
			Mine.AddCards();
			Mine.refreshFourthCard ();
		}
		if (GameList.OtherPlayerId [0] != null) {
			oth0.AddCards();
			oth0.refreshFourthCard ();
		}
		if (GameList.OtherPlayerId [1] != null) {
			oth1.AddCards();
			oth1.refreshFourthCard ();

		}
		if (GameList.OtherPlayerId [2] != null) {
			oth2.AddCards();
			oth2.refreshFourthCard ();
		}

		//그리고 다시 배포한다. 
		StartCoroutine ("Disitribute1Card");
		GameList.Turn = null;


	}
	//카드 배포 
	IEnumerator Disitribute1Card()
	{
		int Count = 0;
		int Turn = 0;

		Action = true;


		//룸아이디가 같은 유저가 선이게 선을 정한다. 
		//존재하는 유저중 방아이디를 가진 유저에게 선을 준다. 
		//만약 다이 상태라면 무시한다. 

		string mindState = ((Player)GameList.PlayList [GameList.MyPlayerId]).printState();
		string oth1State ="";
		string oth2State ="";
		string oth3State ="";
		bool Dmind  = false;
		bool Doth1  = false;
		bool Doth2  = false;
		bool Doth3  = false;

		if (GameList.OtherPlayerId[0] != null) {
			oth1State = ((Player)GameList.PlayList [GameList.OtherPlayerId [0]]).printState ();
		}
		if (GameList.OtherPlayerId [1] != null) {
			oth2State = ((Player)GameList.PlayList [GameList.OtherPlayerId [1]]).printState ();
		}
		if (GameList.OtherPlayerId [2] != null) {
			oth3State = ((Player)GameList.PlayList [GameList.OtherPlayerId [2]]).printState ();
		}




		if (mindState != null && mindState.Equals ("Died")) {
			Dmind = true;
		}
		if (oth1State != null && oth1State.Equals ("Died")) {
			Doth1 = true;
		}
		if (oth2State != null && oth2State.Equals ("Died")) {
			Doth2 = true;
		}
		if (oth3State != null && oth3State.Equals ("Died")) {
			Doth3 = true;
		}


		//내가 방장이면 
		if(GameList.Turn.Equals(GameList.MyPlayerId)){
			Turn  = 3;
		}

		// 다른 사람이 방장이면 
		else if  (GameList.OtherPlayerId[0] != null && GameList.Turn.Equals(GameList.OtherPlayerId[0])) {
			Turn  = 0;
		}
		else if (GameList.OtherPlayerId[1] != null && GameList.Turn.Equals(GameList.OtherPlayerId[1]) ) {
			Turn  = 1;
		}
		else if (GameList.OtherPlayerId[2] != null && GameList.Turn.Equals(GameList.OtherPlayerId[2]) ) {
			Turn  = 2;
		}
	

		while (true) {
			
			yield return new WaitForSeconds(0.8f);

			bool flag = false;
			// 선 순서로부터 하나씩 유저 에 접근한다.  
			// 카드를 오픈한다. 
			if (GameList.MyPlayerId != null && Turn  == 3  ) {

				if(!Dmind)
					Mine.DisbuteCard();

				Turn = 0;
			}
			else if (GameList.OtherPlayerId[0] != null && Turn  == 0  ){
				if(!Doth1)
					oth0.DisbuteCard ();
				Turn = 1;
			}
			else if( GameList.OtherPlayerId[0] == null && Turn  ==0){
				Turn = 1;
				flag = true;

			}
			
			else if (GameList.OtherPlayerId[1] != null && Turn  == 1){
				if(!Doth2)
					oth1.DisbuteCard ();
				Turn = 2;
			}
			else if(  GameList.OtherPlayerId[1] == null && Turn  ==1 ){
				Turn = 2;
				flag = true;

			}

			else if (GameList.OtherPlayerId[2] != null && Turn  == 2){
				if(!Doth3)
					oth2.DisbuteCard ();
				Turn =3 ;
			}
			else if(GameList.OtherPlayerId[2] == null && Turn  == 2 ){

				Turn = 3;
				flag = true;


			}

			if(flag == false){
				if(Count == GameList.UserConunt-1){
					StartCoroutine("AddCards");
					break;
				}
				Count++;
			}

			flag = false;

		}
		
	}


	IEnumerator AddCards(){
		
		yield return new WaitForSeconds(0.5f);

		//4장이하라
		if (GameList.GameOrder <= 4) {
			// 그리고 카드 정보를 보여준다. 
			if (GameList.MyPlayerId != null) {
				Mine.AddCards ();
				Mine.Poker ();

			}
		
			if (GameList.OtherPlayerId [0] != null)
				oth0.AddCards ();
		
			if (GameList.OtherPlayerId [1] != null)
				oth1.AddCards ();
		
			if (GameList.OtherPlayerId [2] != null)
				oth2.AddCards ();
		
		
			Action = false;

			SendMessage ("AllocateCard");

		} else {

			if (GameList.MyPlayerId != null) {
				Mine.AddCard ();
				Mine.TurnFirst ();

				if(GameList.GameOrder !=7)
					Mine.Poker();
				
			}
			
			if (GameList.OtherPlayerId[0] != null)
				oth0.AddCard ();
			
			if (GameList.OtherPlayerId[1] != null)
				oth1.AddCard();
			
			if (GameList.OtherPlayerId[2] != null)
				oth2.AddCard();
			Action = false;

		}
	}
	

	public void Turn(){
		// 만약 내 턴이라면 활성화 
		
		//토탈 금액 출력
		TotalFee.text = GameList.TotalFee.ToString();
		//콜 금액 출력 
		CallFee.text = GameList.CallFee.ToString();

		// 턴이 아니라면 정보 출력 
		if (GameList.MyPlayerId != null) {
			Mine.TurnDefault ();
			Mine.setCashInfo();
		}
		
		if (GameList.OtherPlayerId [0] != null) {
			oth0.setCashInfo ();
		}
		
		if (GameList.OtherPlayerId [1] != null) {
			oth1.setCashInfo ();
		}
		
		if (GameList.OtherPlayerId [2] != null) {
			oth2.setCashInfo ();
		}


	}

	//Allocate
	public void AllocateCard(){
		
		//토탈 금액 출력
		TotalFee.text = GameList.TotalFee.ToString();
		//콜 금액 출력 
		CallFee.text = GameList.CallFee.ToString();

		//이전 내역을 초기화 해줌과 동시에 
		// 턴이 아니라면 정보 출력 
		if (GameList.MyPlayerId != null) {
			Mine.resetCashInfo();
		}
		
		if (GameList.OtherPlayerId [0] != null) {
			oth0.resetCashInfo ();
		}
		
		if (GameList.OtherPlayerId [1] != null) {
			oth1.resetCashInfo ();
		}
		
		if (GameList.OtherPlayerId [2] != null) {
			oth2.resetCashInfo ();
		}

		StartCoroutine ("Disitribute1Card");


	}
	
	//Winner
	public void Winner(){
		StartCoroutine("winner");
	}

	IEnumerator winner()
	{
		while (Action == true) {
			yield return new WaitForSeconds (0.2f);
		}
		while (lAction == true) {
			yield return new WaitForSeconds (0.2f);

		}
		TotalFee.text = "";
		CallFee.text ="";
		
		//이전 내역을 초기화 해준다. 
		if (GameList.MyPlayerId != null) {
			Mine.resetCashInfo();
		}
		
		if (GameList.OtherPlayerId [0] != null) {
			oth0.resetCashInfo ();
		}
		
		if (GameList.OtherPlayerId [1] != null) {
			oth1.resetCashInfo ();
		}
		
		if (GameList.OtherPlayerId [2] != null) {
			oth2.resetCashInfo ();
		}
		
		
		if (GameList.MyPlayerId != null) {
			Mine.showAllCard();
			
		}
		if (GameList.OtherPlayerId[0] != null)
			oth0.showAllCard ();
		
		if (GameList.OtherPlayerId[1] != null)
			oth1.showAllCard ();
		
		if (GameList.OtherPlayerId[2] != null)
			oth2.showAllCard ();

		C.SetActive(false);
		chg.Reset ();


		//wait 3 seconds
		StartCoroutine("Delay3Sec");

	}

	IEnumerator Delay3Sec()
	{
		bool Error = false;

		yield return new WaitForSeconds(10f);
		//모든 내역 초기화 
		GameList.GameListReset ();

		if (GameList.MyPlayerId != null) {
			Mine.Reset ();
			Mine.ClientReset ();
			Mine.resetCardPosition ();

		}
		
		if (GameList.OtherPlayerId [0] != null) {
			oth0.Reset ();
			oth0.ClientReset ();
			oth0.resetCardPosition ();
		}
		
		if (GameList.OtherPlayerId [1] != null) {
			oth1.Reset ();
			oth1.ClientReset ();
			oth1.resetCardPosition ();
		}
		
		if (GameList.OtherPlayerId [2] != null) {
			oth2.Reset ();
			oth2.ClientReset ();
			oth2.resetCardPosition ();
		}

		if(GameList.MyPlayerId.Equals(GameList.RoomMasterId)){
			SendMessage ("finish");
			Error=true;
		}
		//방장이 종결메세지를 보내지 못하고 커넥션이 끊기는 경우 
		if (Error == false)
			StartCoroutine ("ErrorFinish");
	}



	IEnumerator ErrorFinish(){
		yield return new WaitForSeconds(1f);

		if(GameList.MyPlayerId.Equals(GameList.RoomMasterId)){
			SendMessage ("finish");
		}


	}




	void SendMessage(string form){
		web.sendMessage (form);
	}
	


}
