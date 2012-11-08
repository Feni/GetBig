package GetBig;

import java.util.*;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

public class World {
	ArrayList<Particle> particles = new ArrayList<Particle>();
	ArrayList<Collision> collisions = new ArrayList<Collision>();
	Random rand=new Random();
	Color theme = new Color(255,0,0);
	Particle player;
	int width = 2500;
	int height = 2500;
	final int G = 2500;//formerly 7500
	//dust related
	final int max_dust=1000;
	ArrayList<Dust> dustArray=new ArrayList<Dust>();
	final double dustInfRadius=4;
	final double dustSpeedConst=2000;
	int stgCnt;
	
	public World(){
//		System.out.println("(byte)200 = "+(byte)200);
		stgCnt = 0;
		createTESTParticles();
		createRandomWorld();
		createRandomDust();
		
	}
	
	//allows us to add only a few test particles
	public void createTESTParticles(){
		player = new Particle(300,100,0,0,0,0,40,20);
		particles.add(player);
//		particles.add(new Particle( 300, 400, 0, 0, 0, 0, 100, 100));
//		particles.add(new Particle( 300, 400, 0, 0, 0, 0, 50, 15));
	}
	
	//create ALL the other particles
	public void createRandomWorld(){
		for(int k = 0; k < 2; k++){
			//particles.add(new Particle( rand.nextInt(1024), rand.nextInt(768), rand.nextInt(10)+1, rand.nextInt(10)+1, 0, 0, rand.nextInt(10), rand.nextInt(10)));
			int massRadius = rand.nextInt(100)+1;
			particles.add(new Particle( rand.nextInt(height)+1, rand.nextInt(width)+1, 0, 0, 0, 0, massRadius, massRadius));
		}
	}
	
	public void createRandomDust(){
		for(int k=0;k<max_dust;k++){	//TODO: change to Dust objects
			dustArray.add( new Dust(rand.nextInt(width), rand.nextInt(height)) );
		}
	}
	
	
	public void update(){
		if(GetBig.gameOn && GetBig.introPlayed){
			applyGravity();
			pullDust();
			checkCollisions();
			handleCollisions();
			updateParticles();
			if (particles.size() == 1){
				nextStage();
			}
		}
	}
	
	public void nextStage(){
		stgCnt++;
		
		//Generating enemies
		int r = rand.nextInt(4 * stgCnt);
		Particle p1, p2;
		
		if(stgCnt == 1){
			Particle.color = new Color(0,255,0);
		}
		else if(stgCnt == 2){
			Particle.color = new Color(255,0,0);
		}
		
		for (int i = 0; i < r; i++){
			int rad = rand.nextInt(player.getRadius()) + player.getRadius() / 2;
			p1 = new Particle(rand.nextInt(width) + 1, rand.nextInt(height) + 1, rand.nextInt(5), rand.nextInt(5), 0, 0, rad, rad);
			int size = particles.size();
			for (int k = 0; k < size; k++){
				p2 = particles.get(k);
				if (p1.getDistance(p2) <= rad + p2.getRadius()){
					p1.setX(rand.nextInt(width) + 1);
					p1.setY(rand.nextInt(height) + 1);
					k = -1;
				}
				if (k + 1 == size){
					particles.add(p1);
					break;
				}
			}
		}
	}
	
	public void updateParticles(){     
		for(int k = 0; k < particles.size(); k++){
			particles.get(k).update();
		}
	}
	
