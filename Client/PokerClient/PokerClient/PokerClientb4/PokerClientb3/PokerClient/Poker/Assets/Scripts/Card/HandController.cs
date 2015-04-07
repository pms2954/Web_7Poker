using UnityEngine;
using System.Collections;

public class HandController : MonoBehaviour {

	private int DieCount = 0 ;

	public int CardNum = 0;
		
	public string clientID ;
	 
	//card
	public GameObject[] cards;

	//카드가 배치될 위치 정보
	public GameObject[] place;

	// 정적 위치 카드 
	public GameObject DefaultCard;
	


	//Button
	public CheckBehavior ChkButton; 
	 
	public CallBehavoir CallButton; 
	
	public HalfBehavoir HalfButton; 

	public AllinBehavoir AllinButton;

	public DdangBehavoir DdangButton;

	public BbingBehavoir BbingButton;

	public DieBehavoir DieButton;

	public StartButtonBehavior StartButton;


	//poker calculate
	public pokerCalculator poker;


	//Label
	public UILabel CardValue;

	public UILabel ID;

	public UILabel Cash;

	public UILabel Fee;

	public UILabel Paid;

	public UILabel Action;

	
	//Sprite
	public UISprite Winner; 

	public UISprite Allin;

	public UISprite Character;

	public UISprite BG;

	
	public void Reset(){
		CardController defaultCard = DefaultCard.GetComponent<CardController> ();


		for (int i =0; i< cards.Length; i++) {
			CardController cardController = cards [i].GetComponent<CardController> ();
			cardController.cardIndex = 0;
			cardController.HideCard ();
		}
		defaultCard.HideCard (); 

		Winner.enabled = false;
		Allin.enabled = false;

		Action.text = "";
		Paid.text = "";
		CardValue.text = "";
		CardNum = 0;

		if (clientID.Equals (GameList.MyPlayerId)) {
			ChkButton.ButtonReset ();
			CallButton.ButtonReset ();		
			HalfButton.ButtonReset ();		
			AllinButton.ButtonReset ();		
			DdangButton.ButtonReset ();		
			BbingButton.ButtonReset ();
			DieButton.ButtonReset ();
			Fee.text= "";


		} 
		
	}

	public void StartReset(){
		CardController defaultCard = DefaultCard.GetComponent<CardController> ();

		
		for (int i =0; i< cards.Length; i++) {
			CardController cardController = cards [i].GetComponent<CardController> ();
			cardController.cardIndex = 0;
			cardController.ShowBackground ();
		}
	
		defaultCard.ShowBackground ();
	}

	public void ClientReset(){
		Player play = (Player)GameList.PlayList [clientID];
		play.reset ();
	}

	public void DisbuteCard(){

		StartCoroutine ("MoveCard");

	}


	//카드 1장 추가
	public void AddCard( ){

		Player play = (Player)GameList.PlayList [clientID];

		string state = play.printState ();


		int rank;


		int order = GameList.GameOrder - 1;
				
		if (state == null || !state.Equals ("Died")) {

			CardController cardController = cards [order].GetComponent<CardController> ();
		
			if (play.getCard (order).CardRank == 1)
				rank = 12;
			else 
				rank = play.getCard (order).CardRank - 2;
		
			cardController.cardIndex = ((play.getCard (order).CardSuit - 1) * 13) + rank;

			//마지막장은 히든 
			if (GameList.GameOrder != 7){
				cardController.ShowFace ();
			}
			else{ 
				cardController.ShowBackground ();

			}
		}

		play.resetForTurn ();
		GameList.TurnReset ();
		resetCashInfo ();
	
	}
	
	//카드 4장 추가
	public void AddCards(){

		Player play = (Player)GameList.PlayList [clientID];

		int rank;

		for (int index =0; index < 4; index++) {

			CardController cardController = cards [index].GetComponent<CardController> ();

			//card 정보 변환
			if(play.getCard(index).CardRank == 1)
				rank = 12;
			else 
				rank = play.getCard(index).CardRank - 2 ;

			cardController.cardIndex = ((play.getCard(index).CardSuit - 1) * 13) + rank ;

			// 다른 사용자 카드 2 장은 보여주지 않는다.
			if(!clientID.Equals(GameList.MyPlayerId) && index < 2 )
				cardController.ShowBackground();
			else 
				cardController.ShowFace();
		}

	

	}

