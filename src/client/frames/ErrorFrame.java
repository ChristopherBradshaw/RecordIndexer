package client.frames;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class ErrorFrame extends JDialog
{
	public ErrorFrame(String message)
	{
		setModal(true);
		setTitle("Error");
		setResizable(false);
		setSize(300,125);
		setLocationRelativeTo(null);
		
		
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel messagePanel = new JPanel();

		messagePanel.add(new JLabel(message));
		messagePanel.setBorder(new EmptyBorder(10,0,0,0));

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
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