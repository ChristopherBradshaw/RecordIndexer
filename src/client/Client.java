package client;

import java.awt.EventQueue;

import client.communication.ClientCommunicator;
import client.frames.IndexerFrame;
import client.frames.LoginFrame;

public class Client
{
	public static final String DEFAULT_HOST = "localhost";
	public static final int DEFAULT_PORT = 39640;

	public static enum STATE
	{
		LOGIN, INDEXER_FRAME, EXIT
	}

	public static void main(String args[])
	{
		Client client = null;
		switch (args.length)
		{
			case 0:
				client = new Client();
				break;
	
			case 1:
				try {
					int port = Integer.parseInt(args[0]);
					client = new Client(port); // Try to construct w/ port
				} catch (Exception e) {
					System.out.println(e.getClass().toString());
				}
	
				client = new Client(args[1]); // Construct w/ host
				break;
	
			case 2:
				try {
					client = new Client(args[0], Integer.parseInt(args[1]));
				} catch (NumberFormatException e) {
					return; // Entered non-numeric port value
				}
	
				break;
	
			default: // Invalid
				return;
		}

		client.startClient();
	}

	
	private static String host;
	private static int port;
	private static LoginFrame login;
	private static IndexerFrame indexer;
	private static STATE currentState;
	private static ClientCommunicator comm;
	
	private static String loginUsername;
	private static String loginPassword;
	
	public Client()
	{
		this.host = Client.DEFAULT_HOST;
		this.port = Client.DEFAULT_PORT;
	}

	public Client(String host)
	{
		this.host = host;
		this.port = Client.DEFAULT_PORT;
	}

	public Client(int port)
	{
		this.host = Client.DEFAULT_HOST;
		this.port = port;
	}

	public Client(String host, int port)
	{
		this.host = host;
		this.port = port;
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}
	
	public static LoginFrame getLoginFrame()
	{
		return login;
	}
	
	public static IndexerFrame getIndexFrame()
	{
		return indexer;
	}
	
	public static ClientCommunicator getComm()
	{
		return comm;
	}
	
	public void startClient()
	{
		comm = new ClientCommunicator(port,host);
		setState(STATE.LOGIN);
	}

	public static void setState(STATE state)
	{
		currentState = state;
		doState(currentState);
	}	
	
	public static String getUsername()
	{
		return loginUsername;
	}
	
	public static String getPassword()
	{
		return loginPassword;
	}
	
	public static void setUsername(String newUsername)
	{
		loginUsername = newUsername;
	}
	
	public static void setPassword(String newPassword)
	{
		loginPassword = newPassword;
	}
	
	private static void doState(STATE state)
	{
		switch (state)
		{
			case LOGIN:
				doLoginFrame();
				break;
			case INDEXER_FRAME:
				doIndexerFrame();
				break;
			case EXIT:
				if(indexer != null)
					indexer.dispose();
				if(login != null)
					login.dispose();
				loginUsername = null;
				loginPassword = null;
				System.exit(0);
				break;
			default:
				break;

		}
	}

	private static void doLoginFrame()
	{
		login = new LoginFrame();
		login.setVisible(true);
	}
	private static void doIndexerFrame()
	{
		EventQueue.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				if (login.isVisible())
					login.dispose();
				
				indexer = new IndexerFrame();
				indexer.setVisible(true);				
			}
		
		});
		
	}
}