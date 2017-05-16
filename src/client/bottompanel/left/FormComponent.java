package client.bottompanel.left;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import shared.communication.DownloadBatchOutput;
import shared.model.Field;
import shared.model.Project;
import client.Client;
import client.frames.IndexerFrame;
import client.util.PopUpListener;

@SuppressWarnings("serial")
public class FormComponent extends JPanel
{
	private JList<String> list;
	private JPanel form;
	private DownloadBatchOutput batchData;

	private int selectedCol;
	private int selectedRow;
	private ArrayList<ArrayList<String>> currentValues;
	private RowPanel[] rowPanels;

	private IndexerFrame indexer;
	
	public FormComponent(final DownloadBatchOutput batchData, IndexerFrame indexer)
	{
		super();
		this.batchData = batchData;
		this.indexer = indexer;
		setLayout(new BorderLayout(0, 0));

		list = new JList<String>();
		add(list, BorderLayout.WEST);

		form = new JPanel();
		add(form, BorderLayout.CENTER);

		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

		loadBatchData(this.batchData);
	}

	public ArrayList<ArrayList<String>> getCurrentValues()
	{
		return currentValues;
	}

	public int getSelectedRow()
	{
		return selectedRow;
	}

	public int getSelectedCol()
	{
		return selectedCol;
	}

	public void setRow(int row)
	{
		selectedRow = row;
		list.setSelectedIndex(row);
		loadNewRow(selectedRow);
	}

	public void setCol(int col)
	{
		selectedCol = col;
		rowPanels[col].getTextField().requestFocusInWindow();
	}

	public void set(int row, int col, String data)
	{
		this.currentValues.get(row).set(col, data);
		loadNewRow(row);
	}

	private void loadNewRow(int row)
	{

		for (int i = 0; i < rowPanels.length; ++i)
		{
			
			rowPanels[i].getTextField().setText(currentValues.get(row).get(i));
			rowPanels[i].getTextField().revalidate();
			rowPanels[i].getTextField().repaint();
			rowPanels[i].updateTextField(i);
//			rowPanels[i].getTextField().addMouseListener(new PopUpListener());
			
		}
		
		revalidate();
		repaint();

	}

	private void loadBatchData(final DownloadBatchOutput batchData)
	{
		if (batchData == null)
			return;

		this.batchData = batchData;
		this.currentValues = new ArrayList<ArrayList<String>>();
		Project project = batchData.getProject();
		List<Field> fieldList = batchData.getFields();

		DefaultListModel<String> dlm = new DefaultListModel<String>();

		for (int i = 0; i < project.getRecordsPerImage(); ++i)
		{
			dlm.addElement((i + 1) + "                       ");
			currentValues.add(new ArrayList<String>());
			for (int j = 0; j < fieldList.size(); ++j)
			{
				currentValues.get(i).add("");
			}

		}

		list.setModel(dlm);
		list.setSelectedIndex(0);
		list.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting())
					return;

				if (selectedRow == list.getSelectedIndex())
					return;

				selectedRow = list.getSelectedIndex();
				
				Client.getIndexFrame().getSynchronizer().updateFromForm(selectedRow, selectedCol);
				loadNewRow(selectedRow);
				selectedCol = 0;
				rowPanels[0].getTextField().requestFocusInWindow();


			}
		});

		rowPanels = new RowPanel[fieldList.size()];
		int col = 0;

		for (Field field : fieldList)
		{
			rowPanels[col] = new RowPanel(col, field,indexer);

			if (selectedCol == col)
				rowPanels[col].getTextField().requestFocusInWindow();

			form.add(rowPanels[col]);

			++col;
		}
	}

}

@SuppressWarnings("serial")
class RowPanel extends JPanel
{
	private JTextField textField;
	private IndexerFrame indexer;
	public RowPanel(int col, Field field, IndexerFrame indexer)
	{
		setSize(getWidth(), 10);

		JLabel label = new JLabel(field.getTitle());
		label.setSize(120, 20);
		add(label);

		this.indexer = indexer;
		textField = new JTextField();

		textField.setColumns(10);

		final int closeCol = col;

		textField.addFocusListener(new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				Client.getIndexFrame()
						.getSynchronizer()
						.updateFromForm(
								Client.getIndexFrame().getBottomPanel()
										.getLeft().getForm().getSelectedRow(),
								closeCol);
			}

			@Override
			public void focusLost(FocusEvent arg0)
			{
				// TODO Auto-generated method stub
			}

		});

		textField.addKeyListener(new KeyListener()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				String enteredText = ((JTextField) e.getSource()).getText();
				Client.getIndexFrame()
						.getSynchronizer()
						.updateFromFormText(
								Client.getIndexFrame().getBottomPanel()
										.getLeft().getForm().getSelectedRow(),
								closeCol, enteredText);
				Client.getIndexFrame()
						.getBottomPanel()
						.getLeft()
						.getForm()
						.getCurrentValues()
						.get(Client.getIndexFrame().getBottomPanel().getLeft()
								.getForm().getSelectedRow())
						.set(closeCol, enteredText);
				
				updateTextField(closeCol);
			}

			@Override
			public void keyTyped(KeyEvent e)
			{
				// TODO Auto-generated method stub
			}

		});

		textField.addMouseListener(new PopUpListener());
		textField.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				textField.requestFocusInWindow();
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				textField.requestFocusInWindow();
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}
		});

		add(textField);
	}
	

	public void updateTextField(int closeCol)
	{
		String enteredText = textField.getText();
		if(enteredText.trim().length() == 0)
		{
			textField.setBackground(Color.WHITE);
		}
		else if(!indexer.getQualityChecker().isValidIndexerInput(enteredText, indexer.getImagePanel().getBatchData().getFields().get(closeCol)))
		{
			textField.setBackground(Color.RED);
		}
		else
		{
			textField.setBackground(Color.WHITE);
		}
	}

	public JTextField getTextField()
	{
		return textField;
	}
	
}