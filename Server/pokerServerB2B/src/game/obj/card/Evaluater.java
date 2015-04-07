package game.obj.card;

import java.util.ArrayList;
import java.util.Collections;

/*
 *	 ���� : ���� �������� �� 
 *  �ϴ� ��  : �� �÷��̾���� ������� ���� �� ���� ���� ������ �Ǻ�
 *  	       ���� �����Ͻ� ���� �� ����� ���Ͽ� ���� ���� �÷��̾ �Ǻ�
 */

public class Evaluater {

	private ArrayList<Evaluate> evaluater;
	
	// �뿡�� �ʱ�ȭ
	public Evaluater(){
		evaluater= new ArrayList<Evaluate>(4);
	}
	
	//�÷��̾� �߰�
	synchronized public void putPlayer(Evaluate player){
		evaluater.add(player);
	}
	
	//���� , ����, ��� ����  ����
	synchronized public void Sort(){
		Collections.sort(evaluater);
	}
	
	//�Ǻ��� �¸��� ����
	synchronized public Evaluate getPlayer(){
		return evaluater.remove(0);
	}
	
	//�ʱ�ȭ
	synchronized public void clear(){
		evaluater.clear();
	}
	
	//������
	synchronized public int getSize(){
		return evaluater.size();
	}
	

	public String getFirshIndex(){
		return evaluater.get(0).getId();
	}
	
	//���� ī�常 ���
	public void openCalc(int GameOrder){
		for(int index =0; index < evaluater.size() ; index++)
			evaluater.get(index).foundOpenCard(GameOrder);
	}

	//���� ���
	public void AllCalc(int GameOrder){
		for(int index =0; index < evaluater.size() ; index++)
			evaluater.get(index).foundCard(GameOrder);
	}

	//��Ʈ�� ȭ
	public void ToString(){
		System.out.println("EvalSize:"+ evaluater.size());
		for(int index =0; index < evaluater.size() ; index++){
			System.out.println("Eval"+index);
			System.out.println(evaluater.get(index).ToString());
		}
	
	
	}
	
}
