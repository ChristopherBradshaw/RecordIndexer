package client.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import client.Client;

public class PopUpMenu extends JPopupMenu
{
	JMenuItem item;
	
	public PopUpMenu()
	{
		super();
		item = new JMenuItem("See suggestions");
		item.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int row = Client.getIndexFrame().getBottomPanel().getLeft().getForm().getSelectedRow();
				int col = Client.getIndexFrame().getBottomPanel().getLeft().getForm().getSelectedCol();
				String value = Client.getIndexFrame().getBottomPanel().getLeft().getForm().getCurrentValues().get(row).get(col);
				if(Client.getIndexFrame().getQualityChecker().isValidIndexerInput(value, Client.getIndexFrame().getImagePanel().getBatchData().getFields().get(col)))
				{
					return;
				}
				
				SuggestionDialog dialog = new SuggestionDialog(value,Client.getIndexFrame().getImagePanel().getBatchData().getFields().get(col));
				dialog.setVisible(true);
			}
			
		});
		add(item);
	}
}