	//카드 갱신
	public void refreshCard(){

		CardController card;

		Player play = (Player)GameList.PlayList [clientID];
		
		int rank;

		int index = 0;

		int nullIndex = -1;


		for ( index =0; play.getCard(index) != null ; index++) {
			CardController cardController = cards [index].GetComponent<CardController> ();
			
			//card 정보 변환
			if(play.getCard(index).CardRank == 1)
				rank = 12;
			else 
				rank = play.getCard(index).CardRank - 2 ;

			if(((play.getCard(index).CardSuit - 1) * 13) + rank < 0){
				nullIndex =index;
				cardController.cardIndex = 0;
			}
			else {
			cardController.cardIndex = ((play.getCard(index).CardSuit - 1) * 13) + rank ;
			}
			if(!clientID.Equals(GameList.MyPlayerId) )
				cardController.ShowBackground();
			else 
				cardController.ShowFace();
		}

		if (nullIndex != -1){
	
			card = cards [nullIndex].GetComponent<CardController> ();
	
			card.HideCard ();

			//4번째 카드를 제자리에 갖다 둔다. 
		}
	}

	//4번째  카드 회수 
	public void refreshFourthCard(){
		
		if (CardNum == 4)
			CardNum = 3;

		Vector3 tmp = new Vector3 (DefaultCard.transform.position.x , DefaultCard.transform.position.y  , 0);

		cards [3].transform.position = tmp;

		CardController cardController = cards [3].GetComponent<CardController> ();

		cardController.ShowBackground ();

	
	}

	//카드 리포지션 
	public void resetCardPosition(){

		for (int index= 0; index< 7; index++) {

			float f =0.0f ;

			Vector3 tmp = new Vector3 ( DefaultCard.transform.position.x , DefaultCard.transform.position.y  , 0 );

			cards [index].transform.position = tmp;
			
			CardController cardController = cards [index].GetComponent<CardController> ();
			
			cardController.HideCard ();

		}


	}

	// 족보계산 
	public void Poker(){

		//현재 패 의 족보를 계산 
		poker.foundCard (clientID);

		// 계산된 족보를 보여준다.
		CardValue.text = poker.display ();

	}

	//플레이어 인포 보여주기 
	public void AddMe( string MyId ){
		
		clientID = MyId;
	
		Player play = (Player)GameList.PlayList [clientID];

		ID.text = play.printId() ;

		Cash.text = play.printCash().ToString() ;

		Character.enabled = true;
		BG.enabled = true;


		if (clientID.Equals (GameList.RoomMasterId) && clientID.Equals(GameList.MyPlayerId)) {

			StartTheGame ();
		}
	}

	// 턴 마다 버튼 클릭 
	public void TurnDefault (){

		Player play = (Player)GameList.PlayList [clientID];
		string state = play.printState();
		// 콜 , 다이 ,따당  하프 활성화 
		string Doing = GameList.canDo;
			 
		if (GameList.Turn.Equals(GameList.MyPlayerId) && Doing.Equals ("Default")) {

			//만약에 낼 돈이 없다면  비활성화 시킨다. 
			DieButton.ButtonEnable ();
			DdangButton.ButtonEnable ();
			HalfButton.ButtonEnable ();
			CallButton.ButtonEnable ();


			//올인상태 체크
			if(state == null){
				AllinCheck ();
			}
			else{
			//이미 올인이면 콜하고 다이만 활성화 
				if(state.Equals("Allin")){
					HalfButton.ButtonDisable();
					DdangButton.ButtonDisable ();

				}
			}

			GameList.Turn = null;

		} else if ( GameList.Turn.Equals(GameList.MyPlayerId) && Doing.Equals ("Restrict")) { 
				
			DieButton.ButtonEnable ();
			CallButton.ButtonEnable ();

			if(state == null){
				AllinCheck ();
			}
	
			GameList.Turn = null;
				

		} 
		else{
				ChkButton.ButtonEnable ();
				ChkButton.ButtonDisable();

				DieButton.ButtonEnable ();
				DieButton.ButtonDisable();

				DdangButton.ButtonEnable ();
				DdangButton.ButtonDisable();

				HalfButton.ButtonEnable ();
				HalfButton.ButtonDisable();

				BbingButton.ButtonEnable ();
				BbingButton.ButtonDisable();

				CallButton.ButtonEnable ();
				CallButton.ButtonDisable();
		}

	}

