package shared.model;

import java.io.Serializable;

public class Field implements Serializable
{
	private int id;
	private String title;
	private int xCoord;
	private int width;
	private String helpHtml;
	private String knownData;
	private int foreignProjectId;
	
	/**
	 * @param id This Field's unique ID
	 * @param title This Field's column value
	 * @param xCoord This Field's starting x coordinate
	 * @param width This Field's width
	 * @param helpHtml A relative path to a HTML file containing help information
	 * @param knownData A relative path to a text file containing values for this field
	 * @param foreignProjectId The ID of this Field's Project
	 */
	public Field(int id, String title, int xCoord, int width, String helpHtml,
			String knownData, int foreignProjectId)
	{
		this.id = id;
		this.title = title;
		this.xCoord = xCoord;
		this.width = width;
		this.helpHtml = helpHtml;
		this.knownData = knownData;
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
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the xCoord
	 */
	public int getxCoord()
	{
		return xCoord;
	}

	/**
	 * @param xCoord the xCoord to set
	 */
	public void setxCoord(int xCoord)
	{
		this.xCoord = xCoord;
	}

	/**
	 * @return the width
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}

	/**
	 * @return the helpHtml
	 */
	public String getHelpHtml()
	{
		return helpHtml;
	}

	/**
	 * @param helpHtml the helpHtml to set
	 */
	public void setHelpHtml(String helpHtml)
	{
		this.helpHtml = helpHtml;
	}

	/**
	 * @return the knownData
	 */
	public String getKnownData()
	{
		return knownData;
	}

	/**
	 * @param knownData the knownData to set
	 */
	public void setKnownData(String knownData)
	{
		this.knownData = knownData;
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
