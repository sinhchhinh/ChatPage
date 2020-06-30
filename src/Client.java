import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Client implements ActionListener
{
  Socket socket;
  DataInputStream dataInputStream;
  DataOutputStream dataOutputStream;
  Color dim_gray   = new Color (220,220,220);
  Color light_blue = new  Color (240,248,255);
  Color azure      = new Color (248,248,255);

  JButton sendButton, logoutButton,loginButton, exitButton;
  JFrame chatWindow;
  JTextArea txtBroadcast;
  JTextArea txtMessage;
  JList<String> usersList;

  /**
   * Client Constructor
   */
  public Client()
  {
    displayGUI();
  }

  public static void main(String []args)
  {
    new Client();
  }

  /**
   * Laying out the Chat UI
   */
  public void displayGUI()
  {
    chatWindow = new JFrame();
    txtBroadcast = new JTextArea(5,30);
    txtBroadcast.setEditable(false);
    txtBroadcast.setBackground(azure);
    txtMessage = new JTextArea(2,20);
    txtMessage.setBackground(azure);

    usersList = new JList<String>();

    sendButton   = new JButton("Send");    
    logoutButton = new JButton("Log out");
    loginButton  = new JButton("Log in");
    exitButton   = new JButton("Exit");

    setButtonUI(logoutButton, light_blue);
    setButtonUI(loginButton, light_blue);
    setButtonUI(exitButton, light_blue);
    setButtonUI(sendButton, light_blue);


    JLabel msgTitle =  new JLabel ("Diplay all messages from all online users",JLabel.CENTER);

    //text diplay area     
    JPanel center1 = new JPanel();
    center1.setLayout(new BorderLayout());
    center1.add (msgTitle,"North");
    center1.add(new JScrollPane(txtBroadcast),"Center");

    JPanel south1 = new JPanel();
    south1.setLayout(new FlowLayout());
    south1.add(new JScrollPane(txtMessage));
    south1.add(sendButton);

    JPanel south2 =  new JPanel();
    south2.setLayout(new FlowLayout());
    south2.add(loginButton);
    south2.add(logoutButton);
    south2.add(exitButton);
    south2.setBackground(dim_gray);


    JPanel south = new JPanel();
    south.setLayout(new GridLayout(2,1));
    south.add(south1);
    south.add(south2);


    JPanel east = new JPanel();
    east.setLayout(new BorderLayout());
    east.add(new JLabel("Online Users", SwingConstants.CENTER), "North");
    usersList.setBackground(azure);
    east.add(new JScrollPane(usersList),"Center");

    //Setting chatWindow UI
    chatWindow.setPreferredSize(new Dimension(500, 500));
    chatWindow.add(east,"East");
    chatWindow.add(center1,"Center");
    chatWindow.add(south,"South");
    chatWindow.pack();
    chatWindow.setTitle("Login for Chat");
    chatWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    chatWindow.setVisible(true);

    sendButton.addActionListener(this);
    logoutButton.addActionListener(this);
    loginButton.addActionListener(this);
    exitButton.addActionListener(this);
    logoutButton.setEnabled(false);
    loginButton.setEnabled(true);

    txtMessage.addFocusListener(new FocusAdapter()
    {
      public void focusGained(FocusEvent fe)
      {
        txtMessage.selectAll();
      }
    });


    chatWindow.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent ev)
      {
        if(socket!=null)
        {
          JOptionPane.showMessageDialog(chatWindow,"Processing to log out","Exit",JOptionPane.INFORMATION_MESSAGE);
          logoutSession();
        }
        System.exit(0);
      }
    });
  }

  public void setButtonUI (JButton button, Color color) {
    button.setBackground(color);
    button.setBorderPainted(false);
    button.setOpaque(true);
  }


  public void actionPerformed(ActionEvent event)
  {
    JButton temp = (JButton) event.getSource();

    //sendButton Action
    if(temp == sendButton)
    {
      if(socket==null)
      {
        JOptionPane.showMessageDialog(chatWindow,"Please log in inorder to chat"); 
        return;
      }

      try
      {
        dataOutputStream.writeUTF(txtMessage.getText());
        txtMessage.setText("");
      }
      catch(Exception excp)
      {
        txtBroadcast.append("\nsend button click :"+excp);
      }
    }

    //loginButton Action
    if(temp==loginButton)
    {
      String uname = JOptionPane.showInputDialog(chatWindow,"Enter your chat name: ");
      if(uname!=null)
      {
        clientChat(uname); 
      }
    }

    if(temp==logoutButton)
    {
      if(socket!=null)
        logoutSession();
    }

    //exitButton Action
    if(temp==exitButton)
    {
      if(socket != null)
      {
        JOptionPane.showMessageDialog(chatWindow,"Loging Out of the Chat","Exit",JOptionPane.INFORMATION_MESSAGE);
        logoutSession();
      }
      System.exit(0);
    }
  }

  /**
   * Creating a socket to allow user to send chat
   * @param uname
   */
  public void clientChat (String uname)
  {
    try
    {
      String address = "10.0.0.211";
      int PORT = 8800;

      socket = new Socket(address,PORT);

      dataInputStream  = new DataInputStream(socket.getInputStream());
      dataOutputStream = new DataOutputStream(socket.getOutputStream());

      ClientThread clientThread = new ClientThread(dataInputStream,this);
      Thread t1 = new Thread(clientThread);
      t1.start();
      dataOutputStream.writeUTF(uname);
      chatWindow.setTitle(uname+" Chat Window");
    }
    catch(Exception e)
    {
      txtBroadcast.append("\nClient Constructor " +e);
    }
    logoutButton.setEnabled(true);
    loginButton.setEnabled(false);
  }

  /**
   * make the socket null
   * 
   */
  public void logoutSession()
  {
    if(socket == null) 
      return;
    
    try
    {
      dataOutputStream.writeUTF(Server.LOGOUT_MESSAGE); //telling the Server a chat is quiting
      Thread.sleep(500);
      socket = null;
    }
    catch(Exception e)
    {
      txtBroadcast.append("\n inside logoutSession Method"+e);
    }

    logoutButton.setEnabled(false);
    loginButton.setEnabled(true);
    chatWindow.setTitle("Login for Chat");
  }



}


