package GetBig;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JFrame;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class Display extends JFrame{
	int screenWidth = 1024;
	int screenHeight = 768;
	
	static int zoomLevel = 1;
	
	static Image[] background = new Image[4];
	
	public Display(){
		setVisible(true);
		setBounds(0,0,screenWidth,screenHeight);
		createBufferStrategy(2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Get Big");
		
		initBackgrounds();
		generateClouds();
	}
	
	public void drawStage(){
		Graphics g = getBufferStrategy().getDrawGraphics();
		Font font1 = new Font("TimesRoman", Font.BOLD,  32);
		g.setFont(font1);
		g.drawString("Stage "+GetBig.w.stgCnt, 100, 100);
		g.dispose();
		getBufferStrategy().show();
		
	}
	
	public void initBackgrounds(){
		try{
			background[0] = ImageIO.read(new File("Images/S0Background.jpg"));
			background[1] = ImageIO.read(new File("Images/S1Background.jpg"));
			background[2] = ImageIO.read(new File("Images/S2Background.jpg"));
			background[3] = ImageIO.read(new File("Images/S3Background.jpg"));
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void setBackground(int i){
		try{
			background[GetBig.w.stgCnt] = ImageIO.read(new File("Images/background"+i+".jpg"));
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	public void draw(){
		Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0,0,screenWidth, screenHeight);
		
		if(GetBig.gameOn){
			if(!GetBig.introPlayed){
				GetBig.intro.drawScene(getBufferStrategy());
				GetBig.introPlayed = true;
			}
			else{
				if(background[GetBig.w.stgCnt] != null){
					int offsetX = (screenWidth / 2) - GetBig.w.player.getX();
					offsetX = Math.min(offsetX, 0);
					offsetX = Math.max(offsetX, screenWidth - GetBig.w.width);
					int offsetY = (screenHeight / 2) - GetBig.w.player.getY();
					offsetX = Math.min(offsetX, 0);
					offsetX = Math.max(offsetX, screenWidth - GetBig.w.width);			
					
					int x = offsetX * (screenWidth - background[GetBig.w.stgCnt].getWidth(null)) / (screenWidth - GetBig.w.width);
					int y = offsetY * (screenHeight - background[GetBig.w.stgCnt].getHeight(null)) / (screenHeight - GetBig.w.height);
					g.drawImage(background[GetBig.w.stgCnt], x, y, null);

					g.drawImage(background[GetBig.w.stgCnt],0,0,screenWidth,screenHeight,this);
				}
				else{
					Color semiBlack = new Color(0,0,0,150);
					// Draw a gradient based on the theme...
					GradientPaint gradient = new GradientPaint(screenWidth/2, 0, GetBig.w.theme, screenWidth/2, screenHeight, semiBlack, false);
			    	
			    	//draw to glow
			    	g.setPaint(gradient);
			    	g.fillRect(0,0,screenWidth, screenHeight);
				}
				
				g.setColor(new Color(150,0,0));
				for(int k = 0; k < GetBig.w.particles.size(); k++){
					GetBig.w.particles.get(k).drawParticle((Graphics2D)g);
				}
				
				for(int k = 0; k < GetBig.w.dustArray.size(); k++){
					GetBig.w.dustArray.get(k).drawDust(g);
				}
				
				g.setColor(Color.white);
				g.drawString("("+GetBig.w.player.getX()+","+GetBig.w.player.getY()+")",screenWidth-100,screenHeight-20);
				/*
				g.setColor(Color.green);
				g.drawRect(0-GetBig.w.player.getX()+screenWidth/2, 0-GetBig.w.player.getY()+screenHeight/2, GetBig.w.width, GetBig.w.height);		
				*/
				if(GetBig.w.stgCnt != 0 && GetBig.w.stgCnt != 3)
					drawClouds(g);
			}
			
		}
		else{
			GetBig.menu.drawOptions(g);
		}
		
		g.dispose();
		getBufferStrategy().show();
	}
	
	ArrayList<SpaceCloud> spaceClouds = new ArrayList<SpaceCloud>();
	
	Random rand = new Random();
	public void generateClouds(){
		for(int k = 0; k < 20; k++){
			Image cloud = getCloud(rand.nextInt(7));
			int x = rand.nextInt(GetBig.w.width);
			int y = rand.nextInt(GetBig.w.height);
			int width = rand.nextInt(1000)+10;
			System.out.println("Cloud at x "+x+" y "+y+" width "+ width);
			spaceClouds.add( new SpaceCloud(cloud,x,y,width,width)) ;
		}
	}

	public Image getCloud(int i){
		try{
			return ImageIO.read(new File("Images/cloud"+i+".png"));
		}
		catch(Exception e){
			System.out.println("Error finding cloud: "+e);
		}
		return null;
	}
	
	public void drawClouds(Graphics g){
		for(int k = 0; k < spaceClouds.size(); k++){
			spaceClouds.get(k).draw(g);
		}
	}
}

class SpaceCloud{
	Image image;
	int x, y, width, height;
	public SpaceCloud(Image i, int ix, int iy, int iwidth, int iheight){ 
		image = i;
		x = ix;
		y = iy;
		width = iwidth;
		height = iheight;
		
		int zoom = GetBig.display.zoomLevel;
	    Image i2 = (Image) new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics g = i2.getGraphics();
		g.drawImage(image, 0, 0, width/zoom, height/zoom, null);
		image = i2;
	}
	
	public void draw(Graphics g){
		int drawX = (x-GetBig.w.player.getX())+(GetBig.display.screenWidth/2);
		int drawY = (x-GetBig.w.player.getY())+(GetBig.display.screenHeight/2);
		
		g.drawImage(image,drawX, drawY ,GetBig.display);
    	if(drawX < -1*GetBig.w.width+GetBig.display.screenWidth){
    		drawX+=GetBig.w.width;
    	}
    	else if(drawX > GetBig.w.width-GetBig.display.screenWidth){
    		drawX-=GetBig.w.width;
    	}
    	if(drawY < -1*GetBig.w.height+GetBig.display.screenHeight){
    		drawY+=GetBig.w.height;
    	}
    	else if(drawY > GetBig.w.height-GetBig.display.screenHeight){
    		drawY-=GetBig.w.height;
    	}
    	g.drawImage(image,drawX, drawY ,GetBig.display);
	}
}