/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AliSalmani
 */
public class Client {

    public static final int CONTROL_SOCKET = 4021;
    public static final int DATA_SOCKET = 4020;
    public static final String LOCAL_HOST = "127.0.0.1";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ClientFTP  clientFTP  = new ClientFTP();
        
        //TODO path="./"
        //TODO get host ip with system.in, dont use local host
        
        
        try {
            clientFTP.connect();
            
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
