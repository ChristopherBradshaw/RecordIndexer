package server.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class Database
{

	private static final String DATABASE_DIRECTORY = "database";
	private static final String DATABASE_FILE = "recordindexer.sqlite";
	private static final String DATABASE_URL = "jdbc:sqlite:"
			+ DATABASE_DIRECTORY + File.separator + DATABASE_FILE;

	private static Logger logger;
	
	static 
	{
		logger = Logger.getLogger("recordindexer");
	}

	/**
	 * Load database driver
	 * 
	 * @throws DatabaseException
	 *             if an error occurs
	 */
	public static void initialize() throws DatabaseException
	{
		try
		{
			final String driver = "org.sqlite.JDBC";
			Class.forName(driver);
		} 
		catch (ClassNotFoundException e)
		{
			throw new DatabaseException("Could not load database driver", e);
		}
	}

	private Connection connection;

	private BatchDAO batchDAO;
	private FieldDAO fieldDAO;
	private ProjectDAO projectDAO;
	private UserDAO userDAO;
	private IndexedDataDAO indexedDataDAO;

	/**
	 * Create a new instance of Database and initialize all DAO's
	 */
	public Database()
	{
		this.connection = null;
		this.batchDAO = new BatchDAO(this);
		this.fieldDAO = new FieldDAO(this);
		this.projectDAO = new ProjectDAO(this);
		this.userDAO = new UserDAO(this);
		this.indexedDataDAO = new IndexedDataDAO(this);
	}

	
	/**
	 * @return the connection
	 */
	public Connection getConnection()
	{
		return connection;
	}

	/**
	 * @return the batchDAO
	 */
	public BatchDAO getBatchDAO()
	{
		return batchDAO;
	}

	/**
	 * @return the fieldDAO
	 */
	public FieldDAO getFieldDAO()
	{
		return fieldDAO;
	}

	/**
	 * @return the projectDAO
	 */
	public ProjectDAO getProjectDAO()
	{
		return projectDAO;
	}

	/**
	 * @return the userDAO
	 */
	public UserDAO getUserDAO()
	{
		return userDAO;
	}

	
	/**
	 * @return the indexedDataDAO
	 */
	public IndexedDataDAO getIndexedDataDAO()
	{
		return indexedDataDAO;
	}


	/**
	 * Connect to the server and get a <code>File</code>
	 * 
	 * @param filePath
	 *            The path to the <code>File</code> to download
	 * @return A <code>File</code>
	 */
	public File getFile(String filePath)
	{
		return null;
	}

	/**
	 * Starts a new database transaction
	 * 
	 * @throws DatabaseException
	 *             If database connection is bad
	 */
	public void startTransaction() throws DatabaseException
	{	
		try
		{
			assert (this.connection == null);
			this.connection = DriverManager.getConnection(DATABASE_URL);
			this.connection.setAutoCommit(false);
		}
		catch (SQLException e)
		{
			throw new DatabaseException(
					"Could not connect to database. Make sure " + DATABASE_FILE
							+ " is available in ./" + DATABASE_DIRECTORY, e);
		}
	}

	/**
	 * Ends a current transaction if one exists
	 * 
	 * @param commit
	 *            <code>true</code> to commit changes, <code>false</code> to
	 *            rollback
	 */
	public void endTransaction(boolean commit)
	{
		if (this.connection == null)
			return;

		try
		{
			if (commit)
				this.connection.commit();
			else
				this.connection.rollback();
		} 
		catch (SQLException e)
		{
			System.out.println("Could not end transaction");
			this.connection = null;
		} 
		finally
		{
			safeClose(this.connection);
			this.connection = null;
		}
	}

	/**
	 * Clear a specified table in the database
	 * @param tableName Table to be cleared
	 * @throws DatabaseException
	 */
	public void clearTable(String tableName) throws DatabaseException
	{
		Statement stmt = null;
		ResultSet keyRS = null;
		
		try
		{
			String query = "delete from " + tableName;
			stmt = connection.createStatement();
			
			stmt.executeUpdate(query);
		}
		catch(SQLException e)
		{
			throw new DatabaseException("Could not delete " + tableName);
		}
		finally
		{
			safeClose(stmt);
			safeClose(keyRS);
		}
		
	}
	
	public static void safeClose(Connection conn)
	{
		if (conn != null)
		{
			try
			{
				conn.close();
			} 
			catch (SQLException e)
			{
				// ...
			}
		}
	}

	public static void safeClose(Statement stmt)
	{
		if (stmt != null)
		{
			try
			{
				stmt.close();
			} 
			catch (SQLException e)
			{
				// ...
			}
		}
	}

	public static void safeClose(PreparedStatement stmt)
	{
		if (stmt != null)
		{
			try
			{
				stmt.close();
			} 
			catch (SQLException e)
			{
				// ...
			}
		}
	}

	public static void safeClose(ResultSet rs)
	{
		if (rs != null)
		{
			try
			{
				rs.close();
			} 
			catch (SQLException e)
			{
				// ...
			}
		}
	}
	
	

}
