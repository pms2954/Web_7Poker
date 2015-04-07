package game.obj.turn;

import game.obj.card.Evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.netmarble.lobby.Player;
/*
 *	 목적 : 각 플레이어 턴 정보 관리 및 동기화 
 *  하는 일  : 각 플레이어 턴 정보를 정하고, 대기 큐에 삽입한다.
 *  	       레이즈인 경우 큐에 턴을 더 삽압하거나, 다이인 경우 턴에서 제외한다.
 */

public class TurnManger {

	//대기 큐
	private ArrayList<Turn> Queue ;
	//정해진 순서
	private ArrayList<Turn> FixedTurn ;
	
	
	public TurnManger(){
		Queue = new ArrayList<Turn>();
		FixedTurn = new ArrayList<Turn>();
	}
		
	//queue 에서 하나씩 뺀다.
	public Turn removeQueue(){
		return Queue.remove(0);
	}
	
	//사용자가 잇는지 알려준다.
	public boolean playerExist(String PlayerId){
		for( Turn turn : Queue){
			if(turn.getId().equals(PlayerId)){
				return true;
			}
		}
		return false;
	}
	
	// 비어있는 지확인한다.
	public boolean isQueueEmpty(){
		return Queue.isEmpty();
	}

	//큐 초기화 
	public void initQueue(){
		for (int  index = 0 ; index < FixedTurn.size();  index++ ){
			Queue.add(FixedTurn.get(index));
		}
		
	}

	//오직 첫번째턴만 큐에 삽입한다.
	public void onlyoneQueue(){
		Queue.add(FixedTurn.get(0));

	}

	
	//큐에 턴을 추가한다. 
	public void putQueue(Player player){
		//큐에 첫번째 인자를 뺀다. 
		String Id = player.getId();
		
		int Pivot = FoundPivot(Id);
		
		int index = Pivot;
		// pivot 한뷔퀴 돌때까지 
		while(++index != Pivot){
			if(index ==FixedTurn.size()){
				index = 0;
			}
			
			if(index == Pivot)
				break;

			
			// QUEUE 에 있다면 넘어감
			if( Queue.contains((FixedTurn.get(index)))){
				//nothing 
			}
			else {
				Queue.add(FixedTurn.get(index));
			}
			// QUEUE 에 없다면 추가 
			
			
		}
	}
			

	//해당 영역 인덱스 찾기 
	public int FoundPivot(String Id){
		for (int  index = 0 ; index < FixedTurn.size();  index++ ){
			// 같은 객체라면
			if (Id.equals(FixedTurn.get(index).getId())){
				return index;
			}
		}
		return -1; // Error
	}
	
	//해당 영역 인덱스 찾기 
	public int FoundQueue(String Id){
		for (int  index = 0 ; index < Queue.size();  index++ ){
			// 같은 객체라면
			if (Id.equals(Queue.get(index).getId())){
				return index;
			}
		}
		return -1; // Error
	}
	
	//초기화 
	public void MakeFixedTurn( String playerId ){
		FixedTurn.add( new Turn(playerId) );
		
	}
	
	//초기화 
	public void resetFixedTurn(){
		if(!FixedTurn.isEmpty())
			FixedTurn.clear();
	}
	
	//정해진 순서에서 특정 턴 제거 
	public void DieFixedTurn(Player player){
		
		String Id = player.getId();
		int Pivot = FoundPivot(Id);
		FixedTurn.remove(Pivot);
		
	}
	
	//큐에 특정 턴 제거
	public void DieQueue(Player player){
		
		String Id = player.getId();
		int Pivot = FoundQueue(Id);
		Queue.remove(Pivot);
		
	}
	
	
	public void ToString(){
		System.out.println("Queue");

		for (int index = 0 ; index < Queue.size() ; index++){
			System.out.println("Queue"+index +":" +Queue.get(index).getId());
		}
		
		System.out.println("FixedTurn");

		for (int index = 0 ; index < FixedTurn.size(); index++){
			System.out.println("FixedTurn"+index +":" +FixedTurn.get(index).getId());
		}

		
	}
	

	

	
	
}
