package client.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import shared.communication.DownloadBatchOutput;
import shared.communication.SubmitBatchInput;
import shared.communication.ValidateUserInput;
import shared.model.Batch;
import client.Client;
import client.ClientException;
import client.bottompanel.left.FormComponent;
import client.bottompanel.left.TableComponent;
import client.centerpanel.ImagePanel;
import client.facade.ClientFacade;
import client.frames.ErrorFrame;
import client.frames.IndexerFrame;

public class DataManager
{
	private TableComponent table;
	private FormComponent form;
	
	private static String LOG_FILE_NAME = "data" + File.separator + Client.getUsername() + "StateData.txt";
	private static String SERIAL_FILE_NAME = "data" + File.separator + Client.getUsername() + "Serial.txt";

	public DataManager()
	{
	}
	
	public void submitData()
	{
		table = Client.getIndexFrame().getBottomPanel().getLeft().getTable();
		form = Client.getIndexFrame().getBottomPanel().getLeft().getForm();
		
		Batch batchParam = ImagePanel.getBatchData().getBatch();
		ValidateUserInput validateParam = new ValidateUserInput(Client.getUsername(),Client.getPassword());
		ArrayList<ArrayList<String>> values = table.getValues();
		
		SubmitBatchInput params = new SubmitBatchInput(validateParam,batchParam.getId(),values);
		
		try
		{
			if(ClientFacade.submitBatch(params))
			{
				Client.getIndexFrame().clearBatch();
			}
			else
			{
				ErrorFrame error = new ErrorFrame("Error occured while submitting batch");
				error.setVisible(true);
			}
			
		}
		catch (ClientException e)
		{
			ErrorFrame error = new ErrorFrame("Error occured while submitting batch");
			error.setVisible(true);		
		}
	
	}
	
	public void clearSerialFile()
	{
		BufferedWriter writer = null;
		LOG_FILE_NAME = "data" + File.separator + Client.getUsername() + "StateData.txt";
		SERIAL_FILE_NAME = "data" + File.separator + Client.getUsername() + "Serial.txt";

		try
		{
			File logFile = new File(SERIAL_FILE_NAME);
			writer = new BufferedWriter(new FileWriter(logFile));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try
			{
				writer.close();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		
		try
		{
			writer.write("");
			writer.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void saveData()
	{
		ObjectOutputStream out = null;
		LOG_FILE_NAME = "data" + File.separator + Client.getUsername() + "StateData.txt";
		SERIAL_FILE_NAME = "data" + File.separator + Client.getUsername() + "Serial.txt";
		try
		{
			out = new ObjectOutputStream(new FileOutputStream(SERIAL_FILE_NAME));
		} catch (FileNotFoundException e2)
		{
			e2.printStackTrace();
		} catch (IOException e2)
		{
			e2.printStackTrace();
		}
		
		try
		{
			out.writeObject(Client.getIndexFrame().getImagePanel().getBatchData());
		} catch (IOException e2)
		{
			e2.printStackTrace();
		}
		
		
		try
		{
			out.close();
		} catch (IOException e2)
		{
			e2.printStackTrace();
		}
		
		
		BufferedWriter writer = null;

		try
		{
			File logFile = new File(LOG_FILE_NAME);
			writer = new BufferedWriter(new FileWriter(logFile));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try
			{
				writer.close();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		
		try
		{			
			writer.write(Client.getIndexFrame().getLocationOnScreen().x + "\n");
			writer.write(Client.getIndexFrame().getLocationOnScreen().y + "\n");
			writer.write(Client.getIndexFrame().getWidth() + "\n");
			writer.write(Client.getIndexFrame().getHeight() + "\n");
			writer.write(Client.getIndexFrame().getMainPanel().getDividerLocation() + "\n");
			writer.write(Client.getIndexFrame().getBottomPanel().getDividerLocation() + "\n");

			writer.write(Client.getIndexFrame().getImagePanel().getScale() + "\n");
			writer.write(Client.getIndexFrame().getImagePanel().getTranslateX() + "\n");
			writer.write(Client.getIndexFrame().getImagePanel().getTranslateY() + "\n");
			writer.write(Client.getIndexFrame().getImagePanel().getWidth() + "\n");
			writer.write(Client.getIndexFrame().getImagePanel().getHeight() + "\n");
			writer.write(Client.getIndexFrame().getImagePanel().hasHighlights() + "\n");
			writer.write(Client.getIndexFrame().getImagePanel().isInverted() + "\n");

			ArrayList<ArrayList<String>> values = Client.getIndexFrame().getBottomPanel().getLeft().getTable().getValues();
			for(int i = 0; i < values.size(); ++i)
			{
				for(int j = 0; j < values.get(i).size(); ++j)
				{
					if(values.get(i).get(j) == null)
						writer.write("\n");
					else
						writer.write(values.get(i).get(j) + "\n");
				}
			}


		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		try
		{
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadData(IndexerFrame indexerFrame)
	{
		LOG_FILE_NAME = "data" + File.separator + Client.getUsername() + "StateData.txt";
		SERIAL_FILE_NAME = "data" + File.separator + Client.getUsername() + "Serial.txt";
		Scanner in = null;
		try
		{
			in = new Scanner(new File(LOG_FILE_NAME));
		}
		catch(Exception e)
		{
			in.close();
			return;
		}
		
		DownloadBatchOutput out = null;
		try
		{
			ObjectInputStream objectIn = new ObjectInputStream(
			        new FileInputStream(SERIAL_FILE_NAME));
			
			out = (DownloadBatchOutput) objectIn.readObject();
			indexerFrame.getImagePanel().loadBatch(out);
			indexerFrame.reloadBottom(out);
			objectIn.close();
		} catch (Exception e)
		{
		}
		
		int frameX = in.nextInt();
		int frameY = in.nextInt();
		int frameWidth = in.nextInt();
		int frameHeight = in.nextInt();
		int mainDividerLocation = in.nextInt();
		int bottomDividerLocation = in.nextInt();
		
		double zoomLevel = in.nextDouble();
		int imageX = in.nextInt();
		int imageY = in.nextInt();
		int imageWidth = in.nextInt();
		int imageHeight = in.nextInt();
		boolean hasHighlights = in.nextBoolean();
		boolean isInverted = in.nextBoolean();
		
		
		indexerFrame.setBounds(frameX, frameY,frameWidth,frameHeight);
		indexerFrame.getMainPanel().setDividerLocation(mainDividerLocation);
		indexerFrame.getBottomPanel().setDividerLocation(bottomDividerLocation);
			
		indexerFrame.getImagePanel().setScale(zoomLevel);
		indexerFrame.getImagePanel().setTranslation(imageX, imageY);
		indexerFrame.getImagePanel().resize(imageWidth, imageHeight);
		indexerFrame.getImagePanel().setHighlights(hasHighlights);
		indexerFrame.getImagePanel().setInverted(isInverted);
		indexerFrame.getImagePanel().repaint();

		
		in.nextLine();
		
		TableComponent table = indexerFrame.getBottomPanel().getLeft().getTable();
		FormComponent form = indexerFrame.getBottomPanel().getLeft().getForm();
		for(int i = 0; i < out.getProject().getRecordsPerImage(); ++i)
		{
			for(int j = 0; j < out.getFields().size(); ++j)
			{
				String data = in.nextLine();
				if(data == null || data.trim().length() == 0)
					continue;
				
				table.setValueAt(data, i, j+1);
				form.set(i, j, data);
			}
		}
		
		in.close();
	}
}
