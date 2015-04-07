using UnityEngine;
using System.Collections;

public class Def  {

	public static int BBINGVALUE = 1000;

	public static  int CHECK 	= 0;
	
	public static  int BBING 	= 1;
	public static  int DADDANG	= 2;
	public static  int HALF 	= 3;
	public static  int CALL 	= 4;
	public static  int ALLIN 	= 5;
	public static  int DIE 		= 6;



	public static string ConvertString(int Doing){
		string str ="";
		switch(Doing){
		case -1:
			str ="";
			break;
		case 0:
			str = "체크";
			break;
		case 1:
			str = "삥";
			break;
		case 2:
			str = "따당";
			break;
		case 3:
			str = "하프";
			break;
		case 4:
			str = "콜";
			break;
		case 5:
			str = "올인";
			break;
		case 6:
			str = "다이";
			break;

		}
		return str; 
	}
}
