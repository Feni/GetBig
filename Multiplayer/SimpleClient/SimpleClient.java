import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

/*

Simple client based on:
http://www.cs.unc.edu/Courses/jbs/lessons/java/java_client_server1/client_basic.html

*/

public class SimpleClient extends Thread{
	Socket connection;
	DataInputStream inStream;
	DataOutputStream outStream;
	
	public SimpleClient(){
		try{
			InetAddress here = InetAddress.getByName("128.62.27.222");
			String host = here.getHostName ();
			connection = new Socket ( host, 9091);
			System.out.println( "Socket created:  connecting to server on "+host+"\n" );
			inStream = new DataInputStream(connection.getInputStream());
			outStream = new DataOutputStream(connection.getOutputStream());	
		}
		catch(Exception e){
			System.out.println("Error creating Simple Client");
			e.printStackTrace();
		}
		System.out.println("------Client created------");
	}	

  	public void run(){
		try{
			System.out.println("Listenign for messages from server...");
		        while(true) {
		        	processMessage(inStream.readUTF());
	        	}
		}  // end try for connection
		catch (Exception e ) {
			e.printStackTrace ();
		}
	}
  
	public void processMessage(String message){
		System.out.println("Server: "+message);
	}
	public void printToServer(String message){
		try{
			outStream.writeUTF(message );
			System.out.println("Wrote to server: "+message);
		}
		catch(Exception e)
		{
			System.out.println("Exception: "+e);
		}
	}

  	public static void main ( String [] args )  {
		Scanner scan = new Scanner(System.in);
	    	SimpleClient client = new SimpleClient();
//	    	new Thread(client).run();
	    	client.start();
		
		System.out.println("Starting scanner");
		while(true){
			client.printToServer(scan.nextLine());
		}
	}
	

}
