using UnityEngine;
using System.Collections;

public class MainList  {
					
		// Room for structure
		private static ArrayList Player_List = new ArrayList();
		private static ArrayList Room_List   = new ArrayList();
				
		public static void Initialize_MainList(){
			Player_List.Clear ();
			Room_List.Clear ();
		}
		
		public static ArrayList getPlayerList(){
			return Player_List;
		}

		public static ArrayList getRoomList(){
			return Room_List;	
		}


		public static int getPlayerCount(){
			return Player_List.Count;
		}

		public static int getRoomCount(){
			return Room_List.Count;
		}

}
