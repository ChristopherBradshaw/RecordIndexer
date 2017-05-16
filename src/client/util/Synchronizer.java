package client.util;

import java.util.ArrayList;

import client.Client;
import client.bottompanel.left.FormComponent;
import client.bottompanel.left.TableComponent;
import client.centerpanel.ImagePanel;

public class Synchronizer
{
	private ImagePanel image;
	private TableComponent table;
	private FormComponent form;
	
	private boolean modifyFromImage;
	public Synchronizer()
	{
		try
		{
			form = Client.getIndexFrame().getBottomPanel().getLeft().getForm();
			image = Client.getIndexFrame().getImagePanel();
			table = Client.getIndexFrame().getBottomPanel().getLeft().getTable();
		}
		catch(Exception e)
		{
			form = null;
			image = null;
			table = null;
		}
	}
	
	public void resyncForm()
	{
		form = Client.getIndexFrame().getBottomPanel().getLeft().getForm();
		image = Client.getIndexFrame().getImagePanel();
		table = Client.getIndexFrame().getBottomPanel().getLeft().getTable();
		
		ArrayList<ArrayList<String>> values = table.getValues();
	

		for(int i = 0; i < values.size(); ++i)
		{
			for(int j = 0; j < values.get(i).size(); ++j)
			{
				Object o = table.getValueAt(i, j+1);
				if(o == null)
					continue;
				
				form.set(i, j, o.toString()); 	
			}
		}
	}
	public void update(int row, int col)
	{
		if(row < 0)
			row = 0;
		if(col < 0)
			col = 0;
		
		image = Client.getIndexFrame().getImagePanel();
		table = Client.getIndexFrame().getBottomPanel().getLeft().getTable();
		form = Client.getIndexFrame().getBottomPanel().getLeft().getForm();
		
		image.setRow(row);
		image.setCol(col);
		image.repaint();
		
		table.setRow(row);
		table.setCol(col);
		table.repaint();
	}
	
	public boolean getModifyFromImage()
	{
		return modifyFromImage;
	}
	public void updateFromImage(int row, int col)
	{
		
		form = Client.getIndexFrame().getBottomPanel().getLeft().getForm();
		image = Client.getIndexFrame().getImagePanel();
		table = Client.getIndexFrame().getBottomPanel().getLeft().getTable();
		
		modifyFromImage = true;
		if(row < 0)
			row = 0;
		if(col < 0)
			col = 0;
		
		table.setRow(row);
		table.setCol(col);
		table.repaint();
		
		form.setRow(row);
		form.setCol(col);
		form.repaint();
		
		Client.getIndexFrame().getBottomPanel().getRight().setField(col);
		Client.getIndexFrame().getBottomPanel().getRight().repaint();

		modifyFromImage = false;
	}
	
	public void updateFromTable(int row, int col)
	{
		form = Client.getIndexFrame().getBottomPanel().getLeft().getForm();
		image = Client.getIndexFrame().getImagePanel();
		table = Client.getIndexFrame().getBottomPanel().getLeft().getTable();
		
		if(row < 0)
			row = 0;
		if(col < 0)
			col = 0;
		
		image.setRow(row);
		image.setCol(col);
		image.repaint();
		
		form.setRow(row);
		form.setCol(col);
		form.repaint();
		
		Client.getIndexFrame().getBottomPanel().getRight().setField(col);

	}
	
	public void updateFromFormText(int row, int col, String data)
	{
		form = Client.getIndexFrame().getBottomPanel().getLeft().getForm();
		image = Client.getIndexFrame().getImagePanel();
		table = Client.getIndexFrame().getBottomPanel().getLeft().getTable();
		
		table.setValueAt(data, row, col+1);
		table.repaint();

	}
	
	public void updateFromTableText(int row, int col, String data)
	{
		form = Client.getIndexFrame().getBottomPanel().getLeft().getForm();
		image = Client.getIndexFrame().getImagePanel();
		table = Client.getIndexFrame().getBottomPanel().getLeft().getTable();
		
		form = Client.getIndexFrame().getBottomPanel().getLeft().getForm();
		if(col == 0)
			return;
		form.set(row, col-1, data);
		form.repaint();
		
	}
	public void updateFromForm(int row, int col)
	{
		form = Client.getIndexFrame().getBottomPanel().getLeft().getForm();
		image = Client.getIndexFrame().getImagePanel();
		table = Client.getIndexFrame().getBottomPanel().getLeft().getTable();
		
		image.setRow(row);
		image.setCol(col);
		image.repaint();
		
		table.setRow(row);
		table.setCol(col);
		table.repaint();
		
		Client.getIndexFrame().getBottomPanel().getRight().setField(col);		
	}
	
//	public void validateCell(int row, int col)
//	{
//		String text = Client.getIndexFrame().getBottomPanel().getLeft().getTable().getValueAt(row, col+1).toString() + "";
//		boolean isValid = Client.getIndexFrame().getQualityChecker().isValidIndexerInput(text, Client.getIndexFrame().getImagePanel().getBatchData().getFields().get(col));
//		if(!isValid)
//		{
//			Client.getIndexFrame().getBottomPanel().getLeft().getForm
//		}
//	}
}
