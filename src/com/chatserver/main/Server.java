package com.chatserver.main;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {
	
	private int port;
	private Set<String> userNames = new HashSet<>();
	private Set<UserThread> userThreads = new HashSet<>();
	
	public Server(int port) {
		this.port = port;
	}

	public void execute() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			
			System.out.println("Listening on port " + port);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("New User Connected");
				
				UserThread newUser = new UserThread(socket, this);
				userThreads.add(newUser);
				newUser.start();
			}
		} catch (IOException ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Syntax: java Server <port number>");
			System.exit(0);
		}
		
		int port = Integer.parseInt(args[0]);
		
		Server server = new Server(port);
		server.execute();
	}
	
	void broadcast(String message, UserThread excludeUser) {
		for (UserThread aUser : userThreads) {
			if (aUser != excludeUser) {
				aUser.sendMessage(message);
			}
		}
	}
	
	void addUserName(String userName) {
		userNames.add(userName);
	}
	
	void removeUser(String userName, UserThread aUser) {
		boolean removed = userNames.remove(userName);
		if (removed) {
			userThreads.remove(aUser);
			System.out.println("The user " + " has left");
		}
	}
	
	Set<String> getUserNames() {
		return this.userNames;
	}
	
	boolean hasUsers() {
		return !this.userNames.isEmpty();
	}

}
