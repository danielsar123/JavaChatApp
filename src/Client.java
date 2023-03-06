import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private static boolean running = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            socket = new Socket("localhost", 1234);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Enter a username: ");
            String username = scanner.nextLine();
            bufferedWriter.write(username); //send username to server
            bufferedWriter.newLine(); //done sending data
            bufferedWriter.flush(); //flush buffer manually

            Thread readThread = new Thread(new Runnable() { //create a separate thread to listen for messages
                @Override
                public void run() {
                    String messageFromServer;
                    try {
                        while (running) {
                            messageFromServer = bufferedReader.readLine();
                            System.out.println(messageFromServer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            readThread.start(); //start the thread

            String messageToSend;
            while (running) {
                messageToSend = scanner.nextLine(); //get input from user
                bufferedWriter.write(messageToSend); //send user input to server
                bufferedWriter.newLine(); //done sending data
                bufferedWriter.flush(); //flush buffer manually
                if (messageToSend.equalsIgnoreCase("quit")) { //if the user enters "quit", terminate the program
                    running = false;
                }
            }

            readThread.join(); //wait for the thread to finish before terminating the program
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private static void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close(); //only necessary to close outer wrap (ex. not inputstream)
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
