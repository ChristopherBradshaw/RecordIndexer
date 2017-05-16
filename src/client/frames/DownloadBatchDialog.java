package client.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import shared.communication.DownloadBatchOutput;
import shared.communication.GetBatchInput;
import shared.communication.ValidateUserInput;
import shared.model.Project;
import client.Client;
import client.ClientException;
import client.facade.ClientFacade;

@SuppressWarnings("serial")
public class DownloadBatchDialog extends JDialog
{
	public DownloadBatchDialog(final IndexerFrame parentFrame,
			final List<Project> projectList)
	{
		setModal(true);
		setTitle("Download Batch");
		setResizable(false);
		setSize(300, 125);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.add(new JLabel("Project:"));
		final JComboBox<String> projectMenu = new JComboBox<String>();
		for (Project p : projectList)
		{
			projectMenu.addItem(p.getTitle());
		}

		panel.add(projectMenu);

		JButton sample = new JButton("Sample");
		JButton cancel = new JButton("Cancel");
		JButton download = new JButton("Download");

		sample.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				Project tmpProject = null;
				for (Project p : projectList)
				{
					if (p.getTitle().equals(projectMenu.getSelectedItem()))
					{
						tmpProject = p;
						break;
					}
				}

				SampleBatchDialog d = new SampleBatchDialog(tmpProject);
				d.setVisible(true);
				repaint();
			}
		});

		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
			}

		});

		download.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String selected = (String) projectMenu.getSelectedItem();
				Project tmpProject = null;
				for (Project p : projectList)
				{
					if (p.getTitle().equals(selected))
					{
						tmpProject = p;
						break;
					}
				}

				DownloadBatchOutput batchData = null;
				try
				{
					batchData = ClientFacade.getBatch(new GetBatchInput(
							new ValidateUserInput(Client.getUsername(), Client
									.getPassword()), tmpProject.getId()));

					parentFrame.getImagePanel().loadBatch(batchData);
					parentFrame.repaint();
				} catch (ClientException e)
				{
					System.out.println("Could not load batch");
					e.printStackTrace();
				}

				parentFrame.createBottom(batchData);
				parentFrame.getMainPanel().setRightComponent(
						parentFrame.getBottomPanel());
				parentFrame.add(parentFrame.getMainPanel());
				parentFrame.repaint();
				dispose();
			}

		});

		panel.add(sample);
		panel.add(cancel);
		panel.add(download);

		add(panel);
	}
}