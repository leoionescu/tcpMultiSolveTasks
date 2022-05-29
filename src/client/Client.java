package client;

import utils.Message;
import utils.Transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		int port = 5000;
		int portForTask = 6000;
		String hostname = "localhost";
		try (Socket socket = new Socket(hostname, port)) {
			try (Socket socketForTask = new Socket(hostname, port)) {
				System.out.println("Connected to server");
				Transport.send(new Message("active", new ArrayList<>()), socket);

				new Thread(() -> {
					while(true){
						try {
							Message message = Transport.receive(socketForTask);
							System.out.println("doing task " + message.getTitle());
							if(message.getTitle().equals("add")) {
								float sum = 0;
								for(String arg : message.getArgs()) {
									sum += Float.parseFloat(arg);
								}
								List <String> result = new ArrayList<>();
								result.add(String.valueOf(sum));
								Transport.send(new Message("result", result), socketForTask);
							} else if(message.getTitle().equals("sub")) {
								float dif = Float.parseFloat(message.getArgs().get(0));
								for(int i = 1; i < message.getArgs().size(); i++) {
									dif -= Float.parseFloat(message.getArgs().get(i));
								}
								List <String> result = new ArrayList<>();
								result.add(String.valueOf(dif));
								Transport.send(new Message("result", result), socketForTask);
							} else if(message.getTitle().equals("div")) {
								float x = Float.parseFloat(message.getArgs().get(0));
								for(int i = 1; i < message.getArgs().size(); i++) {
									x /= Float.parseFloat(message.getArgs().get(i));
								}
								List <String> result = new ArrayList<>();
								result.add(String.valueOf(x));
								Transport.send(new Message("result", result), socketForTask);
							} else if(message.getTitle().equals("mul")) {
								float mul = 1;
								for(String arg : message.getArgs()) {
									mul *= Float.parseFloat(arg);
								}
								List <String> result = new ArrayList<>();
								result.add(String.valueOf(mul));
								Transport.send(new Message("result", result), socketForTask);
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (NumberFormatException e) {
							try {
								List <String> result = new ArrayList<>();
								result.add("invalid arguments");
								Transport.send(new Message("result", result), socketForTask);
							} catch (IOException ioException) {
								ioException.printStackTrace();
							}
						}
					}
				}).start();

				try (Scanner scanner = new Scanner(System.in)) {
					while (true) {
						String command = scanner.nextLine();
						if (command.equals("new task")) {
							System.out.println("task name: ");
							String title = scanner.nextLine();
							List<String> argsForTask = new ArrayList<>();
							String arg = scanner.nextLine();
							while(!arg.equals("end")) {
								argsForTask.add(arg);
								arg = scanner.nextLine();
							}
							Message message = new Message(title, argsForTask);
							Transport.send(message, socket);
							Message response = Transport.receive(socket);
							System.out.println("result: " + response.getArgs().toString());
						} else if (command.equals("disconnect")) {
							Transport.send(new Message("disconnect", new ArrayList<>()), socket);
						}
//					writer.println(command);
//					writer.flush();
//					String response = reader.readLine();
//					System.out.println(response);
					}
				} catch (Exception e) {

				} finally {
					Transport.send(new Message("disconnect", new ArrayList<>()), socket);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				Transport.send(new Message("disconnect", new ArrayList<>()), socket);
				System.exit(0);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}

}
