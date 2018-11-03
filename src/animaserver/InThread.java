/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package animaserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kail
 */
public class InThread extends Thread{
    
    
   
    ObjectInputStream in;
    public int opt=0;
    Object obj;
    public animaclient.CommunicationPackiet pakiet = new animaclient.CommunicationPackiet("null");
public InThread (ObjectInputStream inFromClient, Object lock){
    
    in = inFromClient;
    obj=lock;
}    

public synchronized void run(){
     while(true){       
       
        
            try { 
            if (opt==0){ 
                pakiet = (animaclient.CommunicationPackiet) in.readObject();
            }
            synchronized(obj){ ////////////////////UWAZAC NA TO W HUJJJ
                if (opt==0){ 
            pakiet.Info();
            System.out.println("opt:"+opt);
            opt=1;                
            obj.notify();    
            }    
             obj.wait(); 
            }
            } catch (IOException ex) {
                System.out.println("Client lost connectioon");
//                Logger.getLogger(InThread.class.getName()).log(Level.SEVERE, null, ex);
                break;
            } catch (ClassNotFoundException ex) {   
                Logger.getLogger(InThread.class.getName()).log(Level.SEVERE, null, ex);
                break;
                
            } catch (InterruptedException ex) {
               Logger.getLogger(InThread.class.getName()).log(Level.SEVERE, null, ex);
           }

            
        
     
       
      } 
    }
}
