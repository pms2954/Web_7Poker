package game.obj.card;

import java.util.Random;
import java.util.ArrayList;
import game.obj.card.Card;
/*
 *	 목적 : 카드 52장을 전체 관리 , 중복된 카드가 나오지 않도록 관리
 *  하는 일  : 카드 셔플 
 *  	       
 */

public class Deck {
	private ArrayList<Card> cards;

    public Deck(){
		cards = new ArrayList<Card>();
		int index_1, index_2;
		Random generator = new Random();
		Card temp;

		for (int a=1; a<=4; a++)
		{
			for (int b=1; b<=13; b++)
			 {
			   cards.add( new Card(a,b) );
			 }
		}


		for (int i=0; i<100; i++)
		{
			index_1 = generator.nextInt( cards.size() - 1 );
			index_2 = generator.nextInt( cards.size() - 1 );

			temp = cards.get( index_2 );
			cards.set( index_2 , cards.get( index_1 ) );
			cards.set( index_1, temp );
		}
    }

    //카드 배포
	public Card drawFromDeck()
	{	   
		return cards.remove( 0 );
	}

	//총 갯수 출력 
	public int getTotalCards()
	{
		return cards.size();   //we could use this method when making a complete poker game to see if we needed a new deck
	}
}

