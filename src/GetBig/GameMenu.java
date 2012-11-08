package GetBig;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.imageio.ImageIO;

public class GameMenu implements KeyListener{
	ArrayList<GameOption> options = new ArrayList<GameOption>();
	int pointer = 0;
	static Image background;
	boolean isDisplaying = false;
	GameOption temp;
	boolean up, down,left,right = false;
	boolean enter = false;
	
	public GameMenu(){setBackground("Images/menuBackground.jpg");}
	public void addOption(GameOption opt){options.add(opt);}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT){	
			temp = (GameOption) options.get(pointer);
			temp.onLeft();
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			temp = (GameOption) options.get(pointer);
			temp.onRight();			
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP){
			pointer--;
			if(pointer < 0){
				pointer = options.size()-1;
			}			
		}		
		else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			pointer++;
			if(pointer > options.size()-1){
				pointer=0;
			}			
		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER){
			temp = (GameOption) options.get(pointer);
			temp.onEnter();
		}		
	}
	public void keyReleased(KeyEvent e) {
	
	}

 	public void keyTyped(KeyEvent e) { 		}
 	 	
	
	public void setBackground(Image back){background = back;}
	
	public void setBackground(String filename){
		try{
			background = ImageIO.read(new File(filename));
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void drawOptions(Graphics g){		
		if(background != null){
			g.drawImage(background, 0,0, GetBig.display.screenWidth, GetBig.display.screenHeight, null);
		}
		g.setColor(new Color(0,0,0,100));
		g.fillRect(GetBig.display.screenWidth-400,GetBig.display.screenHeight-(50*(options.size()+3))+50, 300, (50*options.size()));
		
		g.setColor(new Color(255,0,0,100));
		g.fillRect(GetBig.display.screenWidth-400, GetBig.display.screenHeight-(50*(options.size()+3) )+(50*(pointer+1) ), 300,50);

		g.setColor(Color.white);
		int yCoord = 100;
		GameOption temp;
		for(int k = 0; k < options.size(); k++){
			temp = (GameOption)options.get(k);
			temp.drawOption(g,GetBig.display.screenWidth-400,GetBig.display.screenHeight-(50*(options.size()+3))+(yCoord+12) - 50);
			yCoord+=50;
		}
		g.setColor(Color.black);
	}
}

// Default option is for a boolean true or false/Yes or No option
class GameOption{
	int option;
	String optionName = "NO OPTION SET";
	public GameOption(int opt){
		option = opt;
		if(option == 1){
			optionName = "Play Game";
		}
		else if(option == 2){
			optionName = "Credits";
		}
		else if(option == 3){
			optionName = "Exit";
		}
	}
	public void onRight(){}
	public void onLeft(){}
	public void onEnter(){
		if(option == 0){	// Other Option
			System.out.println("Unknown Option Error");
		}
		else if(option == 1){	// New Game
			GetBig.changeMenuState(true);
		}
		else if(option == 2){	// View Credits
			GetBig.menu.setBackground("Images/credits.jpg");
		}
		else if(option == 3){	// Exit
			System.exit(0);			
		}
	}
	
	public void drawOption(Graphics g, int xCoord, int yCoord){
		g.drawString(optionName, xCoord,yCoord);
	}
}
