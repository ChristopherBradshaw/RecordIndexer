package shared.communication;

public class FilePathInput
{
	private String filePath;

	/**
	 * @param filePath String holding location to file
	 */
	public FilePathInput(String filePath)
	{
		this.filePath = filePath;
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
	
	
}
