package game.obj.turn;

import game.obj.card.Evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.netmarble.lobby.Player;
/*
 *	 ���� : �� �÷��̾� �� ���� ���� �� ����ȭ 
 *  �ϴ� ��  : �� �÷��̾� �� ������ ���ϰ�, ��� ť�� �����Ѵ�.
 *  	       �������� ��� ť�� ���� �� ����ϰų�, ������ ��� �Ͽ��� �����Ѵ�.
 */

public class TurnManger {

	//��� ť
	private ArrayList<Turn> Queue ;
	//������ ����
	private ArrayList<Turn> FixedTurn ;
	
	
	public TurnManger(){
		Queue = new ArrayList<Turn>();
		FixedTurn = new ArrayList<Turn>();
	}
		
	//queue ���� �ϳ��� ����.
	public Turn removeQueue(){
		return Queue.remove(0);
	}
	
	//����ڰ� �մ��� �˷��ش�.
	public boolean playerExist(String PlayerId){
		for( Turn turn : Queue){
			if(turn.getId().equals(PlayerId)){
				return true;
			}
		}
		return false;
	}
	
	// ����ִ� ��Ȯ���Ѵ�.
	public boolean isQueueEmpty(){
		return Queue.isEmpty();
	}

	//ť �ʱ�ȭ 
	public void initQueue(){
		for (int  index = 0 ; index < FixedTurn.size();  index++ ){
			Queue.add(FixedTurn.get(index));
		}
		
	}

	//���� ù��°�ϸ� ť�� �����Ѵ�.
	public void onlyoneQueue(){
		Queue.add(FixedTurn.get(0));

	}

	
	//ť�� ���� �߰��Ѵ�. 
	public void putQueue(Player player){
		//ť�� ù��° ���ڸ� ����. 
		String Id = player.getId();
		
		int Pivot = FoundPivot(Id);
		
		int index = Pivot;
		// pivot �Ѻ��� �������� 
		while(++index != Pivot){
			if(index ==FixedTurn.size()){
				index = 0;
			}
			
			if(index == Pivot)
				break;

			
			// QUEUE �� �ִٸ� �Ѿ
			if( Queue.contains((FixedTurn.get(index)))){
				//nothing 
			}
			else {
				Queue.add(FixedTurn.get(index));
			}
			// QUEUE �� ���ٸ� �߰� 
			
			
		}
	}
			

	//�ش� ���� �ε��� ã�� 
	public int FoundPivot(String Id){
		for (int  index = 0 ; index < FixedTurn.size();  index++ ){
			// ���� ��ü���
			if (Id.equals(FixedTurn.get(index).getId())){
				return index;
			}
		}
		return -1; // Error
	}
	
	//�ش� ���� �ε��� ã�� 
	public int FoundQueue(String Id){
		for (int  index = 0 ; index < Queue.size();  index++ ){
			// ���� ��ü���
			if (Id.equals(Queue.get(index).getId())){
				return index;
			}
		}
		return -1; // Error
	}
	
	//�ʱ�ȭ 
	public void MakeFixedTurn( String playerId ){
		FixedTurn.add( new Turn(playerId) );
		
	}
	
	//�ʱ�ȭ 
	public void resetFixedTurn(){
		if(!FixedTurn.isEmpty())
			FixedTurn.clear();
	}
	
	//������ �������� Ư�� �� ���� 
	public void DieFixedTurn(Player player){
		
		String Id = player.getId();
		int Pivot = FoundPivot(Id);
		FixedTurn.remove(Pivot);
		
	}
	
	//ť�� Ư�� �� ����
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
