package GetBig;

public class Collision implements Comparable{
	Particle p1, p2;	// Particles it's happening to
	float time;			// Time it's happening at
	
	public Collision(Particle ip1, Particle ip2, float t){
		p1 = ip1;
		p2 = ip2;
		time = t;
	}
	public int compareTo(Object o){
		Collision c = (Collision) o;
		if(time < c.time){		return -1;		}
		if(time > c.time){		return 1;		}
		return 0;		// Both times are equal...
	}
	
	public String toString(){
		return "Collision between "+p1.id+" & "+p2.id+" at "+time;
	}
	
	public Particle getP1(){	return p1;	}
	public Particle getP2(){	return p2;	}
	public float getTime(){		return time;}
}