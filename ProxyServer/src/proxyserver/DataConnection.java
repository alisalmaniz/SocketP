/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxyserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 *
 * @author AliSalmani
 */
public class DataConnection implements Runnable {

    DataInputStream inFromClient;
    DataOutputStream outToClient;
    Socket dataSocket;
    ControlConnection cc;
    int clientNumber;
    ServerSideByHTTP serverSideByHTTP;
    
    public DataConnection(int clientNumber){
        this.clientNumber = clientNumber;
        serverSideByHTTP = new ServerSideByHTTP();
        //(new Thread(this)).start();
    }
    public void start(Socket dataSocket, ControlConnection cc) throws Exception{
        
        this.dataSocket = dataSocket;
        this.cc = cc;
        
        inFromClient = new DataInputStream(dataSocket.getInputStream());
        outToClient = new DataOutputStream(dataSocket.getOutputStream());
        
//        (new Thread(this)).start();
    }
    
    public void write(String data) throws Exception{
        outToClient.writeUTF(data);
    }
    
    public void getFileAndCache(String fileName) throws Exception{
        
        String path = "E:\\Education\\Network\\Pro\\ProxyServer\\proxyCache"+clientNumber+"\\";
        File file = new File(path);
        file.mkdir();
        file = new File(path+fileName);
        if(file.exists()){
            ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "file cached.");
            
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();
            
            if(fileData==null){
                outToClient.writeInt(-1);
                return;
            }
           // if error file data is null
            outToClient.writeInt(fileData.length);
            if(!inFromClient.readUTF().equals("OK")){
                return;
            }
            outToClient.write(fileData, 0, fileData.length);
        }
        else{
            ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "get file from server.");
            
            fileName = fileName.replace(" ", "%20");
        
            byte[] fileData = serverSideByHTTP.getDataBinary(fileName);

            if(fileData==null){
                outToClient.writeInt(-1);
                return;
            }
            // if error file data is null
            outToClient.writeInt(fileData.length);
            if(!inFromClient.readUTF().equals("OK")){
                return;
            }
            
            
            outToClient.write(fileData, 0, fileData.length);
            //cache


            path = path.replace("\\", "/");
            fileName = fileName.replace("%20", " ");
            DataOutputStream os = new DataOutputStream(new FileOutputStream(path+fileName));
            os.write(fileData, 0, fileData.length);
            os.close();
        }
        ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "file sent for client.");
        
        
        
        
    }
    
    public void getListOnMainServer() throws Exception{
        String fileNames = serverSideByHTTP.getList();
        String fileNamesdueToMode="";
        if(ProxyServer.accessMode.equals("Admin")){
            fileNamesdueToMode = fileNames;
        }
        else{
            String[] temp = fileNames.split("\n");
            for(int i=0; i<temp.length-1; i++){
                if(!temp[i].startsWith("secret_")){

                    fileNamesdueToMode+=temp[i]+"\n";
                }
            }
        }
        ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "List:");
        ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", fileNamesdueToMode);
            
        outToClient.writeUTF(fileNamesdueToMode);
    }
    
    
    @Override
    public void run() {
        
        
    }
}
