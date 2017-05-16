package client.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import client.Client;

public class PopUpListener extends MouseAdapter
{
	 public void mousePressed(MouseEvent e)
	 {
	        if (e.isPopupTrigger())
	            doPop(e);
	    }

	    public void mouseReleased(MouseEvent e)
	    {
	        if (e.isPopupTrigger())
	            doPop(e);
	    }
//	    PopUpMenu menu = new PopUpMenu();
//        menu.show(e.getComponent(), e.getX(), e.getY());
        
	    private void doPop(MouseEvent e)
	    {

			int row = Client.getIndexFrame().getBottomPanel().getLeft().getForm().getSelectedRow();
			int col = Client.getIndexFrame().getBottomPanel().getLeft().getForm().getSelectedCol();
			String value = Client.getIndexFrame().getBottomPanel().getLeft().getForm().getCurrentValues().get(row).get(col);
			if(value.trim().length() == 0 || Client.getIndexFrame().getQualityChecker().isValidIndexerInput(value, Client.getIndexFrame().getImagePanel().getBatchData().getFields().get(col)))
			{
				return;
			}
			
	        PopUpMenu menu = new PopUpMenu();
	        menu.show(e.getComponent(), e.getX(), e.getY());
	        
	    }
}
