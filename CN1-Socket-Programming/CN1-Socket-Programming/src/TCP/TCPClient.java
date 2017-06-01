package TCP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPClient {
	private static String sentence;
	private static String modifiedSentence;

	public static void main(String[] args) throws Exception {
		System.err.println("Waiting for User Input...");
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
				System.in));
		System.err.println("Creating Client Socket!");
		Socket clientSocket = new Socket("127.0.0.1", 6789);
		DataOutputStream outToServer = new DataOutputStream(
				clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		sentence = inFromUser.readLine();
		System.err.println("Sending Bytes to server...");
		outToServer.writeBytes(sentence + "\n");
		System.err.println("Receiving From Server!");
		modifiedSentence = inFromServer.readLine();
		System.out.println("From Server:" + modifiedSentence + "\n");
		clientSocket.close();
		System.err.println("Connection Closed!");
	}
}
