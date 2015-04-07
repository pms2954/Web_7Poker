using UnityEngine;
using System.Collections;

public class CardController : MonoBehaviour {
	

	public int cardIndex;

	public Texture[] faces;

	public Texture background;

	public Texture outline;
	

	public void ShowBackground(){
	
		renderer.enabled = true;
		renderer.material.mainTexture = background;
	}

	public void HideCard(){
		renderer.enabled = false;
	}

	public void ShowFace(){

		renderer.enabled = true;
		renderer.material.mainTexture = faces[cardIndex];

	}
	public void ShowOutline(){

		renderer.enabled = true;
		renderer.material.mainTexture = outline;

	
	}

	// Use this for initialization
	void Start () {
	
	}


	


}
