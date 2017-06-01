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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AliSalmani
 */
public class ClientSideByFTP implements Runnable{
    
    DataInputStream inFromClient;
    DataOutputStream outToClient;
    int clientNumber;
    public ClientSideByFTP() {
        
    }
    
    public void startAsServer() throws Exception{
        
        (new Thread(this)).start();
        clientNumber = 1;
        
        String path = "E:\\Education\\Network\\Pro\\ProxyServer\\logs\\";
        File file = new File(path);
        file.mkdir();
        file = new File(path+ProxyServer.SERVERLOGS+".txt");
        FileWriter fw = new FileWriter(file, true);
        fw.write("Logs:\n");
        fw.close();
        
        ServerSocket welcomingSocket=new ServerSocket(ProxyServer.CONTROL_SOCKET);
        System.out.println("FTP Server Started on Port Number "+ welcomingSocket.getLocalPort()); 

        while(true)
        {
            System.out.println("Waiting for Connection ...");
            Socket controlScket = welcomingSocket.accept();
//            System.err.println("New Client From "+ controlScket.getInetAddress() + ":"+ controlScket.getLocalPort() + " Connected to Server!");
            
            Socket dataScket = welcomingSocket.accept();
            
//            outToClient.writeUTF("200");
//            String line = inFromClient.readLine();
//            System.out.println("L:"+line);
//            inFromClient.readLine();

            file = new File(path+ProxyServer.CLIENTLOGS+clientNumber+".txt");
            fw = new FileWriter(file, true);
            fw.write("Logs:\n");
            fw.close();
            
            log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "New Client From "+ controlScket.getInetAddress() + ":"+ controlScket.getLocalPort());
            log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "Control Connection established.");
            log(ProxyServer.CLIENTLOGS+clientNumber+".txt", "Data Connection established.");
            
            ControlConnection cc = new ControlConnection(clientNumber);
            DataConnection dc = new DataConnection(clientNumber);
            cc.start(controlScket, dc);
            dc.start(dataScket, cc);
            clientNumber++;
            
        }
        
        
    }
    
    public static void log(String fileName, String log){
        System.out.println(log);

        String path = "E:\\Education\\Network\\Pro\\ProxyServer\\logs\\";
        File file = new File(path);
        file.mkdir();
        file = new File(path+fileName);
        FileWriter fw;
        try {
            fw = new FileWriter(file, true);
            fw.write(log+"\n");
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientSideByFTP.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

    @Override
    public void run() {
        
        int md = 1000*60*60*24;
        String path;
        ServerSideByHTTP ser = new ServerSideByHTTP();
        SimpleDateFormat sdf;
        String Mylast;
//        String ServerLast;
        String recieved;
        String[] mt;
        String[] st;
        while(true){
            try {
                Thread.sleep(md);
            
                for(int j=1; j<=clientNumber; j++){
                    path = "E:\\Education\\Network\\Pro\\ProxyServer\\proxyCache"+clientNumber+"\\";
                    File file = new File(path);
                    File[] fileIn = file.listFiles();
                    if(fileIn==null)
                        continue;
                    for(int i=0; i<fileIn.length; i++){

                        recieved = ser.getData(fileIn[i].getName());
                        st = recieved.split("Last-Modified: ");
                        st = st[1].split(" GMT");
                        st = st[0].split(", ");
                        st = st[1].split(" ");//22 May 2017 19:35:20

                        sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                        Mylast= sdf.format(fileIn[i].lastModified());
                        mt = Mylast.split(" ");//22 May 2017 23:58:14
                        String[] mt2 = mt[3].split(":");
                        String[] st2 = st[3].split(":");

                        if(!mt[0].equals(st[0]) || !mt2[0].equals(st2[0])
                            || !mt2[1].equals(st2[1]) || !mt2[2].equals(st2[2])
                            ){

                            String fileName = fileIn[i].getName().replace(" ", "%20");
                            byte[] fileData = ser.getDataBinary(fileName);
                            path = path.replace("\\", "/");
                            fileName = fileName.replace("%20", " ");
                            DataOutputStream os = new DataOutputStream(new FileOutputStream(path+fileName));
                            os.write(fileData, 0, fileData.length);
                            os.close();
                        }

                    }
                }
            
            } catch (Exception ex) {
                Logger.getLogger(ClientSideByFTP.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    
}
