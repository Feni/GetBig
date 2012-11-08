import java.net.*;
import java.io.*;
import java.util.*;

/*
Server based on:
http://www.cs.unc.edu/Courses/jbs/lessons/java/java_client_server1/server_basic.html
*/

public class SimpleServer extends Thread{
	ServerSocket listenSocket;
	Socket connection;
	
	ArrayList<ServerConnection> clients = new ArrayList<ServerConnection>();

	public void run(){
		try {
			listenSocket = new ServerSocket ( 9091 );
			System.out.print("Server Started");
			InetAddress here = InetAddress.getLocalHost ();
			String host = here.getHostName ();
			System.out.println("on "+host+", port 9091\n");

			while(true){
				connection = listenSocket.accept ();
				 System.out.println( "Connection request received\n" );
				 clients.add(new ServerConnection(connection, this));
			}
		}
		catch (Exception e)  {
			e.printStackTrace ();
		}
	}
  
	public void broadcast(String message, ServerConnection source){
	 	for(int k = 0; k < clients.size(); k++){
	 		ServerConnection client = clients.get(k);
	 		if(client != source){
	 			client.printToClient(message);
			}
		}
	}
	
	public void terminateConnection(ServerConnection conn){
		for(int k = 0; k < clients.size(); k++){
			if(clients.get(k) == conn){
				clients.remove(k);
				break;
			}
		}
	}

	public static void main ( String [ ] args )  {
		SimpleServer server = new SimpleServer();
		server.start();
		
		while(true){
			
		}
	}
}
