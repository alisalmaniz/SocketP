package UDP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

	private static String sentence;
	private static String modifiedSentence;

	public static void main(String[] args) throws Exception {
		System.err.println("Waiting for User Input...");
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
				System.in));
		System.err.println("Creating Client Socket!");
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAdress = InetAddress.getByName("127.0.0.1");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		sentence = inFromUser.readLine();
		sendData = sentence.getBytes();
		System.err.println("creating packet...");
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, IPAdress, 9876);
		System.err.println("Sending Bytes to server...");
		clientSocket.send(sendPacket);
		System.err.println("Receiving From Server!");
		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);
		clientSocket.receive(receivePacket);
		modifiedSentence = new String(receivePacket.getData());
		System.out.println("From Server:" + modifiedSentence.trim() + "\n");
		clientSocket.close();
		System.err.println("Connection Closed!");
	}

}
