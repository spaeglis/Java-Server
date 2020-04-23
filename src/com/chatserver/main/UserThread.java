package com.chatserver.main;

import java.io.*;
import java.net.*;
import java.util.*;

public class UserThread extends Thread {
	private Socket socket;
	private Server server;
	private PrintWriter writer;

	public UserThread(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
	}
	
	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
			
			printUsers();
			
			String userName = reader.readLine();
			System.out.println(userName);
			server.addUserName(userName);
			
			String serverMessage = "New user connected: " + userName;
			server.broadcast(serverMessage, this);
			
			String clientMessage;
			
			do {
				clientMessage = reader.readLine();
				serverMessage = "[" + userName + "]: " + clientMessage;
				server.broadcast(serverMessage, this);
			} while (!clientMessage.equals("bye"));
			
			server.removeUser(userName, this);
			socket.close();
			
			serverMessage = userName + " has left";
			server.broadcast(serverMessage, this);
		} catch (IOException ex) {
			System.out.println("UserThread error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	void printUsers() {
		if (server.hasUsers()) {
			writer.println("Connected users: " + server.getUserNames());
		} else {
			writer.println("No other users");
		}
	}
	
	void sendMessage(String message) {
		writer.println(message);
	}

}
