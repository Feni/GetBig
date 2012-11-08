package GetBig;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.*;

import javax.imageio.ImageIO;

public class Scene {
	ArrayList<Image> scenes = new ArrayList<Image>();
	
	public void drawScene(BufferStrategy strategy){
		for(int k = 0; k < scenes.size(); k++){
			Graphics g = strategy.getDrawGraphics();
			g.drawImage(scenes.get(k), 0,0,null);
			g.dispose();
			strategy.show();
			sleep(4000);
		}
	}
	
	public void addScene(Image i){
		scenes.add(i);
	}
	public void addScene(String str){
		try{
			scenes.add(ImageIO.read(new File(str)));
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void sleep(int i){
		try{
			Thread.sleep(i);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}

