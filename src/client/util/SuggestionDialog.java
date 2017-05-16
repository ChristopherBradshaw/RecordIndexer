package client.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import shared.model.Field;
import client.Client;

@SuppressWarnings("serial")
public class SuggestionDialog extends JDialog
{
	public SuggestionDialog(String enteredStr, Field f)
	{
		setTitle("Suggestions");
		setSize(250,300);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setResizable(false);
		setModal(true);
		TreeSet<String> suggestions = Client.getIndexFrame().getQualityChecker().getSuggestions(enteredStr, f);
		DefaultListModel<String> model = new DefaultListModel<String>();
		final JList<String> menu = new JList<String>(model);
		menu.setFixedCellWidth(150);
		
		DefaultListCellRenderer renderer =  (DefaultListCellRenderer) menu.getCellRenderer();  
		renderer.setHorizontalAlignment(JLabel.CENTER);  
		menu.setBackground(Color.LIGHT_GRAY);
		for(String s : suggestions)
		{
			model.addElement(s);
		}
		
		JPanel modelPane = new JPanel();
		modelPane.add(menu);
		JScrollPane pane = new JScrollPane(modelPane);

		JPanel buttonPane = new JPanel();
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String str = menu.getSelectedValue();
				if(str == null || str.trim().length() == 0)
					return;
				
				int row = Client.getIndexFrame().getBottomPanel().getLeft().getTable().getSelectedRow();
				int col = Client.getIndexFrame().getBottomPanel().getLeft().getTable().getSelectedColumn();
				Client.getIndexFrame().getSynchronizer().updateFromTableText(row, col, str);
				Client.getIndexFrame().getBottomPanel().getLeft().getTable().setValueAt(str, row, col);
				dispose();
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
			}
		});
		buttonPane.add(cancelButton);
		buttonPane.add(okButton);
		
		add(buttonPane,BorderLayout.SOUTH);
		add(pane,BorderLayout.CENTER);
	}
}
