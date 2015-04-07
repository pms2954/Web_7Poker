using UnityEngine;
using System.Collections;
using System.Collections.Generic;

/*
	클래스 목적 : CARD객체 별 족보 계산을 위한 구조  
	하는 일 :  CARD 중복 숫자별 정렬 및 순서 유지 
 */
public class CardRank  {
	
	public List<Card> card ;
	public int index = 0;
	
	public CardRank(){
		
		card = new List<Card>();
	}

}
/*
	클래스 목적 : 족보 계산  
	하는 일 :  족보계산 
 */

public class pokerCalculator : MonoBehaviour {

	public int flag ;

	public Card[] value;

	public CardRank[] cardRank;
	public CardRank[] fCardRank;


	//모든패 계산 
	public void foundCard(string Id){

		Player play = (Player)GameList.PlayList [Id];

		bool flush=false;
		int flushIndex = 0;
		bool straight=false;

		int sameCards = 1;
		int sameCards2 = 1;
		int largeGroupRank=0;
		int smallGroupRank=0;
		int index=0;
		int topStraightValue=0;

		//기존 가지고 있는 카드 순서
		value = new Card[7];

		//족보에 따른 카드 배열 순서 
		Card[] orderedRanks = new Card[7];

		// rank 계산 
		//int[] ranks = new int[14];
		cardRank = new CardRank[14];
		//flush rank 계산 
		//int[] fRanks = new int[14];
		fCardRank = new CardRank[14];
		// 모양 분석 
		int[] suits = new int[5];


		// rank 배열 


		for ( int y=0; y < 5; y++)
		{
			suits[y] = 0;
		}

		//suit 계산 
		for (int x= 0;  x < GameList.GameOrder ; x++) {
			suits [ play.getCard(x).CardSuit ] ++;
		}



		for (int suitIndex = 0 ; suitIndex < 5; suitIndex++){
			if( suits[suitIndex] >= 5 ){
				flush = true;
				flushIndex = suitIndex ;
			}
		}

		// card RANk 초기화
		for (int intialIndex = 0; intialIndex < 14; intialIndex ++) {
			fCardRank [intialIndex] = new CardRank ();
			cardRank [intialIndex] = new CardRank ();

		}

		//value order 초기화 
		for (int initOrderIndex = 0; initOrderIndex < GameList.GameOrder ; initOrderIndex++) {
			orderedRanks [initOrderIndex] = new Card(0,0);
			value [initOrderIndex] 		  = new Card(0,0);
		}


		//flush 일 경우 따로 랭크를 만든다. 
		if (flush == true) {
			for (int findex= 0; findex < GameList.GameOrder; findex++) {
				//flush 는  fRanks
				if (play.getCard (findex).CardSuit == flushIndex){

					fCardRank[play.getCard (findex).CardRank].index++;
					fCardRank[play.getCard (findex).CardRank].card.Add(play.getCard (findex));

				}
			}

		} 

		//card 랭크 정보를 넣는다. 
		for (int x=0; x< GameList.GameOrder; x++) {
			//card 생성 

			cardRank[play.getCard (x).CardRank].index++;
			cardRank[play.getCard (x).CardRank].card.Add( play.getCard (x) );

		}


		//sort
		for (int sortIndex =  0; sortIndex < 14; sortIndex++) {
			selectionSort(fCardRank [sortIndex].card);
			selectionSort(cardRank [sortIndex].card);

		}


		//rank  족보 계산 
		for ( int x=13; x>=1; x-- ) 
		{
			if (cardRank[x].index > sameCards) 
			{
				if (sameCards != 1 )  // 만약 같은 숫자에 에이스가 있다면 제일 큰 숫자가 된다. 
				{
					sameCards2 = sameCards;
					smallGroupRank = largeGroupRank;
				}
				
				sameCards = cardRank[x].index;
				largeGroupRank = x;
				
			}					
			else if(cardRank[x].index == sameCards  && x == 1  ){
				
				sameCards2 = sameCards;
				smallGroupRank = largeGroupRank;
				sameCards = cardRank[x].index;
				largeGroupRank = x;
				
			}
			
			else if (cardRank[x].index > sameCards2 )				   
			{
				sameCards2 = cardRank[x].index;
				smallGroupRank = x;
			}
			else if(cardRank[x].index  == sameCards2  && x == 1  ){
				sameCards2 = cardRank[x].index;
				smallGroupRank = x;
				
			}
		}




		if (flush == true) {
			//frank를 먼저 넣는다.

			for (int x=1; x<=9; x++) //can't have straight with lowest value of more than 10
			{
				if (fCardRank[x].index >= 1 && fCardRank[x+1].index  >= 1 && fCardRank[x+2].index  >= 1 && fCardRank[x+3].index  >= 1 && fCardRank[x+4].index  >= 1)
				{
					straight=true;
					topStraightValue=x+4; //4 above bottom value
					break;
				}
			}
			
			if (fCardRank[10].index >= 1 && fCardRank[11].index >= 1 && fCardRank[12].index >= 1 && fCardRank[13].index >= 1  && fCardRank[14].index >= 1) //ace high
			{
				straight=true;
				topStraightValue=14; //higher than king
			}


			if (! straight){

				if (fCardRank[1].index != 0) {
					
					
					while (fCardRank[1].index != 0) {
						
						orderedRanks [index].CardRank = 14;
						
						orderedRanks [index].CardSuit = fCardRank[1].card[0].CardSuit;
						
						fCardRank[1].card.RemoveAt(0);
						
						index++;
						
						fCardRank[1].index --;
					}
				}
				
				for (int x=13; x>=2; x--) {
					
					if (fCardRank[x].index != 0) { //if ace, run this before because ace is highest card
						while ( fCardRank[x].index != 0) {
							
							orderedRanks [index].CardRank = x;
							
							orderedRanks [index].CardSuit = fCardRank[x].card[0].CardSuit;
							
							fCardRank[x].card.RemoveAt(0);
							
							index++;
							
							fCardRank[x].index --;
						}
					}
				}
			}


		} else if (flush == false) {
		

			for (int x=1; x<=9; x++) //can't have straight with lowest value of more than 10
			{
				if ( cardRank[x].index >= 1 && cardRank[x+1].index  >= 1 && cardRank[x+2].index  >= 1 && cardRank[x+3].index  >= 1 && cardRank[x+4].index  >= 1)
				{
					straight=true;
					topStraightValue = x+4; //4 above bottom value
				}

			}
			
			if (cardRank[10].index >= 1 && cardRank[11].index >= 1 && cardRank[12].index >= 1 && cardRank[13].index >= 1  && cardRank[1].index >= 1) //ace high
			{
				straight=true;
				topStraightValue=14; //higher than king
			}


			// 족보를 제외한 5장 만들기 
			// straight 아니라면 
			// saraight 지만 포카드 나 풀하우스인 경우 
			if( !straight ){
				
				// 족보를 제외 
				if( sameCards!= 1 ){
					cardRank[largeGroupRank].index = 0;
				}

				if(sameCards2 != 1){
					cardRank[smallGroupRank].index = 0;
				}

				
				if (cardRank[1].index != 0) {
					
					
					while (cardRank[1].index != 0) {
						
						orderedRanks [index].CardRank = 14;
						
						orderedRanks [index].CardSuit = cardRank[1].card[0].CardSuit;
						
						cardRank[1].card.RemoveAt(0);
						
						index++;
						
						cardRank[1].index --;
					}
				}
				
				for (int x=13; x>=2; x--) {
					
					if (cardRank[x].index != 0) { //if ace, run this before because ace is highest card
						while ( cardRank[x].index != 0) {
							
							orderedRanks [index].CardRank = x;
							
							orderedRanks [index].CardSuit = cardRank[x].card[0].CardSuit;
							
							cardRank[x].card.RemoveAt(0);
							
							index++;
							
							cardRank[x].index --;
						}
					}
				}
			}
			
			//족보 제외 로직 끝 


		}


	

	

		//start hand evaluation
		if ( !flush && !straight && sameCards==1 ) {
			flag =  1;


			for (int Order = 0; Order < GameList.GameOrder   ; Order++){
				value[Order].CardRank= orderedRanks[Order].CardRank;
				value[Order].CardSuit= orderedRanks[Order].CardSuit;
			}

		}

		
		if ( !flush && !straight && sameCards==2 && sameCards2==1)
		{

			flag = 2;

			value[0].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[0].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair


			cardRank[largeGroupRank].card.RemoveAt(0);

			value[1].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[1].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair

			cardRank[largeGroupRank].card.RemoveAt(0);


			for (int Order = 0; Order < GameList.GameOrder-2   ; Order++){
				value[Order+2].CardRank= orderedRanks[Order].CardRank;
				value[Order+2].CardSuit= orderedRanks[Order].CardSuit;
			}


		}


		if ( !flush && !straight && sameCards==2 && sameCards2==2) //two pair
		{

			flag = 3;

			value[0].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[0].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);
			
			value[1].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[1].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);

