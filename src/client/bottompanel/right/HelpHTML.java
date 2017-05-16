package client.bottompanel.right;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class HelpHTML extends JEditorPane
{
	public HelpHTML(String path)
	{
		super();
		setEditable(false);
		try
		{
			setContentType("text/html");
			setPage(path);
		} catch (IOException e)
		{
			setContentType("text/html");
			setText("<html>Could not load</html>");
		}
	}
}
