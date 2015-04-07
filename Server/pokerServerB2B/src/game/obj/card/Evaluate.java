package game.obj.card;

import net.netmarble.lobby.Player;
import game.obj.turn.Turn;


/*
 *	 목적 : 카드 족보 계산 
 *  하는 일  : 오픈카드 족보 계산
 *  	       모든 카드 족보계산    
 */

public class Evaluate implements Comparable<Evaluate>{
	
		//플레이어
		private Player player;
		
		//족보 플래그
		public int flag ;
		
		//족보 별 카드 정렬 
		public Card[] value;
		
		//room 에서 초기화 
		public Evaluate(Player play){
			
			this.player = play;
		}

		public String getId(){
		 return player.getId();
		}
	

		//모든패 계산 
		public void foundCard(int GameOrder){
			System.out.println("FoundCardOrder" + GameOrder);
			
			if(GameOrder >= 8)
				GameOrder =7 ;
			
			boolean flush=false;
			int flushIndex = 0;
			boolean straight=false;

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
			 CardRank[] cardRank = new CardRank[14];
			
			 //flush rank 계산 
			 CardRank[]  fCardRank = new CardRank[14];
			
			 // 모양 분석 
			int[] suits = new int[5];


			// rank 배열 

			for ( int y=0; y < 5; y++)
			{
				suits[y] = 0;
			}

			//suit 계산 

			for (int x= 1;  x < GameOrder+1 ; x++) {
				suits [player.getCard(x).getSuit()] ++;
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
			for (int initOrderIndex = 0; initOrderIndex < GameOrder ; initOrderIndex++) {
				orderedRanks [initOrderIndex] = new Card(0,0);
				value [initOrderIndex] 		  = new Card(0,0);
			}

			//flush 일 경우 따로 랭크를 만든다. 
			if (flush == true) {
				for (int findex= 1; findex < GameOrder+1; findex++) {
					if (player.getCard (findex).getSuit() == flushIndex){

						fCardRank[player.getCard (findex).getRank()].upIndex();
						fCardRank[player.getCard (findex).getRank()].AddCards(player.getCard(findex));
		
					}
				}

			} 
			
			//card 랭크 정보를 넣는다. 
			for (int x= 1 ; x< GameOrder+1; x++) {
				//card 생성 
				cardRank[player.getCard (x).getRank()].upIndex();
				cardRank[player.getCard (x).getRank()].AddCards( player.getCard(x) );

			}


			//sort
			for (int sortIndex =  0; sortIndex < 14; sortIndex++) {
				fCardRank[sortIndex].sort();
				cardRank [sortIndex].sort();

			}


			//rank  족보 계산 
			for ( int x=13; x>=1; x-- ) 
			{
				if (cardRank[x].getIndex() > sameCards) 
				{
					if (sameCards != 1 )  // 만약 같은 숫자에 에이스가 있다면 제일 큰 숫자가 된다. 
					{
						sameCards2 = sameCards;
						smallGroupRank = largeGroupRank;
					}
					
					sameCards = cardRank[x].getIndex();
					largeGroupRank = x;
					
				}				
				else if(cardRank[x].getIndex() == sameCards  && x == 1  ){
					
					sameCards2 = sameCards;
					smallGroupRank = largeGroupRank;
					sameCards = cardRank[x].getIndex();
					largeGroupRank = x;
					
				}
				
				else if (cardRank[x].getIndex() > sameCards2 )				   
				{
					sameCards2 = cardRank[x].getIndex();
					smallGroupRank = x;
				}
				else if(cardRank[x].getIndex()  == sameCards2  && x == 1  ){
					sameCards2 = cardRank[x].getIndex();
					smallGroupRank = x;
					
				}
			}




			if (flush == true) {
				//Straight 계산

				for (int x=1; x<=9; x++) 
				{

					if (fCardRank[x].getIndex() >= 1 && fCardRank[x+1].getIndex()  >= 1 && fCardRank[x+2].getIndex()  >= 1 && fCardRank[x+3].getIndex()  >= 1 && fCardRank[x+4].getIndex()  >= 1)
					{
						straight=true;
						topStraightValue=x+4;
						break;
					}
				}
				
				if (fCardRank[10].getIndex() >= 1 && fCardRank[11].getIndex() >= 1 && fCardRank[12].getIndex() >= 1 && fCardRank[13].getIndex() >= 1  && fCardRank[14].getIndex() >= 1) //ace high
				{
					straight=true;
					topStraightValue=14; 
				}
				//Straight가 아니라면 rank족보를 순서대로 정렬해서 넣는다. 
				if (! straight){

						
					while (fCardRank[1].getIndex() != 0) {
							
							Card card = fCardRank[1].removeCards();
							orderedRanks[index].setRank(14); 
							orderedRanks[index].setSuit(card.getSuit());
													
							index++;
							
							fCardRank[1].DownIndex();
					}
					
					
					
					for (int x=13; x>=2; x--) {
						
					
						while ( fCardRank[x].getIndex() != 0) {
							
							
							orderedRanks[index] = fCardRank[x].removeCards();
														
							index++;
							
							fCardRank[x].DownIndex();
						}
						
					}
				}


			} else if (flush == false) {
			
				//Straight 계산

				for (int x=1; x<=9; x++) 
				{
					if ( cardRank[x].getIndex() >= 1 && cardRank[x+1].getIndex()  >= 1 && cardRank[x+2].getIndex()  >= 1 && cardRank[x+3].getIndex()  >= 1 && cardRank[x+4].getIndex()  >= 1)
					{
						straight=true;
						topStraightValue = x+4; 
					}

				}
				
				if (cardRank[10].getIndex() >= 1 && cardRank[11].getIndex() >= 1 && cardRank[12].getIndex() >= 1 && cardRank[13].getIndex() >= 1  && cardRank[1].getIndex() >= 1) //ace high
				{
					straight=true;
					topStraightValue=14; 
				}


				// 족보를 제외한 5장 순서대로 넣기 
				if( !straight ){

					// 족보를 제외 
					if( sameCards!= 1 ){
						cardRank[largeGroupRank].setIndex(0);
					}

					if(sameCards2 != 1){
						cardRank[smallGroupRank].setIndex(0);
					}

					

						while (cardRank[1].getIndex() != 0) {
							
							Card card=  cardRank[1].removeCards();
							
							orderedRanks [index].setSuit(card.getSuit());
							
							orderedRanks [index].setRank(14);
							
							index++;
							
							cardRank[1].DownIndex();
						}
					}
					
					for (int x=13; x>=2; x--) {

						while ( cardRank[x].getIndex() != 0) {
								
								orderedRanks [index] = cardRank[x].removeCards();
								
								index++;
								
								cardRank[x].DownIndex();
						}
						
					}
				}
				
				//족보 제외 로직 끝 


			


		


			//start hand evaluation
			if ( !flush && !straight && sameCards==1 ) {

				flag =  1;

				for (int Order = 0; Order < GameOrder   ; Order++){
					value[Order]= orderedRanks[Order];
				}

			}

			
			if ( !flush && !straight && sameCards==2 && sameCards2==1)
			{

				flag = 2;

				value[0]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[1]= cardRank[largeGroupRank].removeCards(); //rank of pair
			
				for (int Order = 0; Order < GameOrder-2   ; Order++){
					value[Order+2] = orderedRanks[Order];
				}


			}


			if ( !flush && !straight && sameCards==2 && sameCards2==2) //two pair
			{

				flag = 3;

				value[0]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[1]= cardRank[largeGroupRank].removeCards(); //rank of pair

				value[2]= cardRank[smallGroupRank].removeCards(); //rank of pair
				value[3]= cardRank[smallGroupRank].removeCards(); //rank of pair
				

				for (int Order = 0; Order < GameOrder - 4   ; Order++){
					value[Order+4]= orderedRanks[Order];

				}

			}

			if ( !flush && !straight && sameCards==3 && sameCards2==1)  
			{

				flag = 4;
				
				value[0]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[1]= cardRank[largeGroupRank].removeCards(); //rank of pair	
				value[2]= cardRank[largeGroupRank].removeCards(); //rank of pair
			
				for (int Order = 0; Order < GameOrder-3  ; Order++){
					value[Order+3]= orderedRanks[Order];
				}

			}

			
			if (straight && !flush)
			{
				System.out.println("straight" +topStraightValue);
				flag =5;
				// straight 라면 
				if(topStraightValue == 14){

					value[0] = cardRank[1].removeCards();
					value[0].setRank(topStraightValue);
					
					value[1] = cardRank[13].removeCards();
					value[1].setRank(topStraightValue);

					value[2] = cardRank[12].removeCards();
					value[2].setRank(topStraightValue);

					value[3] = cardRank[11].removeCards();
					value[3].setRank(topStraightValue);
					
					value[4] = cardRank[10].removeCards();
					value[4].setRank(topStraightValue);
					
				}
				else if( topStraightValue == 5){

					
					value[0] = cardRank[topStraightValue-4].removeCards();
					value[0].setRank(topStraightValue);

					value[1] = cardRank[topStraightValue].removeCards();
					value[1].setRank(topStraightValue);

					value[2] = cardRank[topStraightValue-1].removeCards();
					value[2].setRank(topStraightValue);

					value[3] = cardRank[topStraightValue-2].removeCards();
					value[3].setRank(topStraightValue);

					value[4] = cardRank[topStraightValue-3].removeCards();
					value[4].setRank(topStraightValue);
		
				}
				else if( topStraightValue != 0){

					value[0] = cardRank[topStraightValue].removeCards();
					value[0].setRank(topStraightValue);

					value[1] = cardRank[topStraightValue-1].removeCards();
					value[1].setRank(topStraightValue);

					value[2] = cardRank[topStraightValue-2].removeCards();
					value[2].setRank(topStraightValue);

					value[3] = cardRank[topStraightValue-3].removeCards();
					value[3].setRank(topStraightValue);

					value[4] = cardRank[topStraightValue-4].removeCards();
					value[4].setRank(topStraightValue);
					
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
			
				value[0]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[1]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[2]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[3]= cardRank[smallGroupRank].removeCards(); //rank of pair
				value[4]= cardRank[smallGroupRank].removeCards(); //rank of pair
				

			}

			
			if (sameCards==4)  // poker 
			{

				flag=8;
			
				value[0]= cardRank[largeGroupRank].removeCards(); //rank of pair			
				value[1]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[2]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[3]= cardRank[largeGroupRank].removeCards();

				for (int Order = 0; Order < GameOrder-4  ; Order++){
					value[Order+4]= orderedRanks[Order];

				}

			}
			
			//High Straight
			if (straight && flush)
			{

				flag=9;
				if(topStraightValue == 14){
				
				value[0] = cardRank[1].removeCards();
				value[0].setRank(topStraightValue);
				
				value[1] = cardRank[13].removeCards();
				value[1].setRank(topStraightValue);

				value[2] = cardRank[12].removeCards();
				value[2].setRank(topStraightValue);

				value[3] = cardRank[11].removeCards();
				value[3].setRank(topStraightValue);
				
				value[4] = cardRank[10].removeCards();
				value[4].setRank(topStraightValue);
				
				}
				else if( topStraightValue == 5){
				
				value[0] = cardRank[topStraightValue-4].removeCards();
				value[0].setRank(topStraightValue);
				
				value[1] = cardRank[topStraightValue].removeCards();
				value[1].setRank(topStraightValue);

				value[2] = cardRank[topStraightValue-1].removeCards();
				value[2].setRank(topStraightValue);

				value[3] = cardRank[topStraightValue-2].removeCards();
				value[3].setRank(topStraightValue);

				
				value[4] = cardRank[topStraightValue-3].removeCards();
				value[4].setRank(topStraightValue);	
				}
				else if( topStraightValue != 0){
				
				value[0] = cardRank[topStraightValue].removeCards();
				value[0].setRank(topStraightValue);
				
				value[1] = cardRank[topStraightValue-1].removeCards();
				value[1].setRank(topStraightValue);

				value[2] = cardRank[topStraightValue-2].removeCards();
				value[2].setRank(topStraightValue);

				value[3] = cardRank[topStraightValue-3].removeCards();
				value[3].setRank(topStraightValue);

				value[4] = cardRank[topStraightValue-4].removeCards();
				value[4].setRank(topStraightValue);	
			}


			}

			
		}

		//오픈된 카드 계산 
		public void foundOpenCard(int GameOrder){
			
			int sameCards = 1;
			int sameCards2 = 1;
			int largeGroupRank=0;
			int smallGroupRank=0;
			int index=0;
						
			value = new Card[4];
			
			//족보에 따른 카드 배열 순서 
			Card[] orderedRanks = new Card[4];
			
			// rank 계산 
			CardRank[] cardRank = new CardRank[14];

			// card RANk 초기화
			for (int intialIndex = 0; intialIndex < 14; intialIndex ++) {
				cardRank [intialIndex] = new CardRank ();
			}
			
			//value order 초기화 
			for (int initOrderIndex = 0; initOrderIndex < 4 ; initOrderIndex++) {
				orderedRanks [initOrderIndex] = new Card(0,0);
				value [initOrderIndex]   = new Card(0,0);
			}

			//card 랭크 정보를 넣는다. 
			for (int x=3; x< GameOrder; x++) {
				//card 생성 
				cardRank[player.getCard(x).getRank()].upIndex();
				cardRank[player.getCard(x).getRank()].AddCards(player.getCard(x) );
				
			}
			
			
			//sort
			for (int sortIndex =  0; sortIndex < 14; sortIndex++) {
				cardRank[sortIndex].sort();
				
			}
			
			//rank  족보 계산 
			for ( int x=13; x>=1; x-- ) 
			{
				if (cardRank[x].getIndex() > sameCards) 
				{
					if (sameCards != 1 )  // 만약 같은 숫자에 에이스가 있다면 제일 큰 숫자가 된다. 
					{
						sameCards2 = sameCards;
						smallGroupRank = largeGroupRank;
					}
					
					sameCards = cardRank[x].getIndex();
					largeGroupRank = x;
					
				}				
				else if(cardRank[x].getIndex() == sameCards  && x == 1  ){
					
					sameCards2 = sameCards;
					smallGroupRank = largeGroupRank;
					sameCards = cardRank[x].getIndex();
					largeGroupRank = x;
					
				}
				
				else if (cardRank[x].getIndex() > sameCards2 )				   
				{
					sameCards2 = cardRank[x].getIndex();
					smallGroupRank = x;
				}
				else if(cardRank[x].getIndex()  == sameCards2  && x == 1  ){
					sameCards2 = cardRank[x].getIndex();
					smallGroupRank = x;
					
				}
			}
			
			
								
			// 족보를 제외 
			if( sameCards!= 1 ){
				cardRank[largeGroupRank].setIndex(0);
			}
					
			if(sameCards2 != 1){
				cardRank[smallGroupRank].setIndex(0);
			}
					
					
						
			while (cardRank[1].getIndex() != 0) {
				
				Card card =cardRank[1].removeCards();
				orderedRanks[index].setRank(14);
				orderedRanks[index].setSuit(card.getSuit());
				
				index++;
				
				cardRank[1].DownIndex();
			}
					
			for (int x=13; x>=2; x--) {
				
					while ( cardRank[x].getIndex() != 0) {
						
						
						orderedRanks [index]= cardRank[x].removeCards();
		
						index++;
						
						cardRank[x].DownIndex();
					}
			}
				

			//start hand evaluation
			if ( sameCards==1 ) {
				flag =  1;
				
				
				for (int Order = 3; Order < GameOrder   ; Order++){
					value[Order-3]= orderedRanks[Order-3];
				}
				
			}
			
			
			if ( sameCards==2 && sameCards2==1)
			{
				
				flag = 2;
				
				value[0]= cardRank[largeGroupRank].removeCards(); //rank of pair	
				value[1]= cardRank[largeGroupRank].removeCards(); //rank of pair
				
				
				for (int Order = 5; Order < GameOrder  ; Order++){
					value[Order-3]= orderedRanks[Order-5];
				}
				
				
			}
			
			
			if ( sameCards==2 && sameCards2==2) //two pair
			{
				
				flag = 3;
				
				value[0]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[1]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[2]= cardRank[smallGroupRank].removeCards(); //rank of pair
				value[3]= cardRank[smallGroupRank].removeCards(); //rank of pair
				
				

			}
			
			if ( sameCards==3)  
			{
				
				flag = 4;
				
				value[0]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[1]= cardRank[largeGroupRank].removeCards(); //rank of pair			
				value[2]= cardRank[largeGroupRank].removeCards(); //rank of pair
				
				for (int Order = 6; Order < GameOrder ; Order++){
					value[Order-3]= orderedRanks[Order-6];
					
				}
				
			}
			


			if (sameCards==4)  // poker 
			{
				flag=8;
				
				value[0]= cardRank[largeGroupRank].removeCards(); //rank of pai
				value[1]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[2]= cardRank[largeGroupRank].removeCards(); //rank of pair
				value[3]= cardRank[largeGroupRank].removeCards(); //rank of pai
				
			}




		}

		// HAND 비교 연산 
		@Override
		public int compareTo(Evaluate obj) {

			if (this.flag < obj.flag )
				return 1;
			
			else if (this.flag > obj.flag){
				return -1;
			}
			
			for (int x=0; x< value.length ; x++)
			{
				if (this.value[x].getRank() < obj.value[x].getRank())
					return 1;
				
				else if (this.value[x].getRank() > obj.value[x].getRank()) 
					return -1;
			}
			
			for ( int x=0; x< value.length; x++)
			{
				if (this.value[x].getSuit() < obj.value[x].getSuit())
					return 1;
				else if (this.value[x].getSuit() > obj.value[x].getSuit()) 
					return -1;
			}
			
			return 0;
		
		}
		
		//출력
		public String ToString(){
			String str= "";
			
			str += "playerID :"+this.player.getId() + "\n";
			
			str += "flag " + this.flag + "\n";
			
			for(int index = 0; index < value.length ; index++){
				str += "value "+index+" / Rank:" +value[index].getRank() + " / Sui t:" + value[index].getSuit() + "\n";
			}
			
			return str;
					
		}
		
		
		
		
}
