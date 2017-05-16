package client.bottompanel.left;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import shared.communication.DownloadBatchOutput;
import client.Client;
import client.util.PopUpMenu;
import client.util.SuggestionDialog;

@SuppressWarnings("serial")
public class TableComponent extends JTable
{
	private String[] columnNames;
	private int numRecords;

	public TableComponent()
	{
		super();
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		numRecords = 0;
		columnNames = null;
		repaint();
	}

	public TableComponent(DownloadBatchOutput data)
	{
		super(data.getProject().getRecordsPerImage(),
				data.getFields().size() + 1);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setDefaultRenderer(Object.class, new MyRenderer());

		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("See suggestions");
		menuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SuggestionDialog suggestions = new SuggestionDialog(getValueAt(
						getSelectedRow(), getSelectedColumn()).toString(),
						Client.getIndexFrame().getImagePanel().getBatchData()
								.getFields().get(getSelectedColumn() - 1));
				suggestions.setVisible(true);
			}
		});
		popup.add(menuItem);
		setComponentPopupMenu(popup);

		numRecords = data.getProject().getRecordsPerImage();
		columnNames = new String[data.getFields().size() + 1];
		columnNames[0] = "Record";
		for (int i = 0; i < data.getFields().size(); ++i)
		{
			columnNames[i + 1] = data.getFields().get(i).getTitle();
		}

		for (int i = 0; i < columnNames.length; ++i)
		{
			this.getColumnModel().getColumn(i).setHeaderValue(columnNames[i]);
		}

		for (int i = 0; i < numRecords; ++i)
		{
			this.setValueAt((i + 1), i, 0);
		}

		addMouseListener(new java.awt.event.MouseAdapter()
		{
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt)
			{

				Client.getIndexFrame().getSynchronizer().resyncForm();
				int row = rowAtPoint(evt.getPoint());
				int col = columnAtPoint(evt.getPoint());

				if (row >= 0 && col >= 1)
				{
					Client.getIndexFrame()
							.getSynchronizer()
							.updateFromTable(getSelectedRow(),
									getSelectedColumn() - 1);

				}
			}
		});

		ListSelectionModel colSM = getColumnModel().getSelectionModel();
		colSM.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting())
					return;

				if (!((ListSelectionModel) e.getSource()).isSelectionEmpty()
						&& !Client.getIndexFrame().getSynchronizer()
								.getModifyFromImage())
				{
					int row = getSelectedRow();
					int col = getSelectedColumn();

					if (col == 0)
					{
						col = columnNames.length - 1;
						Client.getIndexFrame().getSynchronizer()
								.updateFromTable(row, col - 1);
						Object data = Client.getIndexFrame().getBottomPanel()
								.getLeft().getTable().getValueAt(row, col);
						if (data instanceof Integer)
							Client.getIndexFrame()
									.getSynchronizer()
									.updateFromTableText(row, col,
											(String) (data.toString() + ""));
						else
							Client.getIndexFrame()
									.getSynchronizer()
									.updateFromTableText(row, col,
											(String) (data));

					} else
					{
						if (row >= 0 && col >= 1)
						{
							Client.getIndexFrame().getSynchronizer()
									.updateFromTable(row, col - 1);

							Object data = Client.getIndexFrame()
									.getBottomPanel().getLeft().getTable()
									.getValueAt(row, col - 1);
							if (data instanceof Integer)
								Client.getIndexFrame()
										.getSynchronizer()
										.updateFromTableText(row, col - 1,
												(String) (data.toString() + ""));
							else
								Client.getIndexFrame()
										.getSynchronizer()
										.updateFromTableText(row, col - 1,
												(String) (data));

						}
					}
				}

			}

		});

		getSelectionModel().addListSelectionListener(
				new ListSelectionListener()
				{
					public void valueChanged(ListSelectionEvent e)
					{

						if (e.getValueIsAdjusting())
							return;

						if (!((ListSelectionModel) e.getSource())
								.isSelectionEmpty()
								&& !Client.getIndexFrame().getSynchronizer()
										.getModifyFromImage())
						{
							int row = getSelectedRow();
							int col = getSelectedColumn();

							if (col == 0)
							{
								col = columnNames.length - 1;
								Client.getIndexFrame().getSynchronizer()
										.updateFromTable(row, col - 1);
								Object data = Client.getIndexFrame()
										.getBottomPanel().getLeft().getTable()
										.getValueAt(row, col);
								if (data instanceof Integer)
									Client.getIndexFrame()
											.getSynchronizer()
											.updateFromTableText(
													row,
													col,
													(String) (data.toString() + ""));
								else
									Client.getIndexFrame()
											.getSynchronizer()
											.updateFromTableText(row, col,
													(String) (data));

							} else
							{
								if (row >= 0 && col >= 1)
								{

									Client.getIndexFrame().getSynchronizer()
											.updateFromTable(row, col - 1);

									Object data = Client.getIndexFrame()
											.getBottomPanel().getLeft()
											.getTable()
											.getValueAt(row, col - 1);
									if (data instanceof Integer)
										Client.getIndexFrame()
												.getSynchronizer()
												.updateFromTableText(
														row,
														col - 1,
														(String) (data
																.toString() + ""));
									else
										Client.getIndexFrame()
												.getSynchronizer()
												.updateFromTableText(row,
														col - 1,
														(String) (data));

								}
							}
						}
					}
				});

		repaint();
	}

	public ArrayList<ArrayList<String>> getValues()
	{
		ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();

		DefaultTableModel dtm = (DefaultTableModel) getModel();
		int nRow = dtm.getRowCount();
		int nCol = dtm.getColumnCount();

		for (int i = 0; i < nRow; ++i)
		{
			values.add(new ArrayList<String>());
			for (int j = 1; j < nCol; ++j)
			{
				Object o = dtm.getValueAt(i, j);
				if (o instanceof Integer)
					values.get(i).add(((Integer) o + ""));
				else
					values.get(i).add(((String) o));
			}
		}

		return values;
	}

	public void setRow(int row)
	{
		this.setRowSelectionInterval(row, row);
	}

	public void setCol(int col)
	{
		this.addColumnSelectionInterval(col + 1, col + 1);
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return col != 0;
	}

	@Override
	public int getRowCount()
	{
		return numRecords;
	}

	@Override
	public String getColumnName(int index)
	{
		return columnNames[index];
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		if (this.getSelectedColumn() == 0)
			this.addColumnSelectionInterval(1, 1);

		super.paintComponent(g);
	}

}

@SuppressWarnings("serial")
class MyRenderer extends DefaultTableCellRenderer
{
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col)
	{

		// Cells are by default rendered as a JLabel.
		JLabel editor = (JLabel) super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, col);

		if (value != null)
		{
			editor.setText(value.toString());
		}

		if (value == null || col == 0 || value.toString().trim().length() == 0)
		{
			editor.setBackground(Color.WHITE);
		} else
		{
			if (!Client
					.getIndexFrame()
					.getQualityChecker()
					.isValidIndexerInput(
							value.toString(),
							Client.getIndexFrame().getImagePanel()
									.getBatchData().getFields().get(col - 1)))
			{
				editor.setBackground(Color.RED);
			} else
			{
				editor.setBackground(Color.WHITE);
			}

		}

		// Return the JLabel which renders the cell.
		return editor;
	}
}