	// 첫번째 턴일때 
	public void TurnFirst(){
		Player play = (Player)GameList.PlayList [clientID];
		string state = play.printState();

		// 오직 띵 , 하프, 다이, 체크  활성화 

		if (GameList.Turn.Equals(GameList.MyPlayerId)){

			ChkButton.ButtonEnable ();
			BbingButton.ButtonEnable();
			DieButton.ButtonEnable ();
			HalfButton.ButtonEnable ();
	
			DdangButton.ButtonEnable ();
			DdangButton.ButtonDisable ();

			CallButton.ButtonEnable ();
			CallButton.ButtonDisable();
			
			DdangButton.ButtonEnable ();
			DdangButton.ButtonDisable();

			if (state ==null){
				AllinCheck();
			}

			else if(state != null && state.Equals("Allin")){
				//다른 버튼 비 활성화
				HalfButton.ButtonDisable();
				DdangButton.ButtonDisable ();
				BbingButton.ButtonDisable();
				// 오직 다이와 콜 버튼 활성화 
				CallButton.ButtonDisable ();
			}
			GameList.Turn = null;
			
		}
	
		else{
			ChkButton.ButtonEnable ();
			ChkButton.ButtonDisable();
			
			DieButton.ButtonEnable ();
			DieButton.ButtonDisable();
			
			DdangButton.ButtonEnable ();
			DdangButton.ButtonDisable();
			
			HalfButton.ButtonEnable ();
			HalfButton.ButtonDisable();
			
			BbingButton.ButtonEnable ();
			BbingButton.ButtonDisable();
			
			CallButton.ButtonEnable ();
			CallButton.ButtonDisable();
		}
	}

	// 모든 버튼 없애기 
	public void AllinButtonDisable (){
		ChkButton.ButtonDisable();
		DieButton.ButtonDisable();
		DdangButton.ButtonDisable();
		HalfButton.ButtonDisable();
		BbingButton.ButtonDisable();
		CallButton.ButtonDisable();
		AllinButton.ButtonDisable ();
	
	}

	// 제일 처음 시작 시 
	void StartTheGame(){
		if ( GameList.UserConunt > 1  ) {
			StartButton.ButtonEnable();// STart button 활성화 
		}
	}

	// 모든 카드 보여주기 
	public void showAllCard(){

		//모든 정보 보여주면서 원래 Fee영역에  영역에 돈 지불 내역 표출 하기 
		resetCashInfo ();

		Player play = (Player)GameList.PlayList [clientID];

		int profit  = play.getMyprofit ();

		string state = play.printState ();


		if (profit > 0)
			Paid.text = " + " + profit.ToString() ;
		else 
			Paid.text =profit.ToString ();

		if (state ==null || !state.Equals("Died")){
			//현재 패 의 족보를 계산 
			poker.foundCard (clientID);
			
			// 계산된 족보를 보여준다.
			CardValue.text = poker.display ();

			//각 포커 계산 내역 보여주기 
			for (int index =0; index < GameList.GameOrder ; index++) {
				CardController cardController = cards [index].GetComponent<CardController> ();
				cardController.ShowFace();
			}
		}

		if(clientID.Equals(GameList.Winner))
			Winner.enabled = true;

	}
	// 캐쉬 정보 관련 정보 보여주기 
	public void setCashInfo(){

		Player play = (Player)GameList.PlayList [clientID];

		string state = play.printState ();

		ID.text = play.printId() ;
		
		//cash 보여주 기
		Cash.text = play.printCash().ToString() ;

		string doing = Def.ConvertString(play.getDoing());

		Action.text = doing;
		
		if (clientID.Equals (GameList.MyPlayerId)) {
			Fee.text = "총 배팅액 : " + play.getTotalFee ().ToString ();
		}

		else if(play.getCurrentFee() != 0 ) 
		 	Paid.text =play.getCurrentFee().ToString() + "지불";

		//올인상태 체크
		if(state != null && state.Equals("Allin") ){
			Allin.enabled = true;
		}

		else if (state !=null && state.Equals("Died")){
			
			AllcardClose();
			//카드를 덮고  nothing 모든걸 비 활성 화 
		}

		 
		// 만약 내 자신이라면 총액을 보여준다. 
	}

