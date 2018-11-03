/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package animaserver;

import animaclient.CommunicationPackiet;
import animaclient.Panel;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kail
 */
public class ServerThread extends Thread{
    
    
    
   //publiczna lista obiektów do wbudzania!!!! 
    
    Socket socket;
    public static List lista_lock = new LinkedList();
    static int a=0;
    final int[] value = new int[1];
    Object lock;
    static String lastmsg;
    static int lastmsgid=0;
    static Boolean inicialized=false;
    static int[][] map = new int[500][500];
    static public Action[] akcje= new Action[500];
    static public int licznik_akcji =0;
    int ostatnia_wykonana_akcja=licznik_akcji+1;////uwaga na to??
    public static int lastgamer=0;
    private int id=lastgamer++;
    int posx=250;
    int posy=250;
    
    
    int lastreadmsg=lastmsgid;          ////uwaga na to
    CommunicationPackiet pakiet;
    static void inicialize(){
        ObjectInputStream ois;
        akcje[0]=new Action(0,-1);

         try {
            ois = new ObjectInputStream(new FileInputStream("map.per"));
            map = (int[][]) (ois.readObject());
            ois.close();
        } catch (IOException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
            System.out.print("err:ois = new ObjectInputStream(new FileInputStream(\"plik.per\"));");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
            System.out.print("err:int[][] ia = (int[][]) (ois.readObject());");
    }
    inicialized=true;
    
    System.out.println("zainicjalizowano");
    }
    ServerThread(Socket socket){
        if (!inicialized){
            inicialize();
        }
        this.socket=socket;
        lock = new Object();
        lista_lock.add(lock);
        
    }
    static synchronized void msgsend(String msg){
       lastmsgid++;
       lastmsg=msg;
    }

    int[][] localmap(int x, int y){
    int[][] localmap = new int[90][150];
        for (int i=0;i<90;i++)
            for(int j=0; j<150; j++){
            localmap[i][j]=map[i+x][j+y];
            }
    return localmap;
    }
    
    public void notify_all()
            {
            for (Object ojbect : lista_lock) {
                synchronized(ojbect){
                ojbect.notifyAll();
                }
            } 
            }
    public void run(){
        
    String message=null;
        try {
            ObjectOutputStream outToClient = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inFromClient = new ObjectInputStream(socket.getInputStream());
            PushbackInputStream pis = new PushbackInputStream(socket.getInputStream());///CO TO KWA JEST???
            InThread test = new InThread(inFromClient, lock);
            test.start();
//            animaclient.CommunicationPackiet pakiet = new animaclient.CommunicationPackiet(0, "null");
            pakiet = new CommunicationPackiet("Witamy na serverze id:"+id);
            pakiet.Info();
            try {
                outToClient.writeObject(pakiet);
            } catch (IOException ex) {
                Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
            }
            akcje[((++licznik_akcji)%500)]=new Action(1,id);
            ostatnia_wykonana_akcja++;
      
            notify_all();
            
            while(socket.isConnected()){
                

//                int tempByte = -1;
//                if ((tempByte = inFromClient.read()) != -1) {
//                pakiet = (animaclient.CommunicationPackiet) inFromClient.readObject();
//                pakiet.Info();
//                
//                }
                
            try{
                synchronized(lock){
                if (test.opt==1){  
                    switch (test.pakiet.getType()) {
                        case 1:
                            
                            break;
                        case 2:
                            break;

                        case 101:
                            System.out.println("Gracz:" + id + " poruszyl <-");
                            posx--;
                            pakiet = new CommunicationPackiet(localmap(posx, posy));
                            break;
                        case 102:
                            System.out.println("Gracz:" + id + " poruszyl sie \\/");
                            posy--;
                            pakiet = new CommunicationPackiet(localmap(posx, posy));
                            break;
                        case 103:
                            System.out.println("Gracz:" + id + " poruszyl sie ->");
                            posx++;
                            pakiet = new CommunicationPackiet(localmap(posx, posy));
                            break;
                        case 104:
                            System.out.println("Gracz:" + id + " poruszyl sie/\\");
                            posy++;
                            pakiet = new CommunicationPackiet(localmap(posx, posy));
                            break;
                        default:
                            System.out.println("Błędny typ pakietu");
                            break;
                    }
                test.opt=0;

                lock.notify();
                
//                pakiet = new CommunicationPackiet(localmap(300,300));
                try {
                    outToClient.writeObject(pakiet);
                } catch (IOException ex) {
                    Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
                } } 
                lock.wait();
                while (ostatnia_wykonana_akcja<=licznik_akcji){
                    if (akcje[ostatnia_wykonana_akcja % 500].typ == 1) {
                        pakiet = new CommunicationPackiet("Zalogowal sie grac o id:" + akcje[ostatnia_wykonana_akcja % 500].autor);
                        pakiet.Info();
                        try {
                            outToClient.writeObject(pakiet);
                        } catch (IOException ex) {
                            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                
                
                }
                
                
//                if(ostatnia_wykonana_akcja==licznik_akcji)break;
                ostatnia_wykonana_akcja++;
                }
                
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }

   
        
        
        
        }
            
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            PrintStream out = new PrintStream(socket.getOutputStream());
//            String nickname=bufferedReader.readLine();
//            System.out.println("New player connected.");
//           
//            while(socket.isConnected()){
//            while(bufferedReader.ready()){
//            //message = bufferedReader.readLine();
//                
//            System.out.println(nickname+":"+message+":"+a);
//            msgsend(nickname+":"+message); 
//            a++;
//            }
//            if(lastreadmsg<lastmsgid){
//                    lastreadmsg++;
//                    out.println(lastmsg);///////////////////////potwierdzenie pakietu
//                }
//            
//            }
//            while ((message = bufferedReader.readLine())!=null){
//                
//                System.out.println(nickname+":"+message+":"+a);
//                msgsend(nickname+":"+message+":"+lastreadmsg+":"+lastmsgid);
//                if(lastreadmsg<lastmsgid){
//                    lastreadmsg++;
//                    out.println(lastmsg);///////////////////////potwierdzenie pakietu
//                }
//                //out.println("potwierdzono");///////////////////////potwierdzenie pakietu
//                a++;  
//            }

            socket.close();
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
