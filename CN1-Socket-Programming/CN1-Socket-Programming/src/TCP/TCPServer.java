package TCP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6789);
		System.err.println("Welcomming Socket in Server was Created!");
		while (true) {
			System.err.println("Waiting for Connection On Port "
					+ welcomeSocket.getLocalPort());
			Socket connectionScket = welcomeSocket.accept();
			System.err.println("New Client From "
					+ connectionScket.getInetAddress() + ":"
					+ connectionScket.getLocalPort() + " Connected to Server!");
			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(connectionScket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(
					connectionScket.getOutputStream());
			String clientSentece = inFromClient.readLine();
			System.err.println("Read Bytes From Client...");
			outToClient.writeBytes(clientSentece.toUpperCase() + "\n");
			System.err.println("Write UpperCase Chars to Client!");
		}
	}
}
