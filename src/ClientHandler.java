import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{ // instances will be executed by a separate thread
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); //helps communicate with multiple clients
    private Socket socket; //imported from server class, used to establish connection between client and server
    private BufferedReader bufferedReader; //used to read messages from client
    private BufferedWriter bufferedWriter; //used to send messages to client
    private String clientUsername;

    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //convert byte stream to character stream, use buffer for efficiency
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine(); //read first line inputted from client and set it as username
            clientHandlers.add(this); //adds current ClientHandler object to arraylist
            broadcastMessage("Server: " + clientUsername + "has entered chat!"); //send message to all connected clients

        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);

        }
    }

    @Override
    public void run() { //listen for messages on a separate thread
             String messageFromClient;
             while (socket.isConnected()){
                 try{
                     messageFromClient = bufferedReader.readLine(); //set client's input to String variable (the reason this is run on a separate thread is so program does not halt until client sends message).
                     broadcastMessage(messageFromClient);
                 }catch (IOException e){
                     closeEverything(socket,bufferedReader, bufferedWriter);
                     break; //break out of loop once socket is disconnected
                 }
             }
    }
    public void broadcastMessage(String messageToSend){
        for (ClientHandler clientHandler : clientHandlers){ //iterate through every client in arraylist
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){ // if current client's username is different from the one that is sending message
                  clientHandler.bufferedWriter.write(messageToSend); //send the desired String
                  clientHandler.bufferedWriter.newLine(); //done sending data
                  clientHandler.bufferedWriter.flush(); //flush buffer manually
                }
            } catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void removeClientHandler(){
        clientHandlers.remove(this); //remove current client handler object from arraylist
        broadcastMessage("Server: " + clientUsername + "has left the chat!");
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if (bufferedReader != null){
                bufferedReader.close(); //only necessary to close outer wrap (ex. not inputstream)
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }

    }
}
