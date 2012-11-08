package GetBig;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class UserInterface implements KeyListener, MouseListener{
	
	public UserInterface(){
		System.out.println("EventHandler initialized");
	}
	
	/*Moves the player  upon key pressing
	 */
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT){	GetBig.w.player.left = true;}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT){GetBig.w.player.right = true;	}
		else if(e.getKeyCode() == KeyEvent.VK_UP){	GetBig.w.player.up = true;	}		
		else if(e.getKeyCode() == KeyEvent.VK_DOWN){GetBig.w.player.down = true;	}
		else if(e.getKeyCode() == KeyEvent.VK_F9){
 			GetBig.step();		
 		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER){
			GetBig.gameOn = !GetBig.gameOn;
		}
	}
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT){	GetBig.w.player.left = false;}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT){GetBig.w.player.right = false;	}
		else if(e.getKeyCode() == KeyEvent.VK_UP){	GetBig.w.player.up = false;	}		
		else if(e.getKeyCode() == KeyEvent.VK_DOWN){GetBig.w.player.down = false;	}		
	}
 	public void keyTyped(KeyEvent e) { 		}
 	public void mouseClicked(MouseEvent e){ 	}
	public void mouseEntered(MouseEvent e) {	}
	public void mouseExited(MouseEvent e) {		}
 	public void mousePressed(MouseEvent e) { 
 		if(e.getButton() == MouseEvent.BUTTON1)
 		{
 			int zoom = GetBig.display.zoomLevel;
 			int actualX = ( e.getX() + GetBig.w.player.getX() ) - ( GetBig.display.screenWidth/2 );
 			int actualY = ( e.getY() + GetBig.w.player.getX() ) - ( GetBig.display.screenHeight/2 );
 			
 			System.out.println("Mouse pressed at actual location: "+actualX+" , "+actualY);
 			
 			if(actualX*zoom > GetBig.w.player.getX()){		// Emit particles in that direction, go the opposite dir...
 				// Press to the right, move to the left, emit particles to the right
 				GetBig.w.player.mouseLeft = true;
 			}
 			else if(actualX*zoom < GetBig.w.player.getX()){
 				GetBig.w.player.mouseRight = true;
 			}
 			if(actualY*zoom > GetBig.w.player.getY()){
 				// Press below, move up, emit particles down
 				GetBig.w.player.mouseUp = true;
 			}
 			else if(actualY*zoom < GetBig.w.player.getY()){
 				GetBig.w.player.mouseDown = true;
 			}
 		}
 	}
 	public void mouseReleased(MouseEvent e) { 
 		GetBig.w.player.mouseLeft = GetBig.w.player.mouseRight = GetBig.w.player.mouseUp = GetBig.w.player.mouseDown = false;
 	}
}