	/*Pulls Dust towards Particles*/
	public void pullDust(){
		double mag;//magnitude of the velocity of the dust being influenced
		double dist;//distance between each dust and particle
		
		for(Particle part:particles){
			for(int k = 0; k < dustArray.size(); k++){
				Dust dust = dustArray.get(k);
				//1 Determine the radius between the dust the particle
				int x = part.getX()-dust.x;
				int y = part.getY()-dust.y;
				dist=Math.sqrt(x * x + y * y);
				//2 if (dist > dustInfRadius particle radius) then goto dust
				
				
				// Dust is in particle's mouth already. Time to eat, yummm!
				if(dist <= part.getRadius()){
					int mass = part.getMass() + 2;
					part.setMass(mass);
					part.modifyRadius(1);
					
					// Add the velocity of the dust to the particle
					part.setDX( ((part.getDX()*mass) + dust.vx )/mass );
					part.setDY( ((part.getDY()*mass) + dust.vy )/mass);
					
					/*
					if(part.getDX() > 0){		part.setDX(part.getDX()-1);				}
					else if(part.getDX() < 0){			part.setDX(part.getDX()+1);				}
					
					if(part.getDY() > 0){		part.setDY(part.getDY()-1);				}
					else if(part.getDY() < 0){			part.setDY(part.getDY()+1);				}
*/
					
					dustArray.remove(k);
					k--;
				}
				else{
					if(dist>dustInfRadius*part.getRadius())	// Too far away...
						continue;
						
					//System.out.printf("Distance: %f\n",dist);
					//3 magnitude of velocity=dustSpeedConst/dist;
					mag=dustSpeedConst/dist;
					
					//4 add to the dust velocity
					dust.ax+=mag*x/dist;
					dust.ay+=mag*y/dist;
//					System.out.printf("vx: %d\nvy: %d\n",dust.vx,dust.vy);
				}
			}
		}
		for(Dust dust:dustArray){
			//  Check if it's gona get eaten...
			dust.moveDust();//tells itself to add vx and vy to x and y
		}
			
	}
	
	/*Alter acceleration between particles*/
	public void applyGravity(){
		for(int k = 0; k < particles.size(); k++){
			for(int i = k+1; i < particles.size(); i++){
				int dis = particles.get(k).getDistance(particles.get(i));
				
				Particle p1 = particles.get(k);
				Particle p2 = particles.get(i);
				
				// Check if the other particle is within range...
				if(dis < 10*(p1.getRadius() + p2.getRadius())){
					// We can go ahead and compute gravity as long as they're not colliding
					// already...
					//This is where the gravity is actually calculated
					//Finds the vector force by each particle and adds them to the dx and dy if noticable
					float force =((float) p1.getM() * p2.getM()) / (dis * dis) * G;
					int fx = Math.round(force / dis) * (p1.getX() - p2.getX());
					int fy = Math.round(force / dis) * (p1.getY() - p2.getY());
						
					//System.out.println("Force: "+force+" FX "+fx+" FY "+fy);
						
					//Add the dx and dy to the first object (object k)
					p1.setDX(p1.getDX() - fx / p1.getM() );
					p1.setDY(p1.getDY() - fy / p1.getM() );
					//Add the dx and dy to the second object (object i) BUT i's dx and dy are opposite of Object k's so subtracting
					p2.setDX(p2.getDX() + fx / p2.getM() );
					p2.setDY(p2.getDY() + fy / p2.getM() );	
				}
			}
		}
	}
	
	public void checkCollisions(){
		for(int k=0; k < particles.size(); k++){/*
			Particle p1 = particles.get(k);
			for(int i = k+1; i < particles.size(); i++){
				Particle p2 = particles.get(i);
				float t = computeCollision(p1, p2, 0);
				if (t >= 0){
					collisions.add(new Collision(p1, p2, t));
				}
			}*/
			evalCollisions(particles.get(k), null, k + 1, 0);
		}
	}
	
	public void evalCollisions(Particle p1, Particle p2, int start, float time){
		Particle p;
		for(int i = start; i < particles.size(); i++){
			p = particles.get(i);
			if (p != p1 && p != p2){
				float t = computeCollision(p, p1, time);
				if (t >= 0){
					addCollision(new Collision(p, p1, t));
				}
				if (p2 != null){
					t = computeCollision(p, p2, time);
					if (t >= 0){
						addCollision(new Collision(p, p2, t));
					}
				}
			}
		}
	}
	
