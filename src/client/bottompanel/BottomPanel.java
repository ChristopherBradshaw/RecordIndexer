package client.bottompanel;

import java.awt.Dimension;

import javax.swing.JSplitPane;

import shared.communication.DownloadBatchOutput;
import client.bottompanel.left.LeftBottomPanel;
import client.bottompanel.right.RightBottomPanel;
import client.frames.IndexerFrame;

@SuppressWarnings("serial")
public class BottomPanel extends JSplitPane
{
	LeftBottomPanel left;
	RightBottomPanel right;
	
	public BottomPanel(int xSize, int ySize, DownloadBatchOutput batchParams, IndexerFrame indexer)
	{
		super();
		setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		setPreferredSize(new Dimension(xSize, (int) (ySize * .43)));
		 	
		left = new LeftBottomPanel(xSize,ySize,batchParams,indexer);
		right = new RightBottomPanel(xSize,ySize,batchParams);
		
		setLeftComponent(left);
		setRightComponent(right);
	}
	
	public LeftBottomPanel getLeft()
	{
		return left;
		
	}
	
	public RightBottomPanel getRight()
	{
		return right;
	}
}
