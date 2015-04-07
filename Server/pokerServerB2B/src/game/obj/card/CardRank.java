package game.obj.card;
import java.util.ArrayList;

/*
 *	 ���� : ī�� ���� ����� ���� ���� �� �ش� ������ ī�� ���� 
 *  �ϴ� ��  : ī�� ���� ��ø ���� ī��Ʈ 
 *  	       �ߺ� ���� �� ��纰�� ���� ����
 */

public class CardRank {
		
		//ī�� ����
		private ArrayList<Card> cards ;
		
		//ī�� �ߺ� ���� 
		private int index = 0;
		
		public CardRank(){
			
			cards = new ArrayList<Card>();
		}

		// ī�� �߰�
		public void AddCards(Card card){
			cards.add(card);
		}
		// ī�� ����
		public Card removeCards(){
			return cards.remove(0);
		}
		
		// ī�� �������� 
		public ArrayList<Card> getCard(){
			return cards;
		}
		
		// ���ؽ� ����
		public void upIndex(){
			++index;
		}
		
		// �ε��� ����
		public void DownIndex(){
			--index;
		}
		
		public int getIndex(){
			return index;
		}
		
		public void setIndex(int index){
			this.index = index;
			
		}
		
		// ���� 
		public void sort(){
			selectionSort(cards);
		}
		
		//���� ���� : ī�� ������ 7���̱� ������ ���������� �� ȿ���� 
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
