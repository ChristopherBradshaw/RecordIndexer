package server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import shared.model.IndexedData;

public class IndexedDataDAO
{
	private static Logger logger;
	
	static 
	{
		logger = Logger.getLogger("recordindexer");
	}
	
	private Database db;
	
	/**
	 * @param db This DAO's database object
	 */
	public IndexedDataDAO(Database db)
	{
		this.db = db;
	}
	
	/**
	 * Add a new IndexedData to the database
	 * @param dataParams The IndexedData to be added
	 * @throws DatabaseException 
	 */
	public void add(IndexedData dataParams) throws DatabaseException
	{
		PreparedStatement stmt = null;
		ResultSet keyRS = null;
		try
		{
			String query = "insert into indexedValues (value, foreignBatchId, urlPath, recordNumber, foreignFieldId) "
					+ "values (?,?,?,?,?)";
			stmt = db.getConnection().prepareStatement(query);
			stmt.setString(1, dataParams.getTextInput());
			stmt.setInt(2, dataParams.getBatchID());
			stmt.setString(3, dataParams.getImageURL());
			stmt.setInt(4, dataParams.getRecordNumber());
			stmt.setInt(5, dataParams.getFieldID());
			
			if(stmt.executeUpdate() == 1)
			{
				Statement keyStmt = db.getConnection().createStatement();
				keyRS = keyStmt.executeQuery("select last_insert_rowid()");
				keyRS.next();
				int id = keyRS.getInt(1);
				dataParams.setId(id);
			}
			else
			{
				throw new DatabaseException("Could not insert indexed data");
			}
		}
		catch(SQLException e)
		{
			throw new DatabaseException("Could not insert indexed data",e);
		} 
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
		finally 
		{
			Database.safeClose(stmt);
			Database.safeClose(keyRS);
		}
	}
	
	/**
	 * Update an existing IndexedData in the database
	 * @param dataParams The IndexedData to be added
	 * @throws DatabaseException 
	 */
	public void update(IndexedData dataParams) throws DatabaseException
	{
		PreparedStatement stmt = null;
		ResultSet keyRS = null;
		try
		{
			String query = "update indexedValues set value = (?) where id = (?)";
			stmt = db.getConnection().prepareStatement(query);
			stmt.setString(1, dataParams.getTextInput());
			stmt.setInt(2, dataParams.getId());
			
			if(stmt.executeUpdate() != 1)
			{
				throw new DatabaseException("Could not update indexed data");
			}
		}
		catch(SQLException e)
		{
			throw new DatabaseException("Could not update indexed data",e);
		}
		finally
		{
			Database.safeClose(stmt);
			Database.safeClose(keyRS);
		}
	}	
	
	/**
	 * Connect to the server and get a <code>List</code> of <code>IndexedData</code> which match elements in <code>keywords</code>
	 * @param fieldId The <code>List</code> of ID of a <code>Field</code> to search in
	 * @param keywords A <code>List</code> of <code>Strings</code> to search for
	 * @return A <code>List</code> of <code>IndexedData</code> which match any of the <code>keywords</code>
	 * @throws DatabaseException 
	 */
	public List<IndexedData> search(List<Integer> fieldIds, List<String> keywords) throws DatabaseException
	{
		logger.entering("server.database.IndexedDataDAO", "search");

		ArrayList<IndexedData> result = new ArrayList<IndexedData>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			for(int i : fieldIds)
			{
				for(String s : keywords)
				{
					String query = "select * from indexedValues where foreignFieldId = (?) and value = (?) collate nocase";
					
					stmt = db.getConnection().prepareStatement(query);
					stmt.setInt(1, i);
					stmt.setString(2, s);
					
					rs = stmt.executeQuery();
					while(rs.next())
					{
						result.add(new IndexedData(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5), rs.getInt(6)));
					}
				}
			}
		}
		catch (SQLException e) 
		{
			DatabaseException serverEx = new DatabaseException(e.getMessage(), e);
			
			logger.throwing("server.database.IndexedDataDAO", "search", serverEx);
			
			throw serverEx;
		}		
		finally 
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}
		
		logger.exiting("server.database.IndexedDataDAO", "search");

		return result;
	}

	/**
	 * Check if exists indexed values for a batch
	 * @param batchId Id of Batch
	 * @return <code>true</code> if already indexed, <code>false</code> otherwise
	 * @throws DatabaseException
	 */
	public boolean isIndexed(int batchId) throws DatabaseException
	{
		logger.entering("server.database.IndexedDataDAO", "isIndexed");
		
		boolean result = true;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			String query = "select * from indexedValues where foreignBatchId = (?)";
			
			stmt = db.getConnection().prepareStatement(query);
			stmt.setInt(1, batchId);
			rs = stmt.executeQuery();
			
			if(!rs.isBeforeFirst())
			{
				result = false;
			}
		}
		catch (SQLException e) 
		{
			DatabaseException serverEx = new DatabaseException(e.getMessage(), e);
			
			logger.throwing("server.database.IndexedDataDAO", "isIndexed", serverEx);
			
			throw serverEx;
		}		
		finally 
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}

		logger.exiting("server.database.IndexedDataDAO", "isIndexed");
		
		return result;		
		
	}
}
