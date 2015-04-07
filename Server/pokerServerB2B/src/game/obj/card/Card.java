package game.obj.card;
/*
 *	 목적 : 카드 객체 생성 및 관리 
 *  하는 일  : 카드 객체 별 비교 
 *  	     
 */

public class Card implements Comparable<Card>
{
	private int  rank, suit;
	private String playerId;
	
	private static String[] suits = { "clubs", "hearts", "diamonds", "spades"};
	private static String[] ranks  = { "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King" ,"Ace"};

	public static String rankAsString( int __rank ) {
		return ranks[__rank];
	}

	public Card(int suit, int rank)
	{
		this.rank=rank;
		this.suit=suit;
	}

	//Getter , Setter
	public void setRank(int Rank){
		this.rank = Rank;
	}
	
	public void setSuit(int Suit){
		this.suit = Suit;
	}
	
	public int getRank() {
		 return rank;
	}

	public int getSuit() {
		return suit;
	}

	
	public String getId(){
		return playerId;
	}
	
	public void setId(String Id){
		this.playerId = Id;
	}
	
	public @Override String toString()
	{
		  return ranks[rank] + " of " + suits[suit];
	}
	

	//카드 비교 연산
	public int compareTo(Card obj){

		
		if (this.getRank() < obj.getRank()){
			//if ace
			if (this.getRank() == 1){
				return -1;
			}
			//if not ace
			return 1;
		}
		else if(this.getRank() > obj.getRank()){
			//if ace
			if (obj.getRank() == 1){
				return 1;
			}
			//if not ace
			return -1;
		}
		
		else{
			
			if(this.getSuit() < obj.getSuit()){
				return 1;
			}
			else{
				return -1;
			}
		}
	
	}
	
}
