package GetBig;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class Particle {
	// x y describes the center x and y of the object
	// vx and vy = the velocities
	// dx and dy is the acceleration
	private int x, y, vx, vy, dx, dy, mass, radius;
	private int radiusCarryOver;//allows radius to be added without a carryover
	private boolean updated;
	private float time;
	
	int id = 0;
	static int nextId = 0;
	private final int MAX_VELOCITY = 20;
	boolean left, right, up, down;
	boolean mouseLeft, mouseRight, mouseUp, mouseDown;
	int speed = 3;
	
	int animState = 0;
	
	int adder = 3;
	int glowRadius = 0;
	final int minRadius=2;
	
	static Random rand = new Random();
	static int emitDistance = 20;
	static Color color = new Color(0,0,255);
	
	public Particle(int ix, int iy, int ivx, int ivy, int idx, int idy, int im, int ir){
		x = ix;
		y = iy;
		vx = ivx;
		vy = ivy;
		dx = idx;
		dy = idy;
		mass = im;
		radius = ir;
		glowRadius = radius;
		id = nextId;
		nextId+=1;
		updated = false;
		time = 0;
		generateImage();
	}
	
	static Image radialGlow;
	static Image particleMask;
	
	Image scaledParticle;
	
	public void generateImage(){
		int zoom = GetBig.display.zoomLevel;
	    scaledParticle = (Image) new BufferedImage(radius*2/zoom, radius*2/zoom, BufferedImage.TYPE_INT_ARGB);
	    Graphics g = scaledParticle.getGraphics();
		//draw particle
		g.setColor(color);
		g.fillOval(0, 0, (radius*2)/zoom, (radius*2)/zoom);
		// Draw the mask over the particle to give it that radial look...
		g.drawImage(particleMask, 0, 0, (radius*2)/zoom, (radius*2)/zoom, null);
//		System.out.println("Radius: "+radius);
	}
	
	public static void initImages(){
	    try {
	        radialGlow = ImageIO.read(new File("Images/radialGlow2.png"));
	        particleMask = ImageIO.read(new File("Images/particleMask3.png"));
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	public void drawParticle(Graphics2D g){
//		System.out.println("Adder: " +adder);
		if( (adder > 0 && glowRadius > radius*2)){
			adder*=-1;
			glowRadius = radius*2;
		}
		else if(adder < 0 && glowRadius < radius/2){
			adder*=-1;
			glowRadius = radius/2;
		}
		else{
			glowRadius+=adder;
		}
		
		Color semiColor = new Color(color.getRed(),color.getGreen(),color.getBlue(),10);
		Color semiBlack = new Color(0,0,0,10);

		int zoom = GetBig.display.zoomLevel;
    	int drawX = (x-GetBig.w.player.x) + (GetBig.display.screenWidth/2);
    	int drawY = (y-GetBig.w.player.y) + (GetBig.display.screenHeight/2);
    	
    	if( drawX < -1*GetBig.w.width+(GetBig.display.screenWidth*zoom) ){
    		drawX += GetBig.w.width;
    	}
    	else if( drawX > GetBig.w.width-(GetBig.display.screenWidth*zoom) ){
    		drawX -= GetBig.w.width;
    	}
    	if( drawY < -1*GetBig.w.height+ (GetBig.display.screenHeight*zoom) ){
    		drawY += GetBig.w.height;
    	}
    	else if( drawY > GetBig.w.height-(GetBig.display.screenHeight*zoom) ){
    		drawY -= GetBig.w.height;
    	}
    	
//    	System.out.println("Drawing object: "+this+" at ("+drawX+","+drawY+")");
		
		GradientPaint gradient = new GradientPaint( (drawX-glowRadius)/zoom, (drawY-glowRadius)/zoom, semiColor, (drawX+glowRadius)/zoom, (drawY+glowRadius)/zoom, semiBlack, false);
    	
    	//draw to glow
    	
    	g.setPaint(gradient);
    	g.fillOval( (drawX-glowRadius)/zoom , (drawY-glowRadius)/ zoom , (glowRadius*2) / zoom, (glowRadius*2)/zoom);
    	g.drawImage(radialGlow, (drawX-glowRadius)/zoom , (drawY-glowRadius) / zoom , (glowRadius*2)/zoom, (glowRadius*2)/zoom, GetBig.display);
    	
		g.drawImage(scaledParticle, (drawX-radius)/zoom, (drawY-radius)/zoom,null);
		
		g.setColor(Color.yellow);
		
		
		/*
		for(int k = 0; k < mass*2; k++){
			if(vx > 0){
				g.drawRect( (drawX-radius - rand.nextInt(radius + k)) / zoom, (drawY-radius + rand.nextInt(radius + k)) / zoom, 1,1 );
			}
			else if(vx < 0){
				g.drawRect( (drawX+radius + rand.nextInt(radius + k)) / zoom, (drawY-radius + rand.nextInt(radius + k)) / zoom, 1,1 );
			}
			
			if(vy < 0){
				g.drawRect((drawX-radius-radius/2 + rand.nextInt(radius*3 + k + trail)) / zoom, (drawY-radius + rand.nextInt(radius*2 + k*rand.nextInt(trail) + trail)) / zoom, 1,1 );
			}
			else if(vy > 0){
				g.drawRect((drawX-radius + rand.nextInt(radius*2 + k + trail)) / zoom, (drawY+radius - rand.nextInt(radius*2 + k*rand.nextInt(trail) + trail)) / zoom, 1,1 );	
			}
		}
		*/
	}
	
	
	static int trail = 10;
	
	public void modifyRadius(int numDust){
		radiusCarryOver+=(int)(7000*(Math.sqrt(radius*radius+numDust)-radius));
		if(radiusCarryOver>1000)
		{
			radius+=1;
			radiusCarryOver-=1000;
			generateImage();
			return;
		}
		if(radiusCarryOver<-1000&&radius>minRadius)
		{
			radius-=1;
			radiusCarryOver+=1000;
			generateImage();
		}
	}
	
	//move
	public void update(){
		// Basic navigation, using the keys...			
		emitDust();	// Emit dust based upon the key being pressed...
		//Alter the position based on how much time is left in it's movement interval
		x+=Math.round(vx * (1 - time));
		y+=Math.round(vy * (1 - time));
		
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
		
		if(vx+dx <= MAX_VELOCITY && vx+dx >= -MAX_VELOCITY){	vx+=dx;		}
		else{
			if(vx < 0)
				vx= -1*MAX_VELOCITY;
			else
				vx=MAX_VELOCITY;
		}
		if(vy+dy <= MAX_VELOCITY && vy+dy >= -MAX_VELOCITY){	vy+=dy;		}
		else{
			if(vy < 0)
				vy= -1*MAX_VELOCITY;
			else
				vy=MAX_VELOCITY;
		}
		dx = 0;
		dy = 0;
			
		time = 0;
		updated = false;
	}
	
	// Emits particles and gains velocity based on that...
	public void emitDust(){
		int numParticlesToEmit = mass/30;
		
		numParticlesToEmit = Math.max(1,numParticlesToEmit);
		
//		System.out.println("Need to emit: "+numParticlesToEmit+" for mass : "+mass);
		if(up || mouseUp){
			for(int k = 0; (k < numParticlesToEmit) && (mass > 2 && radius > 1) ; k++){
				GetBig.w.dustArray.add(new Dust(x+rand.nextInt(emitDistance),y+radius+rand.nextInt(emitDistance)+1,0,Dust.MAX_VELOCITY));
				mass-=2;
				modifyRadius(-1);
				glowRadius-=1;
				dy-=Dust.MAX_VELOCITY;
			}
		}
		if(down || mouseDown){
			for(int k = 0; (k < numParticlesToEmit) && (mass > 2 && radius > 1) ; k++){
				GetBig.w.dustArray.add(new Dust(x+rand.nextInt(emitDistance),y-radius-rand.nextInt(emitDistance)-1,0,-1*Dust.MAX_VELOCITY));
				mass-=2;
				modifyRadius(-1);
				glowRadius-=1;
				dy+=Dust.MAX_VELOCITY;
			}
		}
		if(right || mouseRight){
			for(int k = 0; (k < numParticlesToEmit) && (mass > 2 && radius > 1) ; k++){
				GetBig.w.dustArray.add(new Dust(x-radius-rand.nextInt(emitDistance)-1,y+rand.nextInt(emitDistance),-1*Dust.MAX_VELOCITY,0));
				mass-=2;
				modifyRadius(-1);
				glowRadius-=1;
				dx+=Dust.MAX_VELOCITY;
			}
		}
		if(left || mouseLeft){
			for(int k = 0; (k < numParticlesToEmit) && (mass > 2 && radius > 1) ; k++){
				GetBig.w.dustArray.add(new Dust(x+radius+rand.nextInt(emitDistance)+1,y+rand.nextInt(emitDistance),Dust.MAX_VELOCITY,0));
				mass-=2;
				modifyRadius(-1);
				glowRadius-=1;
				dx-=Dust.MAX_VELOCITY;
			}
		}
	}
	
	public int getDistance(Particle p2){
		int xdiff = Math.abs(p2.getX() - x);
		int ydiff = Math.abs(p2.getY() - y);
		return (int) Math.sqrt(Math.pow(xdiff,2) + Math.pow(ydiff,2));
	}
	
	public Particle cloneParticle(){
		return new Particle(x,y,vx,vy,dx,dy, mass, radius);
	}
	
	public void setX(int nx){		x = nx;		}
	public void setY(int ny){		y = ny;		}
	public void setDX(int ndx){		dx = ndx;	}
	public void setDY(int ndy){		dy = ndy;	}
	public void setVX(int nvx){ 	vx = nvx;	}
	public void setVY(int nvy){ 	vy = nvy;	}
	public void setM(int nm){		mass = nm;	}
	public void setRad(int nr){		radius = nr;}
	public void setMass(int nm){	mass = nm;
									if (mass <= 0){
										GetBig.w.particles.remove(this);
										if (this == GetBig.w.player){
											GetBig.gameOver = true;
										}
									}
								}		// Difft names at dift places...
	public void setRadius(int nr){	radius = nr; generateImage();}
	public void setIsUp(boolean u){	updated = u;}
	public void setTime(float t){	time = t;	}
	public int getX(){			return x;		}
	public int getY(){			return y;		}
	public int getDX(){			return dx;		}	
	public int getDY(){			return dy;		}
	public int getVX(){			return vx;		}
	public int getVY(){			return vy;		}
	public int getM(){			return mass;	}
	public int getMass(){		return mass;	}
	public int getRadius(){		return radius;	}
	public boolean getIsUp(){	return updated;	}
	public float getTime(){		return time;	}
	
	public String toString(){
		return "ID: "+id+" Location: ("+x+","+y+") Velocity: ("+vx+","+vy+")";
	}
}