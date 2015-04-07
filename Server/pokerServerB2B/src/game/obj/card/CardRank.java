package game.obj.card;
import java.util.ArrayList;

/*
 *	 목적 : 카드 족보 계산을 위한 구조 및 해당 족보별 카드 정렬 
 *  하는 일  : 카드 숫자 중첩 갯수 카운트 
 *  	       중복 숫자 중 모양별로 선택 정렬
 */

public class CardRank {
		
		//카드 정보
		private ArrayList<Card> cards ;
		
		//카드 중복 갯수 
		private int index = 0;
		
		public CardRank(){
			
			cards = new ArrayList<Card>();
		}

		// 카드 추가
		public void AddCards(Card card){
			cards.add(card);
		}
		// 카드 삭제
		public Card removeCards(){
			return cards.remove(0);
		}
		
		// 카드 가져가기 
		public ArrayList<Card> getCard(){
			return cards;
		}
		
		// 인텍스 증가
		public void upIndex(){
			++index;
		}
		
		// 인덱스 차감
		public void DownIndex(){
			--index;
		}
		
		public int getIndex(){
			return index;
		}
		
		public void setIndex(int index){
			this.index = index;
			
		}
		
		// 정렬 
		public void sort(){
			selectionSort(cards);
		}
		
		//선택 정렬 : 카드 정보가 7개이기 때무에 선택정렬이 더 효율적 
		public void selectionSort( ArrayList<Card> CardList){
			int lenD = CardList.size();
			int j = 0;
			Card tmp;
			for(int i=0 ; i< lenD ; i++){
				j = i;
				for(int k = i;k<lenD; k++ ){
					if( CardList.get(j).getSuit() < CardList.get(k).getSuit()){
						j = k;
					}
				}
				tmp = CardList.get(i);
				CardList.set(i, CardList.get(j));
				CardList.set(j, tmp);
			}
		}
		
}
