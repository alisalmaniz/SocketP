/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author AliSalmani
 */
public class ClientFTP {
    
    Socket clientSocketControl = null;
    DataInputStream inFromServerControl;
    DataOutputStream outToServerControl;
    
    Socket clientSocketData = null;
    DataInputStream inFromServerData;
    DataOutputStream outToServerData;
    
    public void connect() throws Exception{
        
        if(clientSocketControl !=null){
            disconnect();
        }
        
        clientSocketControl=new Socket(Client.LOCAL_HOST,Client.CONTROL_SOCKET);
//        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        inFromServerControl = new DataInputStream(clientSocketControl.getInputStream());
        outToServerControl = new DataOutputStream(clientSocketControl.getOutputStream());
        
        clientSocketData=new Socket(Client.LOCAL_HOST,Client.CONTROL_SOCKET);
//        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        inFromServerData = new DataInputStream(clientSocketData.getInputStream());
        outToServerData = new DataOutputStream(clientSocketData.getOutputStream());
        
        String username = JOptionPane.showInputDialog("Username:");
        outToServerControl.writeUTF("USER "+username);
        String result = inFromServerControl.readUTF();
        if(!result.startsWith("220")){
            System.out.println("Username incorrect");
            disconnect();
            return;
        }
        String password = JOptionPane.showInputDialog("Password:");
        outToServerControl.writeUTF("PASS "+password);
        result = inFromServerControl.readUTF();
        if(!result.startsWith("220")){
            
            System.out.println("Password incorrect");
            disconnect();
            return;
        }
        
        while(true){
            String command = JOptionPane.showInputDialog(
                    "Enter number:\n\n"
                    + "1.RMD\n"
                    + "2.DELE\n"
                    + "3.RETR\n"
                    + "4.LIST\n"
                    + "5.QUIT");
            if(command==null){
                disconnect();
                break;
            }
            if(command.equals("1")){
                    outToServerControl.writeUTF("RMD");
                    String msgFromServer=inFromServerControl.readUTF();
                    System.out.println(msgFromServer);
                    //SendFile();
            }
            else if(command.equals("2")){
                String fileRequested = JOptionPane.showInputDialog("File name:");
                outToServerControl.writeUTF("DELE "+fileRequested);
//                outToServerControl.writeUTF("DELE");
                String msgFromServer=inFromServerControl.readUTF();
                System.out.println(msgFromServer);
                //ReceiveFile();
            }
            else if(command.equals("3")){
                String fileRequested = JOptionPane.showInputDialog("File name:");
                outToServerControl.writeUTF("RETR "+fileRequested);
                int dataSize = inFromServerData.readInt();
                if(dataSize<0 ){
                    outToServerData.writeUTF("Error");
                    System.err.println("Error");
                    continue;
                }
//                System.out.println(dataSize+"");
                outToServerData.writeUTF("OK");
                byte[] data = new byte[dataSize];
                Thread.sleep(20);// very important
                
                byte[] bytes = new byte[2048];
                int length;
                ByteBuffer bb = ByteBuffer.allocate(dataSize);
                for (int i=0; i<dataSize; i+=length) {
                    
                  length = inFromServerData.read(bytes);
                  bb.put(bytes, 0, length);
                }
                data = bb.array();
//                inFromServerData.read(data, 0, dataSize);
                if(data==null){
                    System.err.println("Error");
                    continue;
                }
                String path = "E:\\Education\\Network\\Pro\\Client\\Recieved\\";
                File file = new File(path);
                file.mkdir();
                path = path.replace("\\", "/");
                DataOutputStream os = new DataOutputStream(new FileOutputStream(path+fileRequested));
                os.write(data, 0, data.length);
                os.close();
                System.out.println("file Saved.");
                    
            }
            else if(command.equals("4")){
                    outToServerControl.writeUTF("LIST");
                    System.out.println(inFromServerData.readUTF());
            }
            else if(command.equals("5")){
                    
                    disconnect();
                    break;
            }
        }

        /*
        String response = readLine();
        if (!response.startsWith("220 ")) {
          throw new IOException(
              "SimpleFTP received an unknown response when connecting to the FTP server: "
                  + response);
        }

        sendLine("USER " + username);

        response = readLine();
        if (!response.startsWith("331 ")) {
          throw new IOException(
              "SimpleFTP received an unknown response after sending the user: "
                  + response);
        }

        sendLine("PASS " + password);

        response = readLine();
        if (!response.startsWith("230 ")) {
            throw new IOException(
              "SimpleFTP was unable to log in with the supplied password: "
                  + response);
        }

        // Now logged in.
        System.out.println("Logged in");
        */
        
    }
    
    
    public synchronized void disconnect(){
        try {
            outToServerControl.writeUTF("QUIT");
        } catch (IOException ex) {
            Logger.getLogger(ClientFTP.class.getName()).log(Level.SEVERE, null, ex);
        }
          clientSocketControl = null;
          clientSocketData = null;
        
    }
    
    private void sendLine(String line) throws Exception {
    if (clientSocketControl == null) {
      throw new IOException("SimpleFTP is not connected.");
    }
    try {
      outToServerControl.writeBytes(line + "\r\n");
      outToServerControl.flush();
    
    } catch (IOException e) {
      clientSocketControl = null;
      throw e;
    }
  }

  private String readLine() throws Exception {
      System.out.println("NOW");
      outToServerControl.writeUTF("U");
      System.out.println("NOW2");
    String line = inFromServerControl.readLine();
    System.out.println("Raaded line:"+line);
    return line;
  }
    
}
