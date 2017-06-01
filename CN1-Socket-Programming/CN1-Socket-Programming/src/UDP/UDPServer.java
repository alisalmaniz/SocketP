package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {

	public static void main(String[] args) throws Exception {
		DatagramSocket serverSocket = new DatagramSocket(9876);
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			System.err.println("Read Packets From Client...");
			serverSocket.receive(receivePacket);
			String sentence = new String(receivePacket.getData());
			InetAddress IPAdress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			sendData = sentence.toUpperCase().getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAdress, port);
			serverSocket.send(sendPacket);
			System.err.println("Write Packet to Client!");
		}
	}

}
