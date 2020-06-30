import java.io.DataInputStream;
import java.util.StringTokenizer;
import java.util.Vector;

class ClientThread implements Runnable
{
  DataInputStream dataInputStream;
  Client client;

  ClientThread (DataInputStream dataInputStream,Client client)
  {
    this.dataInputStream = dataInputStream;
    this.client = client;
  }

  public void run()
  {
    String s2 = "";
    do
    {
      try
      {
        s2 = dataInputStream.readUTF(); //reading from the server after clicking send 
        if(s2.startsWith(Server.UPDATE_USERS))
          updateUsersList(s2);

        else if(s2.equals(Server.LOGOUT_MESSAGE))
          break;

        else
          client.txtBroadcast.append("\n"+s2);

        int lineOffset = client.txtBroadcast.getLineStartOffset(client.txtBroadcast.getLineCount() - 1);
        client.txtBroadcast.setCaretPosition(lineOffset);
      }
      catch(Exception e)
      {
        client.txtBroadcast.append("\nClientThread run : "+e);
      }
    }
    while(true);
  }

  /**
   * 
   * @param updateUsrList - current list of users
   */
  public void updateUsersList(String updateUsrList) 
  {
    Vector<String> ulist = new Vector<String>();
    updateUsrList = updateUsrList.replace("[","");
    updateUsrList = updateUsrList.replace("]","");
    updateUsrList = updateUsrList.replace(Server.UPDATE_USERS,"");

    StringTokenizer st = new StringTokenizer(updateUsrList,",");

    while(st.hasMoreTokens())
    {
      String temp = st.nextToken();
      ulist.add(temp);
    }
    client.usersList.setListData(ulist);
  }
}


