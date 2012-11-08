package GetBig;

import java.awt.*;

public class Dust
{
	public int x;
	public int y;
	public int vx,vy;
	public int ax,ay;
	
	private static final int radius=3;
	
	public Dust(int x,int y){
		this.x=x;
		this.y=y;
		vx = 0;
		vy = 0;
	}
	
	public Dust(int x, int y, int vx, int vy){
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
	}
	
	final static int MAX_VELOCITY = 20;
	
	public void moveDust(){
		x+=vx;
		y+=vy;
				
		// Make sure you stay in bounds
		if(x > GetBig.w.width){
			x = x % GetBig.w.width;
		}
		else if(x < 0){
			x = GetBig.w.width + x % GetBig.w.width;
		}
		if(y > GetBig.w.height){
			y = y % GetBig.w.height;
		}
		else if(y < 0){
			y = GetBig.w.height + y % GetBig.w.height;
		}		
		
		
		vx+=ax;
		vy+=ay;
		
		
		// Make sure it won't overspeed...
		if(vx+ax <= MAX_VELOCITY && vx+ax >= -MAX_VELOCITY){	vx+=ax;		}
		else{
			if(vx < 0)
				vx= -1*MAX_VELOCITY;
			else
				vx=MAX_VELOCITY;
		}
		if(vy+ay <= MAX_VELOCITY && vy+ay >= -MAX_VELOCITY){	vy+=ay;		}
		else{
			if(vy < 0)
				vy= -1*MAX_VELOCITY;
			else
				vy=MAX_VELOCITY;
		}
		
		// Apply some friction to slow it down a bit...
		if(vx > 0){
			vx-=1;
		}
		else if(vx < 0){
			vx+=1;
		}
		
		ax=ay=0;
	}
	
	public void drawDust(Graphics g){
    	int drawX = (x - GetBig.w.player.getX()) + (GetBig.display.screenWidth/2);
    	int drawY = (y - GetBig.w.player.getY()) + (GetBig.display.screenHeight/2);
    	
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
    	
    	
    	
		int zoom = GetBig.display.zoomLevel;
		g.setColor(Color.white);
		g.drawLine( (drawX-radius/2)/zoom,(drawY/zoom),(drawX+radius/2)/zoom,(drawY/zoom));
		g.drawLine( (drawX-radius/2)/zoom, (drawY-radius/2)/zoom,(drawX/zoom),(drawY+radius/2)/zoom);
		
		//g.drawOval(x-radius/2,y-radius/2,radius,radius);
	}
}