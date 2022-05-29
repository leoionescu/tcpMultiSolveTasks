package server;

import java.net.Socket;

public class Client {
    private Socket socket;
    private Socket SocketForTask;
    private boolean hasTask;

    public Client(Socket socket, Socket socketForTask, boolean hasTask) {
        this.socket = socket;
        SocketForTask = socketForTask;
        this.hasTask = hasTask;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocketForTask() {
        return SocketForTask;
    }

    public void setSocketForTask(Socket socketForTask) {
        SocketForTask = socketForTask;
    }

    public boolean isHasTask() {
        return hasTask;
    }

    public void setHasTask(boolean hasTask) {
        this.hasTask = hasTask;
    }

    @Override
    public String toString() {
        return "Client{" +
                "socket=" + socket +
                ", SocketForTask=" + SocketForTask +
                ", hasTask=" + hasTask +
                '}';
    }
}
