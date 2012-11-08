
/*************************************************************************
 *  Compilation:  javac -classpath .:jl1.0.jar MP3.java         (OS X)
 *                javac -classpath .;jl1.0.jar MP3.java         (Windows)
 *  Execution:    java -classpath .:jl1.0.jar MP3 filename.mp3  (OS X / Linux)
 *                java -classpath .;jl1.0.jar MP3 filename.mp3  (Windows)
 *  
 *  Plays an MP3 file using the JLayer MP3 library.
 *
 *  Reference:  http://www.javazoom.net/javalayer/sources.html
 *
 *
 *  To execute, get the file jl1.0.jar from the website above or from
 *
 *      http://www.cs.princeton.edu/introcs/24inout/jl1.0.jar
 *
 *  and put it in your working directory with this file MP3.java.
 *
 *************************************************************************/
package GetBig;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.*;
import java.awt.*;

import javazoom.jl.player.Player;

public class MP3 implements Runnable{
	private String filename;
	private Player player;
	private Random rand;
	static String currentlyPlaying = "";
	String[] files = {	"Slow_Small.mp3",
						"Happy_Maybe.mp3",
						"Faster.mp3",
						"Slow_end.mp3"};

	// constructor that takes the name of an MP3 file
	public MP3() {
		rand = new Random();
		currentlyPlaying = files[rand.nextInt(files.length)];
	}
	
	public void close() { if (player != null) player.close(); }
	
	// play the MP3 file to the sound card
	
	public void play() {
	    try {
	    	String filename = "Music\\" + files[GetBig.w.stgCnt];
	        FileInputStream fis     = new FileInputStream(filename);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        player = new Player(bis);
	    }
	    catch (Exception e) {
	        System.out.println("Problem playing file " + filename);
	        System.out.println(e);
	    }
	}
    
	public void draw(Graphics g){
		g.setFont(new Font("TimesRoman",Font.ITALIC,12));
		g.drawString(currentlyPlaying,0,625);
	}
	
	public void run() {
		while (true){
		    	try {Thread.sleep(100);	}
		    	catch (Exception e)	{System.out.println(e);}
		    	if(player == null || player.isComplete())
		    		play();
		    		try { player.play(); }
		    		catch (Exception e) { System.out.println(e); }
		}
	}
}