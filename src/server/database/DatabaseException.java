package server.database;

@SuppressWarnings("serial")
public class DatabaseException extends Exception
{
	/**
	 * Create an instance of DatabaseException
	 */
	public DatabaseException()
	{
		return;
	}
	
	/**
	 * Create an instance of DatabaseException
	 * @param message String to be output
	 */
	public DatabaseException(String message)
	{
		super(message);
	}
	
	/**
	 * Create an instance of DatabaseException
	 * @param cause Throwable object to use in output
	 */
	public DatabaseException(Throwable cause)
	{
		super(cause);

	}
	
	/**
	 * Create an instance of DatabaseException
	 * @param message String to be output
	 * @param cause Throwable object to use in output
	 */
	public DatabaseException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
