import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket; //responsible for listening for incoming clients and creating a socket object to communicate with them
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){ //responsible for keeping server running
        try{
            while(!serverSocket.isClosed()){ //runs while ServerSocket is open
             Socket socket =  serverSocket.accept(); //program will be halted until client connects, and socket object is returned to be able to communicate with client
             System.out.println("A new client has connected!");  //output when a client connects
             ClientHandler clientHandler = new ClientHandler(socket); //each object of this class will be responsible for communicating with a client
             Thread thread = new Thread(clientHandler); //a new thread will be spawned to deal with each new client so server can deal with multiple clients simultaneously
             thread.start();

            }
        } catch(IOException e){

        }


        }
    public void closeServerSocket() {  //avoid nested try-catch blocks with this method
         try{
             if(serverSocket != null){
                 serverSocket.close();
             }
         } catch(IOException e){
             e.printStackTrace();
         }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234); // Server listens for clients who connect to that port
        Server server = new Server(serverSocket);
        server.startServer();
        }
    }