	public void resetCashInfo(){
	
		Player play = (Player)GameList.PlayList [clientID];

		string state = play.printState ();

		//cash 보여주 기
		Cash.text = play.printCash().ToString() ;

		Action.text = "";

		if (clientID.Equals (GameList.MyPlayerId)) {
			Fee.text = "총 배팅액 : " + play.getTotalFee ().ToString ();
		}
		
		Paid.text = "";

		//올인상태 체크
		if(state != null && state.Equals("Allin") ){
			Allin.enabled = true;
		}

		else if (state !=null && state.Equals("Died")){
			AllcardClose();
			//카드를 덮고  nothing 모든걸 비 활성 화 
		}



	}

	public void AllinCheck(){
		// 콜 , 삥, 하프 , 따당 
		bool AllinFlag = false;

		Player play = (Player)GameList.PlayList [clientID];

		int myCash = play.printCash ();
		//가진 금액이 call 할 금액 보다 작으면 
		if (myCash < GameList.CallFee) {
			CallButton.ButtonDisable();
		}

		// 가진 금액이 판돈 보다 작으면
		if (myCash < Def.BBINGVALUE) {
			BbingButton.ButtonDisable();
		} 

		//가진 금액이 따당할 금액 보다 작으면 
		if (myCash < GameList.CallFee * 2 || GameList.CallFee  == 0 ) {
			DdangButton.ButtonDisable();
		}

		// 가진 금액이 하프할 금액보다 작으면 
		if (myCash < GameList.CallFee + (GameList.CallFee + GameList.TotalFee) / 2) {
			AllinFlag = true;
		}

		int Total = GameList.CallFee + (GameList.CallFee + GameList.TotalFee) / 2;

		if (AllinFlag == true) {
			HalfButton.ButtonDisable();
			AllinButton.ButtonEnable();
		}

	}
	//모든 카드 다이처리
	public void AllcardClose(){

		if (DieCount == 0) {
			DieCount = GameList.GameOrder;
		}
		//각 포커 계산 내역 보여주기 
		for (int index =0; index < DieCount ; index++) {
			CardController cardController = cards [index].GetComponent<CardController> ();
			cardController.ShowOutline();
		}

	}

	//사용자 커넥션 끊길시 
	public void leveForReset(){

		GameList.PlayList.Remove (clientID);
		Reset ();
		resetCardPosition ();
		clientID = "";
		ID.text ="";
		Cash.text ="";
		Character.enabled = false;
		BG.enabled = false;
	
	}


	// Use this for initialization
	void Start(){
		Reset ();
		Character.enabled = false;
		BG.enabled = false;

	}
	//카드 움직이기 액션 
	IEnumerator MoveCard()
	{
			int loop = 0;

			while (true) {
			yield return new WaitForSeconds (0.01f);

			float xloop = (place[CardNum].transform.position.x - cards[CardNum].transform.position.x )/30f;
			float yloop = (place[CardNum].transform.position.y - cards[CardNum].transform.position.y -1 )/30f;
			float zloop = (place[CardNum].transform.position.z - cards[CardNum].transform.position.z )/30f;

			//Vector3 Dir = new Vector3 (place.transform.position.x, place.transform.position.y, place.transform.position.z);
			//	Dir = Dir.normalized;
			cards[CardNum].transform.position +=new Vector3(xloop, yloop, zloop);

			if (loop==30)
				break;

				loop++;
			}
			CardNum++;

	}



	

}
