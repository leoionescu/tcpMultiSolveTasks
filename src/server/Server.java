package server;

import utils.Message;
import utils.Transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements AutoCloseable {

	private ServerSocket serverSocket;
	private List<Client> clientList = Collections.synchronizedList(new ArrayList<>());
//	private List<Socket> clientList = Collections.synchronizedList(new ArrayList<>());

	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		ExecutorService executorService = Executors.newFixedThreadPool(10 * Runtime.getRuntime().availableProcessors());
		executorService.execute(() -> {
			while (!serverSocket.isClosed()) {
				try {
					Socket socket = serverSocket.accept();
					Socket socketForTask = serverSocket.accept();
					executorService.submit(new ClientHandler(socket, socketForTask, this));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void close() throws Exception {
		serverSocket.close();	
	}

	public void addClient(Client client) {
		clientList.add(client);
		System.out.println("client connected");
		System.out.println(clientList);
	}

	public void removeClient(Socket socket) {
		clientList.removeIf(obj -> obj.getSocket() == socket);
		System.out.println("client disconnected");
		System.out.println(clientList);
	}

	public Message sendTaskToNextClient(Message message) {
		Message response = null;
		for(Client client : clientList) {
			if(!client.isHasTask()) {
				try {
					client.setHasTask(true);
					Transport.send(message, client.getSocketForTask());
					response = Transport.receive(client.getSocketForTask());
					client.setHasTask(false);
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		if(response == null) System.out.println("no client available");
		return response;
	}

	public static void main(String[] args) {
		int port = 5000;
		try (Server server = new Server(port)) {
			System.out.println(String.format("Server running on port %d, type 'exit' to close", port));
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					String command = scanner.nextLine();
					if (command == null || "exit".equals(command)) {
						break;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}

}
