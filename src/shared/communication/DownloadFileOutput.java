package shared.communication;

import java.io.File;

public class DownloadFileOutput
{
	private File file;

	/**
	 * @param file File result
	 */
	public DownloadFileOutput(File file)
	{
		this.file = file;
	}

	/**
	 * @return the file
	 */
	public File getF()
	{
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setF(File f)
	{
		this.file = file;
	}
	
	
}
