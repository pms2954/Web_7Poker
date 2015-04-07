package game.obj.card;

import java.util.ArrayList;
import java.util.Collections;

/*
 *	 목적 : 계산된 족보들의 비교 
 *  하는 일  : 각 플레이어들의 족보계산 내역 중 제일 높은 족보를 판별
 *  	       같은 족보일시 숫자 및 모양을 비교하여 제일 높은 플레이어를 판별
 */

public class Evaluater {

	private ArrayList<Evaluate> evaluater;
	
	// 룸에서 초기화
	public Evaluater(){
		evaluater= new ArrayList<Evaluate>(4);
	}
	
	//플레이어 추가
	synchronized public void putPlayer(Evaluate player){
		evaluater.add(player);
	}
	
	//족보 , 숫자, 모양 별로  정렬
	synchronized public void Sort(){
		Collections.sort(evaluater);
	}
	
	//판별된 승리자 추출
	synchronized public Evaluate getPlayer(){
		return evaluater.remove(0);
	}
	
	//초기화
	synchronized public void clear(){
		evaluater.clear();
	}
	
	//사이즈
	synchronized public int getSize(){
		return evaluater.size();
	}
	

	public String getFirshIndex(){
		return evaluater.get(0).getId();
	}
	
	//오픈 카드만 계산
	public void openCalc(int GameOrder){
		for(int index =0; index < evaluater.size() ; index++)
			evaluater.get(index).foundOpenCard(GameOrder);
	}

	//전부 계산
	public void AllCalc(int GameOrder){
		for(int index =0; index < evaluater.size() ; index++)
			evaluater.get(index).foundCard(GameOrder);
	}

	//스트링 화
	public void ToString(){
		System.out.println("EvalSize:"+ evaluater.size());
		for(int index =0; index < evaluater.size() ; index++){
			System.out.println("Eval"+index);
			System.out.println(evaluater.get(index).ToString());
		}
	
	
	}
	
}
