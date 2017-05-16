package client.bottompanel.left;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import shared.communication.DownloadBatchOutput;
import client.frames.IndexerFrame;

public class LeftBottomPanel extends JTabbedPane
{
	
	private TableComponent table;
	private FormComponent form;
	
	public LeftBottomPanel(int xSize, int ySize, DownloadBatchOutput batchParams, IndexerFrame indexer)
	{
		super();
		
		setPreferredSize(new Dimension((int) (xSize * .2), (int) (ySize * .44)));
		try
		{
			if(batchParams != null)
				table = new TableComponent(batchParams);
			else
				table = new TableComponent();
			
			add(new JScrollPane(table),"Table Entry");

		}
		catch(Exception e)
		{
			
		}
		
		form  = new FormComponent(batchParams,indexer);
		add(form,"Form Entry");	

		
	}
	
	public TableComponent getTable()
	{
		return table;
	}
	
	public FormComponent getForm()
	{
		return form;
	}
}