	private float computeCollision(Particle p1, Particle p2, float t){
		//System.out.println("Computing collision between:");
		//System.out.println(p1);
		//System.out.println(p2);
		int dx = p1.getX() - p2.getX();				//This is x1 - x2
		int dy = p1.getY() - p2.getY();				//This is y1 - y2
		
		// p1.getX() > (width - 4*p1.radius())
		// p1 = p1.getX() - width
		
		int dvx = p1.getVX() - p2.getVX();			//This is vx1 - vx2
		int dvy = p1.getVY() - p2.getVY();			//This is vy1 - vy2
		int rsum = p1.getRadius() + p2.getRadius();	//This is r1 + r2

		int a = dvx * dvx + dvy * dvy;           //This is dvx^2 + dvy^2
		int b = 2 * (dx * dvx + dy * dvy);       //This is 2(dx dvx + dy dvy)
		int c = dx * dx + dy * dy - rsum * rsum; //This is dx^2 + dy^2 - (rsum)^2

		//System.out.println("a: "+a+" b: "+b +" c: "+c);
		//Obviously this is the equation transformed into the quadratic formula; super simple, right? 8D
		int underRad = b * b - 4 * a * c;
		if (underRad < 0)
			return -1;
		float radical = (float) ( Math.sqrt(b * b - 4 * a * c) );
		
		//System.out.println("Radical: "+radical);
		//The basic formula for calculating if two objects will collide in a 2d plane is:
		float t1 = (float) ((-b + radical) / (2 * a));
		float t2 = (float) ((-b - radical) / (2 * a));

		//System.out.println("The two objects will collide in: "+t1);
		// Feni: One possible problems is when we go from one end of the screen
		// to the other. The x and y's completley mess up!
		// It could "teleport" and end up landing on an objects
		// That's why we should just do an infinite plane (more or less...)
		
		// Seems to be working, except for the radius part...
		if (t1 >= t && t1 <= 1)		//The collision must happen between the "current phase" of the step and the end of the step
			if (t2 >= t && t2 <= 1){	return Math.min(t1, t2);}
			else{						return t1;				}
		else
			if (t2 >= 0 && t2 <= 1){	return t2;				}
		return -1;
	}
	
	private void addCollision(Collision c){
		int i = 0;
		while (i < collisions.size() && collisions.get(i).getTime() < c.getTime())
			i++;
		//System.out.println("Array Size is " + collisions.size() + " Index called is " + i);
		collisions.add(i, c);
	}
	
	public void handleCollisions(){
		//Collections.sort(collisions);
		//System.out.println("Collisions: "+collisions.getTime());
		float tempTime;
		float currentTime = 0;
		Particle p1, p2;
		int[] res;
		Collision c;
		while(collisions.size() > 0){
			// Get the earliest collisions and get rid of it from the array
			c = collisions.remove(0);
			// do stuff with the collision... First, check to make sure that the collision is appropriate
			if ((tempTime = computeCollision((p1 = c.getP1()),(p2 = c.getP2()), currentTime)) >= 0){
				currentTime = tempTime;
				
				//Second, find place the objects at the points that have the intersection
				p1.setX(p1.getX() + (int) (p1.getVX() * (currentTime - p1.getTime())));
				p1.setY(p1.getY() + (int) (p1.getVY() * (currentTime - p1.getTime())));
				p2.setX(p2.getX() + (int) (p2.getVX() * (currentTime - p2.getTime())));
				p2.setY(p2.getY() + (int) (p2.getVY() * (currentTime - p2.getTime())));
				p1.setTime(currentTime);
				p2.setTime(currentTime);
				
				//Third, explode stuff to make dust
				int m1o = p1.getMass();
				int m2o = p2.getMass();
				int vx1 = p1.getVX();
				int vy1 = p1.getVY();
				int vx2 = p2.getVX();
				int vy2 = p2.getVY();
				int x1 = p1.getX();
				int y1 = p1.getY();
				int x2 = p2.getX();
				int y2 = p2.getY();
				
				explosion(p1,p2,x1,y1,x2,y2);
				
				//Fourth, alter the velocities appropriately
				int m1f = p1.getMass();
				int m2f = p2.getMass();
				if (m1f > 0 && m2f > 0){
					res = calcNewVelocities(p2, x1, x2, m1o, m1f, m2o, m2f, vx1, vy1);
					p1.setVX(res[0]);
					p2.setVX(res[1]);
					res = calcNewVelocities(p2, y1, y2, m1o, m1f, m2o, m2f, vx2, vx1);
					p1.setVY(res[0]);
					p2.setVY(res[1]);
					//Re-evaluate the collisions that might happen with these two particles
					evalCollisions(p1, p2, 0, currentTime);
				}
				else if (m1f > 0){
					p1.setVX((int) Math.sqrt((m1o * vx1 * vx1 + m2o * vx2 * vx2 )/ m1f));
					p1.setVY((int) Math.sqrt((m1o * vy1 * vy1 + m2o * vy2 * vy2 )/ m1f));
				}
				else if (m2f > 0){
					p1.setVX((int) Math.sqrt((m1o * vx1 * vx1 + m2o * vx2 * vx2 )/ m2f));
					p1.setVY((int) Math.sqrt((m1o * vy1 * vy1 + m2o * vy2 * vy2 )/ m2f));
				}
					
				/*
				int radsum = p1.getRadius() + p2.getRadius();
				System.out.println("Distance is: " + p1.getDistance(p2) + " and rad sum is : " +radsum );
				updateParticles();
				p1.setVX(0);
				p1.setVY(0);
				p2.setVX(0);
				p2.setVY(0);
				p1.setDX(0);
				p1.setDY(0);
				p2.setDX(0);
				p2.setDY(0);
				GetBig.display.draw();
				System.out.println("Distance is: " + p1.getDistance(p2) + " and rad sum is : " +radsum );
				while(true);*/
			}
			
			//System.out.println("Collision! "+c);
			
			// Remove any other collisions dealing with the same objects...
			//removeParticleCollisions(c.p1);
			//removeParticleCollisions(c.p2);
		}
	}
	
