package shared.model;

import java.io.Serializable;

public class Batch implements Serializable
{
	private int id;
	private String filePath;
	private int foreignProjectId;
	
	/**
	 * @param id The Batch's unique ID
	 * @param filePath Relative path to this Batch's image file
	 * @param foreignProjectId The ID of this Batch's Project
	 */
	public Batch(int id, String filePath, int foreignProjectId)
	{
		this.id = id;
		this.filePath = filePath;
		this.foreignProjectId = foreignProjectId;
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
	 * @return the filePath
	 */
	public String getFilePath()
	{
		return filePath;
	}
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	
	/**
	 * @return the foreignProjectId
	 */
	public int getForeignProjectId()
	{
		return foreignProjectId;
	}
	/**
	 * @param foreignProjectId the foreignProjectId to set
	 */
	public void setForeignProjectId(int foreignProjectId)
	{
		this.foreignProjectId = foreignProjectId;
	}
	
	
}
