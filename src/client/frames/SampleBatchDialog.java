package client.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import shared.communication.GetBatchInput;
import shared.communication.ValidateUserInput;
import shared.model.Project;
import client.Client;
import client.ClientException;
import client.communication.ClientCommunicator;
import client.facade.ClientFacade;

@SuppressWarnings("serial")
class SampleBatchDialog extends JDialog
{
	public SampleBatchDialog(Project project)
	{
		setModal(true);
		setTitle("Sample Image from " + project.getTitle());
		setResizable(false);
		setSize(500, 500);
		setLocationRelativeTo(null);
		setBackground(Color.WHITE);

		JPanel panel = new JPanel(new BorderLayout());
		BufferedImage image = null;
		try
		{

			URL url = new URL(ClientCommunicator.getURL()
					+ File.separator
					+ ClientFacade.getSample(new GetBatchInput(
							new ValidateUserInput(Client
									.getUsername(), Client
									.getPassword()), project.getId())));
			image = ImageIO.read(url);

		} catch (ClientException e)
		{
			System.out.println("Error while getting sample image");
			e.printStackTrace();
		} catch (MalformedURLException e)
		{
			System.out.println("Error while getting sample image");
			e.printStackTrace();
		} catch (IOException e)
		{
			System.out.println("Error while getting sample image");
			e.printStackTrace();
		}
		
		ImageIcon imgIcon = new ImageIcon(image.getScaledInstance(getWidth(),
				(int) (getHeight() * .9), Image.SCALE_DEFAULT));

		panel.add(new JLabel(imgIcon), BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");

		okButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
			}

		});

		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(okButton);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		add(panel);
	}

}