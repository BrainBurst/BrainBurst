

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Keyboard {
	public static boolean[] pressed = new boolean[256];
	public static boolean[] prev=new boolean[256];
	private Keyboard(){}
	
	public static void update(){
		for(int i=0;i<4;i++){
			if(i==0) prev[KeyEvent.VK_LEFT]=pressed[KeyEvent.VK_LEFT];
			if(i==1) prev[KeyEvent.VK_RIGHT]=pressed[KeyEvent.VK_RIGHT];
			if(i==2) prev[KeyEvent.VK_DOWN]=pressed[KeyEvent.VK_DOWN];
			if(i==3) prev[KeyEvent.VK_UP]=pressed[KeyEvent.VK_UP];
		}
	}
	public static void keyPressed(KeyEvent e){
		pressed[e.getKeyCode()]=true;
	}
	public static void keyReleased(KeyEvent e){
		pressed[e.getKeyCode()]=false;
	}
	public static boolean typed(int keyEvent){
		return (prev[keyEvent]&&(!pressed[keyEvent]));
		
	}

	public static boolean mouseClicked(MouseEvent arg0) {
		if(arg0.getX()==1){
			return true;
		}
		return false;
	}
}
