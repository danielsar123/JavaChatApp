import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    public Client(Socket socket, String username){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //convert byte stream to character stream, use buffer for efficiency
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void sendMessage(){
        try{
            bufferedWriter.write(username); //send username over to client handler so it can identify user
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner sc = new Scanner(System.in);
            while(socket.isConnected()){
                String messageToSend = sc.nextLine(); //while the socket is still connected receive the user's input
                bufferedWriter.write(username + ": " + messageToSend); //sends to client handler to broadcast
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
             String msgFromGroupChat;

             while(socket.isConnected()){
                 try{
                     msgFromGroupChat = bufferedReader.readLine(); //each client will be waiting to receive message from broadcastMessage() in clienthandler, and output the message to console
                     System.out.println(msgFromGroupChat); //output message to console
                 }catch (IOException e){
                     closeEverything(socket, bufferedReader, bufferedWriter);
                 }
             }
            }
        }).start();
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter username for groupchat: ");
        String username = sc.nextLine();
        Socket socket = new Socket("localhost",1234); //port must match server's
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
