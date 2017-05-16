package client.frames;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import shared.communication.ValidateUserInput;
import shared.communication.ValidateUserOutput;
import shared.model.User;
import client.Client;
import client.ClientException;
import client.Client.STATE;
import client.facade.ClientFacade;

@SuppressWarnings("serial")
public class LoginFrame extends JFrame
{		
	private JPanel textPane;
	private JPanel textPaneUsername;
	private JPanel textPanePassword;
	private JTextField usernameField;
	private JPasswordField passwordField;
	
	private JPanel buttonPane;
	private JButton loginButton;
	private JButton exitButton;
	
	WelcomeFrame welcomeFrame;
	InvalidFrame invalidFrame;
	ErrorFrame errorFrame;
	
	public LoginFrame()
	{		
		setTitle("Login to Indexer");
		setSize(375,125);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
		textPane = new JPanel(new BorderLayout());
		
		textPaneUsername = new JPanel();
		textPaneUsername.add(new JLabel("Username: "));
		usernameField = new JTextField(20);
		textPaneUsername.add(usernameField);
		
		textPanePassword = new JPanel();
		textPanePassword.add(new JLabel("Password: "));
		passwordField = new JPasswordField(20);
		textPanePassword.add(passwordField);
		
		textPane.add(textPaneUsername,BorderLayout.NORTH);
		textPane.add(textPanePassword,BorderLayout.SOUTH);

		
		buttonPane = new JPanel();
		
		loginButton = new JButton("Login");
		
		loginButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String username = usernameField.getText();
				String password = passwordField.getText();
				
				User resultUser = null;
				try
				{
					resultUser = ClientFacade.validateUser(username, password);
				}
				catch(ClientException e)
				{
					errorFrame = new ErrorFrame("Could not login. Try again later.");
					errorFrame.setVisible(true);
					return;
				}
				
				if(resultUser == null)
				{
					invalidFrame = new InvalidFrame();
					invalidFrame.setVisible(true);
				}
				else
				{
					Client.setUsername(username);
					Client.setPassword(password);
					
					welcomeFrame = new WelcomeFrame(resultUser);	
					welcomeFrame.setVisible(true);
				}
			}
		});
		buttonPane.add(loginButton);
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				dispose();
				Client.setState(STATE.EXIT);
			}
			
		});
		buttonPane.add(exitButton);
		
		add(textPane,BorderLayout.NORTH);
		add(buttonPane,BorderLayout.SOUTH);
	}
}

