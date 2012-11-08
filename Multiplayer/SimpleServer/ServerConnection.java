import java.net.*;
import java.io.*;
import java.util.*;

public class ServerConnection extends Thread{
	Socket connection;
	DataOutputStream outStream;
	DataInputStream inStream;
	
	SimpleServer server;
	
	public ServerConnection(Socket con, SimpleServer serv){
		connection = con;
		server = serv;
		start();
	}
	
	public void run(){
		try{
			outStream = new DataOutputStream (connection.getOutputStream());
			outStream.writeUTF("Hello, Client, from Server!");
			inStream = new DataInputStream ( connection.getInputStream());
			while(true){
		        	processInput(inStream.readUTF ());
		        }
		}
		catch(Exception e){
			terminateConnection();
			System.out.println("Error in Server Connection :"+e);
			e.printStackTrace();
		}
	}
	
	public void processInput(String message){
		System.out.println("Message received: "+message);
		printToClient("Got : "+message);
		server.broadcast(message,this);
	}
	
	public void printToClient(String message){
		try{
			outStream.writeUTF(message );
			System.out.println( "  Message returned to client \n\n" );
		}
		catch(Exception e){
			terminateConnection();
			e.printStackTrace();
		}
	}
	
	public void terminateConnection(){
		try{
			connection.close();
		}
		catch(Exception e){
			System.out.println("Error closing connection.... hmmm.... this should never happen... ");
			e.printStackTrace();
		}
		server.terminateConnection(this);
		System.out.println("Connection Terminated");
		
	}
}
