package shared.model;

import java.io.Serializable;

public class Project implements Serializable
{
	private int id;
	private String title;
	private int recordsPerImage;
	private int firstYCoord;
	private int recordHeight;
	
	/**
	 * Creates a new instance of <code>Project</code> 
	 * @param id The Project's unique ID
	 * @param title The Project's name
	 * @param recordsPerImage Number of records in each batch in this Project
	 * @param firstYCoord The y-coordinate of the top of the first record in the the Project's batches
	 * @param recordHeight The height of each record in the project's images, measured in pixels
	 */
	public Project(int id, String title, int recordsPerImage, int firstYCoord,
			int recordHeight)
	{
		this.id = id;
		this.title = title;
		this.recordsPerImage = recordsPerImage;
		this.firstYCoord = firstYCoord;
		this.recordHeight = recordHeight;
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
	 * @return the title.
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
	 * @return the recordsPerImage
	 */
	public int getRecordsPerImage()
	{
		return recordsPerImage;
	}
	/**
	 * @param recordsPerImage the recordsPerImage to set
	 */
	public void setRecordsPerImage(int recordsPerImage)
	{
		this.recordsPerImage = recordsPerImage;
	}
	/**
	 * @return the firstYCoord
	 */
	public int getFirstYCoord()
	{
		return firstYCoord;
	}
	/**
	 * @param firstYCoord the firstYCoord to set
	 */
	public void setFirstYCoord(int firstYCoord)
	{
		this.firstYCoord = firstYCoord;
	}
	/**
	 * @return the recordHeight
	 */
	public int getRecordHeight()
	{
		return recordHeight;
	}
	/**
	 * @param recordHeight the recordHeight to set
	 */
	public void setRecordHeight(int recordHeight)
	{
		this.recordHeight = recordHeight;
	}
	
	
	
	
}
