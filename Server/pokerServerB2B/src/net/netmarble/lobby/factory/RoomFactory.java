package net.netmarble.lobby.factory;

import java.lang.reflect.InvocationTargetException;

import net.netmarble.lobby.Room;

/*
 *  기존 코드 : LANPARTY 코드 
 *	 목적 : 룸  생성
 *   추가 하는 일  :  추가사항 없음 
 *  	     
 */
public class RoomFactory {
	public static Room createRoom(Class<? extends Room> clazz, String roomId) {
		try {
			if(null == clazz) 
				return null;
			else {
				Room room = (Room) clazz.getDeclaredConstructor(String.class).newInstance(roomId);
				return room;
			}
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
