using UnityEngine;
using System.Collections;

public class CardChangeHover : MonoBehaviour {

	CardController card;

	public ChangeCardController chgCard;
	public UILabel  label;

	public bool openflag = false ;
 
	void Start () {
		card = GetComponent<CardController>();
	}

	//마우스 위로 갈 경우
	public void hover()
	{
		// not choice open
		if (chgCard.Count == 0 && openflag == false) {
			label.text = "오픈카드";
		} 
		else if (chgCard.Count == 1 && openflag == false) {
			label.text = "버릴카드";

		}

	}
	//마우스가 벗어날 경우 
	public void hoverOut(){	
		if(chgCard.Count == 0 && openflag == false)
			label.text =""; 
		//오픈 레이블 제외한 나머지를 없앤다. 
		else if (chgCard.Count == 1 && openflag == false) {
			if(label.text.Equals("버릴카드")){
				label.text ="";
			}
			
		}
	}

	// 클릭할 경우 
	public void Click(){
		// 첫번 째 선택 
		if (chgCard.Count == 0) {
			label.text = "오픈카드";
			openflag =true;
			chgCard.Count++;
			chgCard.Choice(card.cardIndex);

		}
		// 재선택시 취소 
		else if (chgCard.Count == 1 && openflag == true ) {
			label.text = "";
			openflag =false;
			chgCard.Count--;
			chgCard.Choice(card.cardIndex);

		}
		//두번째 선택시 완료 
		else if (chgCard.Count == 1) {
			label.text ="";
			openflag= false;
			chgCard.Count++;
			chgCard.Choice(card.cardIndex);
			chgCard.SendMessage ();

		}



	
	}


}
