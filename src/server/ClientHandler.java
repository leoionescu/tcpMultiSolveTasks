package server;

import utils.Message;
import utils.Transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

	private Socket socket;
	private Socket socketForTask;
	private BufferedReader reader;
	private PrintWriter writer;
	private Server server;
	
	public ClientHandler(Socket socket, Socket socketForTask, Server server) throws IOException {
		this.socket = socket;
		this.socketForTask = socketForTask;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer = new PrintWriter(socket.getOutputStream());
		this.server = server;
	}

	@Override
	public void run() {
		while (!socket.isClosed() && !socketForTask.isClosed()) {
			try {
				Message message = Transport.receive(socket);
//				System.out.println(message.toString());
				if(message.getTitle().equals("active")) {
					System.out.println("active");
					server.addClient(new Client(socket, socketForTask, false));
				} else if(message.getTitle().equals("disconnect")) {
					server.removeClient(socket);
				} else if(message.getTitle().equals("add") || message.getTitle().equals("sub") || message.getTitle().equals("div") || message.getTitle().equals("mul")) {
					System.out.println("task: " + message.toString());
					Message response = server.sendTaskToNextClient(message);
					Transport.send(response, socket);
				} else  {
					List<String> result = new ArrayList<>();
					result.add("task doesn't exist");
					Transport.send(new Message("result", result), socket);
				}
			} catch (Exception e) {
				writer.println(e.getMessage());
				writer.flush();
			}	
		}
		server.removeClient(socket);
		System.out.println("removed client");
	}


	
}
