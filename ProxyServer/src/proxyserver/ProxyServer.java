/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxyserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AliSalmani
 */
public class ProxyServer {

    public static final int CONTROL_SOCKET = 4021;
    public static final int DATA_SOCKET = 4020;
    public static final String LOCAL_HOST = "127.0.0.1";
    public static final String SERVERLOGS = "ServerLogs";
    public static final String CLIENTLOGS = "ClientLogs";
    public static String accessMode = "Admin";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        ClientSideByFTP clientSideByFTP  = new ClientSideByFTP();
     
        try {
            
            clientSideByFTP.startAsServer();
            
        } catch (Exception ex) {
            Logger.getLogger(ProxyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
}
