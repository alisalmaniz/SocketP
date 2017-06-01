/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxyserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author AliSalmani
 */
public class ControlConnection implements Runnable{

//    BufferedReader inFromClient;
    DataInputStream inFromClient;
    DataOutputStream outToClient;
    Socket controlSocket;
    DataConnection dc;
    int clientNumber;
    ServerSideByHTTP serverSideByHTTP;
    
    public ControlConnection(int clientNumber){
        this.clientNumber = clientNumber;
        serverSideByHTTP = new ServerSideByHTTP();
        //(new Thread(this)).start();
    }
    
    public void start(Socket controlSocket, DataConnection dc) throws Exception{
        
        this.controlSocket = controlSocket;
        this.dc = dc;
        
//        inFromClient = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
        inFromClient = new DataInputStream(controlSocket.getInputStream());
        outToClient = new DataOutputStream(controlSocket.getOutputStream());
//        System.out.println("FTP Client Connected ...");
        (new Thread(this)).start();
    }
    
    public void write(String data) throws Exception{
        outToClient.writeUTF(data);
    }
    
    public String deleteAllCached(){

        String path = "E:\\Education\\Network\\Pro\\ProxyServer\\proxyCache"+clientNumber+"\\";
        File file = new File(path);
        File[] fileIn = file.listFiles();
        if(fileIn==null)
            return "OK";
        for(int i=0; i<fileIn.length; i++){
            if(fileIn[i].getName().startsWith("secret_"))
                if(ProxyServer.accessMode.equals("User"))
                    continue;
            fileIn[i].delete();
        }
            
        ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "All cached files deleted.");
        return "All cached files deleted.";
    }
    
    public String deleteCachedFile(String fileName){
        String path = "E:\\Education\\Network\\Pro\\ProxyServer\\proxyCache"+clientNumber+"\\";
        File file = new File(path+fileName);
        if(file.getName().startsWith("secret_"))
            if(ProxyServer.accessMode.equals("User"))
                return "Access denided";
        
        if(!file.exists())
            return "not Found";
        
        
        file.delete();
        ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "cached file deleted.");
        return "cached file deleted.";
    }
    
    @Override
    public void run() {
        
        String username="";
        String password="";
        
        while(true){
            try{
                System.out.println("Waiting for Command ...");
                String Command=inFromClient.readUTF();
                
                if(Command.startsWith("USER ")){
                    username = Command.substring(5);
                    ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "username received.");
                    if(username.equals("Admin") || username.equals("anonymous")){
                        outToClient.writeUTF("220 OK.");
                        ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "username is valid.");
                    
                    }
                    else{
                        outToClient.writeUTF("440 ERROR.");
                        ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "username is not valid.");
                    
                    }
                }
                else if(Command.startsWith("PASS ")){
                    password = Command.substring(5);
                    ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "password received.");
                    
                    if(username.equals("Admin") && password.equals("Admin")){
                        outToClient.writeUTF("220 OK.");
                        ProxyServer.accessMode = "Admin";
                        ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "logged in as Admin.");
                    
                    }
                    else if(username.equals("anonymous")){
                        outToClient.writeUTF("220 OK.");
                        ProxyServer.accessMode = "User";
                        ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "logged in as usual user.");
                    
                    }
                    else{
                        outToClient.writeUTF("440 ERROR.");
                        ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "password is not valid.");
                    
                    }
                }
                else if(Command.compareTo("RMD")==0)
                {
                    
                    ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "RMD Command Receiced ...");
                    outToClient.writeUTF(deleteAllCached());
                }
                else if(Command.startsWith("DELE"))
                {
                    ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "DELE Command Receiced ...");
                    outToClient.writeUTF(deleteCachedFile(Command.substring(5)));
    
                }
                else if(Command.startsWith("RETR"))
                {
                    
                    ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "RETR Command Receiced ...");
                    String fileRequested = Command.substring(5);
                    if(fileRequested.startsWith("secret_"))
                        if(ProxyServer.accessMode.equals("User")){
                            ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "Access Denided.");
                            continue;
                        }
                    
                    dc.getFileAndCache(fileRequested);
                    
                }
                else if(Command.compareTo("LIST")==0)
                {
                    ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "LIST Command Receiced ...");
                    dc.getListOnMainServer();
                    
                }
                else if(Command.compareTo("QUIT")==0)
                {
                    ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "QUIT Command Receiced ...");
                    ClientSideByFTP.log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "Connection Closed.");
                    break;
                }
            }
            catch(Exception ex){
                
            }
        }
        
    }
    
}
