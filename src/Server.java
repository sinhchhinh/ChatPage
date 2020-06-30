import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Connecting a local server
 * Allows the ability to send text messages within the local server
 */
public class Server {

  ArrayList<String> al = new ArrayList<String>();
  ArrayList<String> users = new ArrayList<String>();
  ServerSocket socketServer;
  Socket socket;

  public final static int PORT = 8800;
  public final static String UPDATE_USERS = "updateuserslist:";
  public final static String LOGOUT_MESSAGE = "@@logoutme@@:";
  
  public Server()
  {
    try
    {
      socketServer = new ServerSocket(PORT);

      while(true)
      {
        socket = socketServer.accept();
        Runnable r=new ServerThread(socket,al,users);
        Thread thread = new Thread(r);
        thread.start();
      }
    }
    catch(Exception e)
    {
      System.err.println("Server constructor"+e);
    }
  }

  public static void main(String [] args)
  {
    System.out.println("Start processing the server ");
    new Server();
  }
}
