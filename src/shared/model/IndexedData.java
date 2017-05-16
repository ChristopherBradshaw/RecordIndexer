package shared.model;

import java.io.Serializable;

public class IndexedData implements Serializable
{
	private int id;
	private String textInput;
	private int batchID;
	private String imageURL;
	private int recordNumber;
	private int fieldID;

	/**
	 * @param id
	 * 			  The primary key
	 * @param textInput
	 * 			  The information entered in this cell
	 * @param batchID
	 *            The ID of the batch containing the match
	 * @param imageURL
	 *            The URL of the batch's image file on the server
	 * @param recordNumber
	 *            The row on the batch that contains the match
	 * @param fieldID
	 *            The ID of the field in the record that contains the match
	 */
	public IndexedData(int id, String textInput, String imageURL, int fieldID, int batchID, int recordNumber)
	{
		this.id = id;
		this.textInput = textInput;
		this.batchID = batchID;
		this.imageURL = imageURL;
		this.recordNumber = recordNumber;
		this.fieldID = fieldID;
	}

	
	
	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}



	/**
	 * @return the fieldID
	 */
	public int getFieldID()
	{
		return fieldID;
	}


	/**
	 * @param fieldID the fieldID to set
	 */
	public void setFieldID(int fieldID)
	{
		this.fieldID = fieldID;
	}


	/**
	 * @return the textInput 
	 */
	public String getTextInput()
	{
		return textInput;
	}

	/**
	 * @param textInput the textInput to set
	 */
	public void setTextInput(String textInput)
	{
		this.textInput = textInput;
	}

	/**
	 * @return the batchID
	 */
	public int getBatchID()
	{
		return batchID;
	}

	/**
	 * @param batchID
	 *            the batchID to set
	 */
	public void setBatchID(int batchID)
	{
		this.batchID = batchID;
	}

	/**
	 * @return the imageURL
	 */
	public String getImageURL()
	{
		return imageURL;
	}

	/**
	 * @param imageURL
	 *            the imageURL to set
	 */
	public void setImageURL(String imageURL)
	{
		this.imageURL = imageURL;
	}

	/**
	 * @return the recordNumber
	 */
	public int getRecordNumber()
	{
		return recordNumber;
	}

	/**
	 * @param recordNumber
	 *            the recordNumber to set
	 */
	public void setRecordNumber(int recordNumber)
	{
		this.recordNumber = recordNumber;
	}
}
