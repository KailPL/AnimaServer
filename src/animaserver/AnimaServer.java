/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package animaserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Kail
 */
public class AnimaServer {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static final int port = 2030;
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
         int n1=666;
        new AnimaServer().runServer();
        
//        Scanner sc=new Scanner(ss.getInputStream());

        
//        while(!(sc.hasNext()));
//        
//        if (sc.hasNext()){
//            System.out.println("cos jest");
//            n1=sc.nextInt();
//            System.out.println("liczba to:"+n1);
//        }else{
//            System.out.println("nima");
//        }
    }
    public void runServer() throws IOException{
        ServerSocket ssocket = new ServerSocket(port);
        System.out.println("Server wstał na porcie:"+port);
        ServerThread.inicialize();

        while(true){
            Socket ss=ssocket.accept();
            new ServerThread(ss).start();
        }
   
    }
    
    
}
