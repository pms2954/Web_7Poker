using UnityEngine;
using System.Collections;

public class CardDebugBehavoir : MonoBehaviour {

	CardController card; 
	public HandController MyHand;

	void Start () {
		card = GetComponent<CardController>();
	}
	

	public void ClickTest()
	{
		card.ShowFace();
		MyHand.Poker ();
	
	}

}
