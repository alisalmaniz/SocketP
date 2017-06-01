package http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class HttpClient {
	public static void main(String[] args) throws Exception {
		InetAddress addr = InetAddress.getByName("www.google.com");
		Socket socket = new Socket(addr, 80);

		// send an HTTP request to the web server
		DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		outToServer.writeBytes("GET / HTTP/1.1\r\n");
		outToServer.writeBytes("Host: www.google.com:80\r\n");
		outToServer.writeBytes("Connection: Close\r\n");
		outToServer.writeBytes("\r\n");
		// Receive an HTTP reply from the web server
		boolean loop = true;
		StringBuilder sb = new StringBuilder();
		while (loop) {
			if (inFromServer.ready()) {
				int i = 0;
				while (i != -1) {
					i = inFromServer.read();
					sb.append((char) i);
				}
				loop = false;
			}
		}
		System.out.println(sb.toString());
		socket.close();
	}

}
