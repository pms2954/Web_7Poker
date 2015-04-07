package net.netmarble.lobby.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.websocket.Session;

import net.netmarble.lobby.Player;

/*
 *  ���� �ڵ� : LANPARTY �ڵ� 
 *	 ���� : �÷��̾� ����
 *   �߰� �ϴ� ��  :  POKER PLAYER �ʿ���� �߰�  
 *  	     
 */

public class PlayerFactory {
	
	public static Player createPlayer(Class<? extends Player> clazz, String userId, Session session, Integer Cash ) {
		// TODO UserID�� null�� �� ���� ó�� ������Ѵ�!!!
		try {
			if(null == clazz) 
				return null;
			else {
				Constructor<? extends Player> constructor = clazz.getDeclaredConstructor(String.class, Session.class , Integer.class); 
				Player player = (Player) constructor.newInstance(userId, session, Cash);
				return player;
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
	
	public static Player createPlayer(Class<? extends Player> clazz, String userId) {
		try {
			if(null == clazz) 
				return null;
			else {
				Constructor<?> constructor = clazz.getDeclaredConstructor(String.class); 
				Player player = (Player) constructor.newInstance(userId);
				return player;
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
