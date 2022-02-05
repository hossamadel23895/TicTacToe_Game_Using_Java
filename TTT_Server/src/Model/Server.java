package Model;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class Server {
    private final int port;
    private ServerSocket s;
    public Server(int port)
    {
       this.port=port;
       startServer();
    }
  
    private void startServer()
    {
        new Thread(() -> {
        System.out.println("server running...");
        try {
            s=new ServerSocket(port);
            while (true) {                
                Socket socket= s.accept();
                System.out.println("New client connected ");
                //class server thread to create new thread with each new client
                new ServerThread(socket).start();
            }
           
        } catch (IOException ex) {
            System.out.println("Error in server socket");
            ex.getStackTrace();
        }
        }).start();
    }
    
    public void closeServer()
    {
            try {
                s.close();
                System.out.println("sever closed.");
                
            } catch (IOException ex) {
                System.out.println("Error while closing sever");
                ex.getStackTrace();
            }
    }
}
