using UnityEngine;
using System.Collections;
/*
	클래스 목적 :  Room 관리 
	하는 일 :  -
 */
public class Room  {

		private string roomId;
		private string rommState;
		private bool Full;
		
		public Room(string id , string state , bool full){
			this.roomId = id;
			this.rommState = state;
			this.Full = full;
		}
		
		public string  printId(){
			
			return roomId;
		}
		
		public string printState(){

			return rommState;
		}

		public bool isJoin(){
			if (rommState.Equals("WAITING_ROOM") && Full == false)
				return true;
			
			return false;
		}

}
