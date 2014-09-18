import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/*
 * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JLabel serverLabel, portLabel, userLabel, passLabel, msgLabel;
	// to hold the Username and later on the messages
	private JTextField tfMsg;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort, tfUser, tfPass;
	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn;
	// for the chat room
	private JTextArea ta;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort;
	private String defaultHost;

	// Constructor connection receiving a socket number
	ClientGUI(String host, int port) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		// Main Panels
		JPanel northPanel = new JPanel(new GridLayout(2,1));
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		JPanel msgPanel = new JPanel(new GridLayout(1,1));
		//JPanel msg = new JPanel(new GridLayout(1,1));
		// NorthPanel components
		JPanel serverAndPort = new JPanel(new GridLayout(1,5,1,3));
		JPanel loginID = new JPanel(new GridLayout(1,5,1,3));

		// the two JTextField with default value for server address and port number
		serverLabel = new JLabel("Server Address: ");
		portLabel = new JLabel("Port: ");		
		userLabel = new JLabel("Username: ");
		passLabel = new JLabel("Password: ");
		
		msgLabel = new JLabel("Message: ");

		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfUser = new JTextField("");
		tfPass = new JTextField("");

		tfMsg = new JTextField("");

		//tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(serverLabel);
		serverAndPort.add(tfServer);
		serverAndPort.add(portLabel);
		serverAndPort.add(tfPort);
		northPanel.add(serverAndPort);

		// the Label and the TextField
		loginID.add(userLabel);
		loginID.add(tfUser);
		loginID.add(passLabel);
		loginID.add(tfPass);
		northPanel.add(loginID);
		//tf = new JTextField("");
		
		//northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new GridLayout(2,1));

		msgPanel.add(msgLabel);
		msgPanel.add(tfMsg);
		southPanel.add(msgPanel);

		// the 3 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);		// you have to login before being able to Who is in
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(login);
		buttonPanel.add(logout);
		buttonPanel.add(whoIsIn);
		southPanel.add(buttonPanel);

		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tfUser.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(true);
		tfPort.setEditable(true);
		tfUser.setEditable(true);
		tfPass.setEditable(true);
		// don't react to a <CR> after the username
		tfMsg.removeActionListener(this);
		connected = false;
	}
		
	/*
	* Button or JTextField clicked
	*/
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == logout) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			return;
		}
		// if it the who is in button
		if(o == whoIsIn) {
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			return;
		}

		// ok it is coming from the JTextField
		if(connected) {
			// just have to send the message
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tfMsg.getText()));				
			tfMsg.setText("");
			return;
		}
		

		if(o == login) {
			String username = tfUser.getText().trim();
			String password = tfPass.getText().trim();
			String server = tfServer.getText().trim();
			String portNumber = tfPort.getText().trim();

			// Ignore blank fields
			if(username.length() == 0)
				return;
			if(password.length() == 0)
				return;
			if(server.length() == 0)
				return;
			if(portNumber.length() == 0)
				return;
			
			int port = 0;

			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   // nothing I can do if port number is not valid
			}

			// try creating a new Client with GUI
			client = new Client(server, port, username, password, this);
			
			// Check if client will start
			if(!client.start()) 
				return;

			connected = true;
			
			// disable login button
			login.setEnabled(false);
			// enable the 2 buttons
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);

			tfMsg.setEditable(true);

			// Disable textfields once logged in.
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			tfUser.setEditable(false);
			tfPass.setEditable(false);

			// Action listener for when the user enters a message
			tfMsg.addActionListener(this);
		}

	}

	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI("localhost", 1500);
	}

}