			value[2].CardRank= cardRank[smallGroupRank].card[0].CardRank; //rank of pair
			value[2].CardSuit= cardRank[smallGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[smallGroupRank].card.RemoveAt(0);
			
			value[3].CardRank= cardRank[smallGroupRank].card[0].CardRank; //rank of pair
			value[3].CardSuit= cardRank[smallGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[smallGroupRank].card.RemoveAt(0);

			for (int Order = 0; Order < GameList.GameOrder - 4   ; Order++){
				value[Order+4].CardRank= orderedRanks[Order].CardRank;
				value[Order+4].CardSuit= orderedRanks[Order].CardSuit;

			}

		}

		if ( !flush && !straight && sameCards==3 && sameCards2==1)  
		{

			flag = 4;
			
			value[0].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[0].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);
			
			value[1].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[1].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);
			
			value[2].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[2].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);

		
			for (int Order = 0; Order < GameList.GameOrder-3  ; Order++){
				value[Order+3].CardRank= orderedRanks[Order].CardRank;
				value[Order+3].CardSuit= orderedRanks[Order].CardSuit;

			}

		}

		
		if (straight && !flush)
		{
			flag =5;
			// straight 라면 
			if(topStraightValue == 14){

				value[0].CardRank = topStraightValue;
				value[0].CardSuit = cardRank[1].card[0].CardSuit;
				cardRank[1].card.RemoveAt(0);
				
				value[1].CardRank = topStraightValue;
				value[1].CardSuit = cardRank[13].card[0].CardSuit;
				cardRank[13].card.RemoveAt(0);
				
				value[2].CardRank = topStraightValue;
				value[2].CardSuit = cardRank[12].card[0].CardSuit;
				cardRank[12].card.RemoveAt(0);
				
				value[3].CardRank = topStraightValue;
				value[3].CardSuit = cardRank[11].card[0].CardSuit;
				cardRank[11].card.RemoveAt(0);
				
				value[4].CardRank = topStraightValue;
				value[4].CardSuit = cardRank[10].card[0].CardSuit;
				cardRank[10].card.RemoveAt(0);
				
			}
			else if( topStraightValue == 5){
				
				value[0].CardRank = topStraightValue;
				value[0].CardSuit = cardRank[topStraightValue-4].card[0].CardSuit;
				cardRank[topStraightValue-4].card.RemoveAt(0);
				
				value[1].CardRank = topStraightValue;
				value[1].CardSuit = cardRank[topStraightValue].card[0].CardSuit;
				cardRank[topStraightValue].card.RemoveAt(0);
				
				value[2].CardRank = topStraightValue;
				value[2].CardSuit = cardRank[topStraightValue-1].card[0].CardSuit;
				cardRank[topStraightValue-1].card.RemoveAt(0);
				
				value[3].CardRank = topStraightValue;
				value[3].CardSuit = cardRank[topStraightValue-2].card[0].CardSuit;
				cardRank[topStraightValue-2].card.RemoveAt(0);
				
				value[4].CardRank = topStraightValue;
				value[4].CardSuit = cardRank[topStraightValue-3].card[0].CardSuit;
				cardRank[topStraightValue-3].card.RemoveAt(0);		
			}
			else if( topStraightValue != 0){
				
				value[0].CardRank = topStraightValue;
				value[0].CardSuit = cardRank[topStraightValue].card[0].CardSuit;
				cardRank[topStraightValue].card.RemoveAt(0);
				
				value[1].CardRank = topStraightValue;
				value[1].CardSuit = cardRank[topStraightValue-1].card[0].CardSuit;
				cardRank[topStraightValue-1].card.RemoveAt(0);
				
				value[2].CardRank = topStraightValue;
				value[2].CardSuit = cardRank[topStraightValue-2].card[0].CardSuit;
				cardRank[topStraightValue-2].card.RemoveAt(0);
				
				value[3].CardRank = topStraightValue;
				value[3].CardSuit = cardRank[topStraightValue-3].card[0].CardSuit;
				cardRank[topStraightValue-3].card.RemoveAt(0);
				
				value[4].CardRank = topStraightValue;
				value[4].CardSuit = cardRank[topStraightValue-4].card[0].CardSuit;
				cardRank[topStraightValue-4].card.RemoveAt(0);		
			}

		}
		
		if (flush && !straight)
		{
			flag =6;
			value[0]=orderedRanks[0]; //tie determined by ranks of cards
			value[1]=orderedRanks[1];
			value[2]=orderedRanks[2];
			value[3]=orderedRanks[3];
			value[4]=orderedRanks[4];


			
		}
		
		if (sameCards==3 && sameCards2 >= 2)// full house
		{

			flag = 7;
		
			value[0].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[0].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);
			
			value[1].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[1].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);
			
			value[2].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[2].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);
			
			
			value[3].CardRank= cardRank[smallGroupRank].card[0].CardRank; //rank of pair
			value[3].CardSuit= cardRank[smallGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[smallGroupRank].card.RemoveAt(0);
			

			value[4].CardRank= cardRank[smallGroupRank].card[0].CardRank; //rank of pair
			value[4].CardSuit= cardRank[smallGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[smallGroupRank].card.RemoveAt(0);

		}

		
		if (sameCards==4)  // poker 
		{
			flag=8;
		
			value[0].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[0].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);
			
			value[1].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[1].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);
			
			value[2].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[2].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);
			
			
			value[3].CardRank= cardRank[largeGroupRank].card[0].CardRank; //rank of pair
			value[3].CardSuit= cardRank[largeGroupRank].card[0].CardSuit; //rank of pair
			
			cardRank[largeGroupRank].card.RemoveAt(0);

			
			
			for (int Order = 0; Order < GameList.GameOrder-4  ; Order++){
				value[Order+4].CardRank= orderedRanks[Order].CardRank;
				value[Order+4].CardSuit= orderedRanks[Order].CardSuit;

			}

		}
		
		if (straight && flush)
		{
			flag=9;
		if(topStraightValue == 14){
			
			value[0].CardRank = topStraightValue;
			value[0].CardSuit = fCardRank[1].card[0].CardSuit;
			fCardRank[1].card.RemoveAt(0);
			
			value[1].CardRank = topStraightValue;
			value[1].CardSuit = fCardRank[13].card[0].CardSuit;
			fCardRank[13].card.RemoveAt(0);
			
			value[2].CardRank = topStraightValue;
			value[2].CardSuit = fCardRank[12].card[0].CardSuit;
			fCardRank[12].card.RemoveAt(0);
			
			value[3].CardRank = topStraightValue;
			value[3].CardSuit = fCardRank[11].card[0].CardSuit;
			fCardRank[11].card.RemoveAt(0);
			
			value[4].CardRank = topStraightValue;
			value[4].CardSuit = fCardRank[10].card[0].CardSuit;
			fCardRank[10].card.RemoveAt(0);
			
		}
		else if( topStraightValue == 5){
			
			value[0].CardRank = topStraightValue;
			value[0].CardSuit = fCardRank[topStraightValue-4].card[0].CardSuit;
			fCardRank[topStraightValue-4].card.RemoveAt(0);
			
			value[1].CardRank = topStraightValue;
			value[1].CardSuit = fCardRank[topStraightValue].card[0].CardSuit;
			fCardRank[topStraightValue].card.RemoveAt(0);
			
			value[2].CardRank = topStraightValue;
			value[2].CardSuit = fCardRank[topStraightValue-1].card[0].CardSuit;
			fCardRank[topStraightValue-1].card.RemoveAt(0);
			
			value[3].CardRank = topStraightValue;
			value[3].CardSuit = fCardRank[topStraightValue-2].card[0].CardSuit;
			fCardRank[topStraightValue-2].card.RemoveAt(0);
			
			value[4].CardRank = topStraightValue;
			value[4].CardSuit = fCardRank[topStraightValue-3].card[0].CardSuit;
			fCardRank[topStraightValue-3].card.RemoveAt(0);		
		}
		else if( topStraightValue != 0){
			
			value[0].CardRank = topStraightValue;
			value[0].CardSuit = fCardRank[topStraightValue].card[0].CardSuit;
			fCardRank[topStraightValue].card.RemoveAt(0);
			
			value[1].CardRank = topStraightValue;
			value[1].CardSuit = fCardRank[topStraightValue-1].card[0].CardSuit;
			fCardRank[topStraightValue-1].card.RemoveAt(0);
			
			value[2].CardRank = topStraightValue;
			value[2].CardSuit = fCardRank[topStraightValue-2].card[0].CardSuit;
			fCardRank[topStraightValue-2].card.RemoveAt(0);
			
			value[3].CardRank = topStraightValue;
			value[3].CardSuit = fCardRank[topStraightValue-3].card[0].CardSuit;
			fCardRank[topStraightValue-3].card.RemoveAt(0);
			
			value[4].CardRank = topStraightValue;
			value[4].CardSuit = fCardRank[topStraightValue-4].card[0].CardSuit;
			fCardRank[topStraightValue-4].card.RemoveAt(0);		
		}

			
	

		}

		GameList.value = this.value;
		GameList.flag = flag;
		
		
	}

	public string display()
	{
		string Cardvalue = "";
		switch(flag)
		{
			
		case 1:
			Cardvalue=" Top";
			break;
		case 2:
			Cardvalue=" One pair" ;
			break;
		case 3:
			Cardvalue=" Two pair";
			break;
		case 4:
			Cardvalue=" Three of a kind " ;
			break;
		case 5:
			Cardvalue= " High straight" ;
			break;
		case 6:
			Cardvalue="Flush";
			break;
		case 7:
			Cardvalue="FullHouse";
			break;
		case 8:
			Cardvalue="FourCard";
			break;
		case 9:
			Cardvalue="StraightFlush";
			break;
		}

		/*
		for (int GameOrder = 0; GameOrder < GameList.GameOrder; GameOrder++) {

			Cardvalue += "/" + AsString (value [GameOrder]) + "/   ";
			
		}
		*/


		return Cardvalue;

	}
	

	public string AsString(Card Card){

		string value = "" ;
		string suit = "";
		switch( Card.CardRank ){
			case 0:
			value ="Error";
				break;
			case 1: 
				value ="1";
				break;
			case 2: 
				value ="2" ;
				break;
			case 3 : 
				value ="3";
				break;
			case 4: 
				value ="4";
				break;
			case 5: 
				value ="5" ;
				break;
			case 6 : 
				value ="6";
				break;			
			case 7: 
				value ="7";
				break;
			case 8: 
				value ="8" ;
				break;
			case 9 : 
				value ="9";
				break;
			case 10: 
				value ="10";
				break;
			case 11: 
				value ="J" ;
				break;
			case 12 : 
				value ="Q";
				break;
			case 13: 
				value ="K";
				break;
			case 14: 
				value ="A";
				break;
		}

		switch (Card.CardSuit) {
		case 0:
			suit ="   Error";
			break;
		case 1:
			suit ="  clover";
			break;
		case 2:
			suit ="  heart";
			break;
		case 3:
			suit ="  diamond";
			break;
		case 4:
			suit ="  spade";
			break;
		}

		return suit + value;
	
	}

	public void selectionSort( List<Card> CardList){
		int lenD = CardList.Count;
		int j = 0;
		Card tmp;
		for(int i=0 ; i< lenD ; i++){
			j = i;
			for(int k = i;k<lenD; k++ ){
				if( CardList[j].CardSuit < CardList[k].CardSuit){
					j = k;
				}
			}
			tmp = CardList[i];
			CardList[i] = CardList[j];
			CardList[j] = tmp;
		}
	}
	

}



