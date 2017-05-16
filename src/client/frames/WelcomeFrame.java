package client.frames;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import shared.model.User;
import client.Client;
import client.Client.STATE;
@SuppressWarnings("serial")
class WelcomeFrame extends JDialog
{

	public WelcomeFrame(User userResult)
	{
		super(Client.getLoginFrame(),"Welcome to Indexer",true);
		setSize(300,125);
		setResizable(false);
		setLocationRelativeTo(null);
		
		
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel messagePanel = new JPanel();
		StringBuilder welcomeMessage = new StringBuilder();
		welcomeMessage.append("<html>Welcome, ");
		welcomeMessage.append(userResult.getFirstName() + " ");
		welcomeMessage.append(userResult.getLastName() + ".<br>");
		welcomeMessage.append("You have indexed " + userResult.getCompletedBatches() + " records.</html>");
		messagePanel.add(new JLabel(welcomeMessage.toString()));
		messagePanel.setBorder(new EmptyBorder(10,0,0,0));

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
				Client.setState(STATE.INDEXER_FRAME);
			}	
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.setBorder(new EmptyBorder(0,0,0,0));
		panel.add(buttonPanel,BorderLayout.CENTER);
		panel.add(messagePanel,BorderLayout.NORTH);
		add(panel);
	}
}
