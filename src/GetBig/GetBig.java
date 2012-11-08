package GetBig;

public class GetBig implements Runnable{
	static GetBig game; 
	final static int FPS = 20;

	//step allows you to step the game manually if true, automatically if false
	static boolean step = false;
	static boolean gameOn = false;
	
	// TODO: Set this to false if you want it to play the scene....
	static boolean introPlayed = false;
	static boolean gameOver = false;	
	
	static Scene intro;
	
	/*main method:
	 *-outputs some random message to command prompt
	 *-starts a new game
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Initiating Project Get Big");
		System.out.println(" (Not what she said...)");
		System.out.println("Warning: Gravitation is not responsible for people falling in love.");//hi there
		game = new GetBig();
	}
		
	static UserInterface ui;
	static Display display;
	static World w;
	static GameMenu menu;
	static MP3 mp3;
	
	public GetBig(){
		intro = new Scene();
		intro.addScene("Images/scene1.jpg");
		intro.addScene("Images/scene2.jpg");
		intro.addScene("Images/scene3.jpg");
		intro.addScene("Images/scene4.jpg");
		
		Particle.initImages();
		
		menu = new GameMenu();
		
	    menu.addOption(new GameOption(1));
	    menu.addOption(new GameOption(2));
	    menu.addOption(new GameOption(3));
	  
		w = new World();
		ui = new UserInterface();		// Setup the interface 
		display = new Display();		// Setup the display
		mp3 = new MP3();				// Setup the music
		new Thread(mp3).start();
		
		if(gameOn){
			display.removeKeyListener(menu);
			display.addKeyListener(ui);
			display.addMouseListener(ui);			
		}
		else{
			display.removeKeyListener(ui);
			display.removeMouseListener(ui);			
			display.addKeyListener(menu);
		}
		
		// Setup the input and redirect it to the UI object...
		
		new Thread(this).start();// The actual game
	}
		
	public static void step(){
		if(step){
			System.out.println("Stepping");
			w.update();
			display.draw();
			//sleep(1000);	
		}
	}
	int counter = 0;
	public void run(){
		int now, then, wait = 1000/FPS, actualWait;
		while(!step){	// We're not in step mode, but in run mode...
			then = (int) System.currentTimeMillis();
			
//			counter++;
			
//			if(counter > 100 && w.stgCnt == 0){
//				w.nextStage();
//			}
			
			w.update();
			display.draw();
			
			now = (int) System.currentTimeMillis();
			actualWait = wait - now + then;
			
			if(actualWait < 0){
				System.out.println("ActualWait: "+actualWait);
				actualWait = 0;
			}
			sleep(actualWait);
		}
	}
	
	public static void changeMenuState(boolean b){
		gameOn = b;
		if(gameOn){
			display.removeKeyListener(menu);
			display.addKeyListener(ui);
			display.addMouseListener(ui);			
		}
		else{
			display.removeKeyListener(ui);
			display.removeMouseListener(ui);			
			display.addKeyListener(menu);
		}
	}
	
	public static void sleep(int ms){
		try{
			Thread.sleep(ms);
		}
		catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	
}