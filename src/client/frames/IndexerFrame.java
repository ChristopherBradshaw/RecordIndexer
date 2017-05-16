package client.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import shared.communication.DownloadBatchOutput;
import shared.communication.ValidateUserInput;
import shared.model.Field;
import shared.model.User;
import client.Client;
import client.Client.STATE;
import client.ClientException;
import client.bottompanel.BottomPanel;
import client.centerpanel.ImagePanel;
import client.facade.ClientFacade;
import client.util.DataManager;
import client.util.QualityChecker;
import client.util.Synchronizer;

@SuppressWarnings("serial")
public class IndexerFrame extends JFrame
{
	private static Client client;

	private Toolkit tk = Toolkit.getDefaultToolkit();
	private int xSize = ((int) tk.getScreenSize().getWidth());
	private int ySize = ((int) tk.getScreenSize().getHeight());

	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JButton invertButton;
	private JButton toggleHighlightButton;
	private JButton saveButton;
	private JButton submitButton;

	private JPanel topButtonPanel;
	private JSplitPane mainPanel;
	private ImagePanel centerPanel;
	private BottomPanel bottomPanel;
	
	private Synchronizer synchronizer;
	private DataManager dataManager;
	private QualityChecker spellCheck;
	
	public IndexerFrame()
	{
		setTitle("Indexer");
		setSize(xSize, ySize);
		setLayout(new BorderLayout());

		addMenuBar();
		addTopButtons();
		addMainPanel();
		
		this.addMouseWheelListener(new MouseWheelListener()
		{

			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int notches = e.getWheelRotation();
				if(notches < 0)
				{
					centerPanel.zoomIn();
					repaint();				
				}
				else if(notches > 0)
				{
					centerPanel.zoomOut();
					repaint();
				}
			}
		
		});
		synchronizer = new Synchronizer();
		dataManager = new DataManager();
		loadData();
	}

	public void reloadBottom(DownloadBatchOutput params)
	{
		bottomPanel = new BottomPanel(xSize, ySize, params,this);
		mainPanel.setRightComponent(bottomPanel);
		mainPanel.repaint();
		repaint();
	}
	
	public void reloadSynch()
	{
		synchronizer = new Synchronizer();
	}
	
	public void loadData()
	{
		try
		{
			dataManager.loadData(this);
			repaint();

		}
		catch(Exception e)
		{
			setTitle("Indexer");
			setSize(xSize, ySize);
			setLayout(new BorderLayout());

			addMenuBar();
			addTopButtons();
			addMainPanel();

			synchronizer = new Synchronizer();
			dataManager = new DataManager();
			

		}
	}
	public Synchronizer getSynchronizer()
	{
		return synchronizer;
	}

	public DataManager getDataManager()
	{
		return dataManager;
	}
	
	public QualityChecker getQualityChecker()
	{
		return spellCheck;
	}
	public JPanel getButtonPanel()
	{
		return topButtonPanel;
	}


	public void initSpellCheck(List<Field> fields)
	{
		spellCheck = new QualityChecker(fields);
	}
	
	private void addTopButtons()
	{
		topButtonPanel = new JPanel();

		zoomInButton = new JButton("Zoom in");
		zoomInButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				centerPanel.zoomIn();
				repaint();
			}
		});

		zoomOutButton = new JButton("Zoom out");
		zoomOutButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				centerPanel.zoomOut();
				repaint();
			}
		});

		invertButton = new JButton("Invert image");
		invertButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				centerPanel.invert();
				repaint();
			}
		});

		toggleHighlightButton = new JButton("Toggle Highlights");
		toggleHighlightButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				centerPanel.toggleHighlights();
				repaint();
			}
		});

		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{	
				Client.getIndexFrame().getDataManager().saveData();
			}
			
		});
		submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(centerPanel.hasAssignedBatch())
				{
					dataManager.submitData();
					dataManager.clearSerialFile();
				}
				else
				{
					ErrorFrame error = new ErrorFrame("Error: No batch currently assigned.");
					error.setVisible(true);
					
				}
			}
		});
		
		topButtonPanel.setLayout(new FlowLayout(5, 5, 5));
		topButtonPanel.add(zoomInButton);
		topButtonPanel.add(zoomOutButton);
		topButtonPanel.add(invertButton);
		topButtonPanel.add(toggleHighlightButton);
		topButtonPanel.add(saveButton);
		topButtonPanel.add(submitButton);

		add(topButtonPanel, BorderLayout.NORTH);
	}

	public static Client getClient()
	{
		return client;
	}

	public ImagePanel getImagePanel()
	{
		return centerPanel;
	}

	public JSplitPane getMainPanel()
	{
		return mainPanel;
	}

	public BottomPanel getBottomPanel()
	{
		return bottomPanel;
	}

	private void addMenuBar()
	{
		JMenuBar menubar = new JMenuBar();

		JMenu file = new JMenu("File");

		JMenuItem downloadBatchItem = new JMenuItem("Download Batch");
		downloadBatchItem.setToolTipText("Download a new batch");
		downloadBatchItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{

				User u = null;
				try
				{
					u = ClientFacade.validateUser(Client.getUsername(),
							Client.getPassword());
				} catch (ClientException e1)
				{
					e1.printStackTrace();
				}

				if (u == null)
					return;

				if (u.getCurrentBatchID() != -1)
				{
					ErrorFrame f = new ErrorFrame(
							"<html>Please finish your current batch<br>before downloading a new one.</html>");
					f.setVisible(true);
					return;
				}

				DownloadBatchDialog d = null;
				try
				{
					d = new DownloadBatchDialog(
							Client.getIndexFrame(),
							ClientFacade.getProjects(new ValidateUserInput(
									Client.getUsername(), Client.getPassword())));
				} catch (ClientException e)
				{
					e.printStackTrace();
				}

				if (d != null)
					d.setVisible(true);

				synchronizer.update(0, 0);

				repaint();

			}
		});

		file.add(downloadBatchItem);

		JMenuItem logoutItem = new JMenuItem("Logout");
		logoutItem.setToolTipText("Exit to login screen");
		logoutItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					Client.getIndexFrame().getDataManager().saveData();
				}
				catch(Exception e)
				{
					
				}				dispose();
				Client.setState(STATE.LOGIN);
			}
		});

		file.add(logoutItem);

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setToolTipText("Exit to desktop");
		exitItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					Client.getIndexFrame().getDataManager().saveData();
				}
				catch(Exception e)
				{
					
				}
				dispose();
				Client.setState(STATE.EXIT);
			}
		});

		file.add(exitItem);

		menubar.add(file);

		setJMenuBar(menubar);
	}

	public void addMainPanel()
	{
		createCenter();
		createBottom();

		mainPanel = new JSplitPane();
		mainPanel.setLayout(new FlowLayout(0, 0, 0));
		mainPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainPanel.setPreferredSize(new Dimension(xSize, (int) (ySize * .9)));
		mainPanel.setLeftComponent(centerPanel);
		mainPanel.setRightComponent(bottomPanel);

		add(mainPanel, BorderLayout.CENTER);

	}

	public void createCenter()
	{
		centerPanel = new ImagePanel(xSize, (int) (ySize * .45),this);
	}

	public void createBottom()
	{
		bottomPanel = new BottomPanel(xSize, ySize, null,this);
	}

	public void createBottom(DownloadBatchOutput batchData)
	{
		bottomPanel = new BottomPanel(xSize, ySize, batchData,this);
	}
	
	public void clearBatch()
	{
		dispose();
		JOptionPane.showMessageDialog(null,"Batch submitted successfully.");
		Client.setState(STATE.INDEXER_FRAME);
	}
}
