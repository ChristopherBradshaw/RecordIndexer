package client.bottompanel.right;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import client.communication.ClientCommunicator;
import shared.communication.DownloadBatchOutput;

@SuppressWarnings("serial")
public class RightBottomPanel extends JTabbedPane
{
	private HelpHTML[] helphtmls;
	private JScrollPane scroll;
	private DownloadBatchOutput params;
	
	private int oldCol;
	public RightBottomPanel(int xSize, int ySize, DownloadBatchOutput params)
	{
		super();
		setPreferredSize(new Dimension((int) (xSize * .78), (int) (ySize * .44)));
		setBackground(Color.GRAY);
		
		if(params == null)
			return;
		
		this.params = params;
		helphtmls = new HelpHTML[params.getFields().size()];
		String urlpath = ClientCommunicator.getURL() + File.separator;
		for(int i = 0; i < params.getFields().size(); ++i)
		{
			helphtmls[i]  = new HelpHTML(urlpath + params.getFields().get(i).getHelpHtml());
		}
		oldCol = 0;
		scroll = new JScrollPane(helphtmls[0]);
		add(scroll,"Help HTML");
		
		
	}
	
	public void setField(int field)
	{	
		if(field == oldCol)
			return;
		
		this.remove(scroll);
		oldCol = field;
		scroll = new JScrollPane(helphtmls[field]);
		this.add(scroll,"Help HTML");

	}
}
