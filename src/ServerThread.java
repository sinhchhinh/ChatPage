import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ServerThread implements Runnable {

  Socket socket;
  ArrayList al;
  ArrayList<String> users;
  String username;
 
  ServerThread (Socket socket, ArrayList al,ArrayList<String> users)
  {
    this.socket = socket;
    this.al = al;
    this.users = users;
   
    try
    {
      DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
      username = dataInputStream.readUTF();
      al.add(socket);
      users.add(username);
      tellEveryOne("****** "+ username+" Logged in at "+(new Date())+" ******");
      sendNewUserList();
    }
    catch(Exception e)
    {
      System.err.println("ServerThread constructor  "+e);
     }
  }
  
  public void run()
  {
    String string1;
    try
    {
      DataInputStream dis = new DataInputStream(socket.getInputStream());
      do
      {
        string1 = dis.readUTF();
        if(string1.toLowerCase().equals(Server.LOGOUT_MESSAGE)) 
          break;
        //  System.out.println("received from "+s.getPort());
        tellEveryOne(username+" said: "+" : "+ string1);
      }
      while(true);
      DataOutputStream tdos = new DataOutputStream(socket.getOutputStream());
      
      tdos.writeUTF(Server.LOGOUT_MESSAGE);
      tdos.flush();
      
      users.remove(username);
      tellEveryOne("****** "+username+" Logged out at "+(new Date())+" ******");
      
      sendNewUserList();
      al.remove(socket);
      socket.close();

    }
    catch(Exception e)
    { 
      System.out.println("ServerThread Run"+e);
      }
  }

  public void sendNewUserList()
  {
    tellEveryOne(Server.UPDATE_USERS+users.toString());

  }
  public void tellEveryOne(String s1) 
  {
    Iterator i = al.iterator();
    while(i.hasNext())
    {
      try
      {
        Socket temp = (Socket)i.next();
        DataOutputStream dos = new DataOutputStream(temp.getOutputStream());
        dos.writeUTF(s1);
        dos.flush();
        //System.out.println("sent to : "+temp.getPort()+"  : "+ s1);
      }
      catch(Exception e)
      {
        System.err.println("TellEveryOne "+e);
      }
    }
  }

}