	private void explosion(Particle p1, Particle p2,int x1,int y1,int x2,int y2){
		int mass1 = p1.getMass();
		int mass2 = p2.getMass();
		int mv1 = mass1 * p1.getVX() + mass1 * p1.getVY();
		int mv2 = mass2 * p2.getVX() + mass2 * p2.getVY();
		int massDiff = 0;
		if (mv1 > mass2 / 2){
			massDiff += p2.getMass();
			p2.setMass((int) Math.sqrt(mass2 * mass2- mv1/4));
			massDiff -= p2.getMass();
		}
		if (mv2 > mass1 / 2){
			massDiff += p1.getMass();
			p1.setMass((int) Math.sqrt(mass1 * mass1- mv1/4));
			massDiff -= p1.getMass();
		}
		int dust_num = (int) Math.sqrt(massDiff * 50);
		for(int k=0;k<dust_num ;k++){	//TODO: change to Dust objects
			dustArray.add( new Dust(rand.nextInt(Math.abs(x1 - x2 + 1)) + Math.min(x1, x2), rand.nextInt(Math.abs(y1 - y2 +1)) + Math.min(y1, y2)) );
		}
	}
	
	private int[] calcNewVelocities(Particle p, int p1, int p2, int m1o, int m2o, int m1f, int m2f, int v1o, int v2o){
		int mv1o = m1o * v1o;
		int mv2o = m2o * v2o;
		int mvsum = mv1o + mv2o;
		
		int a = m1f + m1f * m1f / m2f;
		int b = 2 * mvsum * m1f / m2f;
		int c = mv1o + mv2o - mvsum * mvsum / m2f;
		int underRad = b * b - 2 * a * c;
		if (underRad < 0){
			int [] temp = {-v1o, -v2o};
			return temp;
		}
		float radical = (float) Math.sqrt(underRad);
		int pv1 = Math.round((-b + radical) / (2 * a));
		int pv2 = Math.round((-b - radical) / (2 * a));
		
		//System.out.println("The solutions were " + pv1 + " and " + pv2);
		int[] vf = new int[2];
		if (p1 < p2)
			if (pv1 <= pv2)
				vf[0] = pv1;
			else
				vf[0] = pv2;
		else
			if (pv1 >= pv2)
				vf[0] = pv1;
			else
				vf[0] = pv2;
		vf[1] = (mv1o + mv2o - m1f * vf[0]) / m2f;
		return vf;
	}
	
	public void removeParticleCollisions(Particle p){ 
		for(int k = 0; k < collisions.size(); k++){
			// Check if the collision contains one of the particles...
			if(collisions.get(k).p1 == p || collisions.get(k).p2 == p){
				collisions.remove(k);	// No longer valid, so get rid of it...
				k--;	// Decrement so that we don't skip over stuff...
			}
		}
	}
	
}