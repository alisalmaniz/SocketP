/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxyserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author AliSalmani
 */
public class ServerSideByHTTP {
    
    
    public void startAsClient(){
        
    }
    
    public String getList() throws Exception{
        
        String fileData = getData("");
        String[] rowLinks = fileData.split("<a href=");
        String filenames = "";
        String[] rowLinks2 = new String[0];
        String[] rowLinks3 = new String[0];
        System.out.println(rowLinks.length+"");
        for(int i=1; i<rowLinks.length; i++){
            rowLinks2 = rowLinks[i].split(">", 2);
            rowLinks3 = rowLinks2[1].split("<", 2);
            if(!rowLinks3[0].equals("Name") && !rowLinks3[0].equals("Last modified")
                && !rowLinks3[0].equals("Size") && !rowLinks3[0].equals("Description")
                && !rowLinks3[0].equals("Parent Directory")
                    ){
                
                filenames+=rowLinks3[0]+"\n";
            }
        }
        
        return filenames;
    }
    
    public String getData(String sub) throws Exception{
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", "Connecting to ceit.aut.ac.ir ...");
        InetAddress addr = InetAddress.getByName("ceit.aut.ac.ir");
        Socket socket = new Socket(addr , 80);
        
        // send an HTTP request to the web server
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outToServer.writeBytes("GET /~94131090/CN1_Project_Files/"+sub+" HTTP/1.1\r\n");
        outToServer.writeBytes("Host: ceit.aut.ac.ir:80\r\n");
        outToServer.writeBytes("Connection: Close\r\n");
        outToServer.writeBytes("\r\n");
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", "Connected to server.");
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", "requested: /~94131090/CN1_Project_Files/"+sub);
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
        socket.close();
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", "data recived:");
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", sb.toString());
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", "Connection closed.");
        return sb.toString();
    }
    
    public byte[] getDataBinary(String sub) throws Exception{
        
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", "Connecting to ceit.aut.ac.ir ...");
        InetAddress addr = InetAddress.getByName("ceit.aut.ac.ir");
        Socket socket = new Socket(addr , 80);
        
        // send an HTTP request to the web server
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outToServer.writeBytes("GET /~94131090/CN1_Project_Files/"+sub+" HTTP/1.1\r\n");
        outToServer.writeBytes("Host: ceit.aut.ac.ir:80\r\n");
        outToServer.writeBytes("Connection: Close\r\n");
        outToServer.writeBytes("\r\n");
        
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", "Connected to server.");
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", "requested: /~94131090/CN1_Project_Files/"+sub);
        // Receive an HTTP reply from the web server
        InputStream in = socket.getInputStream();
        int dataSize=0;
        byte[] b = new byte[1];
        byte[] bytest = new byte[2048];
        int lengtht;
        String get = "";
        
        for (int i =0; (lengtht = in.read(b)) != -1 ; i++) {
            bytest[i]=b[0];
          get = new String(bytest, 0, i+1); //very important
          if(get.contains("\r\n\r\n")){
              if(!get.contains("200 OK"))
                  return null;
              
              String[] t = get.split("Content-Length: ");
              t= t[1].split("\r\n");
              dataSize=Integer.parseInt(t[0]);
              break;
          }
        }

        byte[] bytes = new byte[2048];
        int length;
        ByteBuffer bb = ByteBuffer.allocate(dataSize);
        while ((length = in.read(bytes)) != -1) {
          bb.put(bytes, 0, length);
        }

        socket.close();
        
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", "data recived:");
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", bb.toString());
        ClientSideByFTP.log(ProxyServer.SERVERLOGS+".txt", "Connection closed.");
        return bb.array();
    }
}
