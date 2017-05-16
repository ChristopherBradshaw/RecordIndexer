package shared.communication;

import java.io.File;
import java.util.List;

import client.communication.ClientCommunicator;
import shared.model.IndexedData;

public class SearchOutput
{
	private List<IndexedData> data;

	/**
	 * @param data Data results
	 */
	public SearchOutput(List<IndexedData> data)
	{
		this.data = data;
	}

	/**
	 * @return the data
	 */
	public List<IndexedData> getData()
	{
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<IndexedData> data)
	{
		this.data = data;
	}
	
	/**
	 * @return String representation of output
	 */
	public String toString()
	{
		StringBuilder ss = new StringBuilder();
		for(IndexedData d : data)
		{
			ss.append(d.getBatchID() + "\n");
			ss.append(ClientCommunicator.getURL() + File.separator + d.getImageURL() + "\n");
			ss.append(d.getRecordNumber() + "\n");
			ss.append(d.getFieldID() + "\n");
		}
		
		return ss.toString();
	}
	
	
}
