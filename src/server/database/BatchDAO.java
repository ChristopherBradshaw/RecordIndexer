package server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import shared.model.Batch;
import shared.model.Field;

public class BatchDAO
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
	public BatchDAO(Database db)
	{
		this.db = db;
	}
	
	/**
	 * Add a new Batch to the database
	 * @param batchParams The Batch to be added
	 * @throws DatabaseException 
	 */
	public void add(Batch batchParams) throws DatabaseException
	{
		
		PreparedStatement stmt = null;
		ResultSet keyRS = null;
		try
		{
			String query = "insert into batch (filePath, foreignProjectId) values (?,?)";
			stmt = db.getConnection().prepareStatement(query);
			stmt.setString(1, batchParams.getFilePath());
			stmt.setInt(2, batchParams.getForeignProjectId());
			
			if(stmt.executeUpdate() == 1)
			{
				Statement keyStmt = db.getConnection().createStatement();
				keyRS = keyStmt.executeQuery("select last_insert_rowid()");
				keyRS.next();
				int id = keyRS.getInt(1);
				batchParams.setId(id);
			}
			else
			{
				throw new DatabaseException("Could not insert batch");
			}
		}
		catch(SQLException e)
		{
			throw new DatabaseException("Could not insert batch",e);
		}
		finally 
		{
			Database.safeClose(stmt);
			Database.safeClose(keyRS);
		}
	}
	
	/**
	 * Connect to the server and get a <code>Batch</code> associated with the <code>Project</code> that has the id <code>projectId</code>
	 * @param projectId The ID of a <code>Project</code> to get the <code>Batch</code> from
	 * @return A <code>Batch</code> to be downloaded
	 * @throws DatabaseException 
	 */
	public Batch getSampleBatch(int projectId) throws DatabaseException
	{	
		return getBatch(projectId,false);
	}

	/**
	 * Connect to the server and get a <code>Batch</code> associated with the <code>Project</code> that has the id <code>projectId</code>
	 * @param projectId The ID of a <code>Project</code> to get the <code>Batch</code> from
	 * @return A <code>Batch</code> to be downloaded
	 * @throws DatabaseException 
	 */
	public Batch downloadBatch(int projectId) throws DatabaseException
	{
		return getBatch(projectId,true);
	}
	
	private Batch getBatch(int projectId, boolean checkForAssignments) throws DatabaseException
	{
		logger.entering("server.database.BatchDAO", "getBatch");

		Batch returnBatch = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			String query = "select id, filePath, foreignProjectId from batch where foreignProjectId = (?)";
			
			stmt = db.getConnection().prepareStatement(query);
			stmt.setInt(1, projectId);
			
			rs = stmt.executeQuery();
			
			if(!rs.isBeforeFirst())
			{
				return null;	//Empty result set
			}
			
			if(checkForAssignments)	
			{
				while(this.db.getUserDAO().isAssignedToUser(rs.getInt(1)) || this.db.getIndexedDataDAO().isIndexed(rs.getInt(1)))		//Keep iterating while current batch is assigned or batch has already been indexed
				{
					if(!rs.next())
					{

						Database.safeClose(rs);
						Database.safeClose(stmt);
						return null;	//Did not find any unassigned batches
					}
				}
			}
			
			returnBatch = new Batch(rs.getInt(1),rs.getString(2),rs.getInt(3));
		}
		catch(SQLException e)
		{
			DatabaseException ex = new DatabaseException();
			logger.throwing("server.database.BatchDAO", "getBatch", ex);
			throw ex;
		}
		finally
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}
		
		logger.exiting("server.database.BatchDAO", "getBatch");
		return returnBatch;
	}
	
	/**
	 * 
	 * @param projectId ID representing Project to get Batches from
	 * @return List of all Batches in the Project associated with <code>projectID</code>
	 * @throws DatabaseException 
	 */
	public List<Batch> getBatchesInProject(int projectId) throws DatabaseException
	{
		logger.entering("server.database.BatchDAO", "getBatchesInProject");
		
		ArrayList<Batch> result = new ArrayList<Batch>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			String query = "select * from batch where foreignProjectId = (?)";
			
			stmt = db.getConnection().prepareStatement(query);

			stmt.setInt(1, projectId);
			
			rs = stmt.executeQuery();
			while (rs.next()) 
			{
				int tmpId = rs.getInt(1);
				String tmpFilePath = rs.getString(2);
				int tmpForeignProjectId = rs.getInt(3);
				result.add(new Batch(tmpId, tmpFilePath, tmpForeignProjectId));
				
			}
		}
		catch (SQLException e) 
		{
			DatabaseException serverEx = new DatabaseException(e.getMessage(), e);
			
			logger.throwing("server.database.BatchDAO", "getBatchesInProject", serverEx);
			
			throw serverEx;
		}		
		finally 
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}

		logger.exiting("server.database.BatchDAO", "getBatchesInProject");
		
		return result;		
		
	}
	
	/**
	 * 
	 * @param batchId Id of batch to search for
	 * @return The project Id of this batch
	 * @throws DatabaseException 
	 */
	public int getProjectId(int batchId) throws DatabaseException
	{
		logger.entering("server.database.BatchDAO", "getProjectId");
		
		int result = -1;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			String query = "select foreignProjectId from batch where id = (?)";
			
			stmt = db.getConnection().prepareStatement(query);

			stmt.setInt(1, batchId);
			
			rs = stmt.executeQuery();
			
			result = rs.getInt(1);
		}
		catch (SQLException e) 
		{
			DatabaseException serverEx = new DatabaseException(e.getMessage(), e);
			
			logger.throwing("server.database.BatchDAO", "getBatchesInProject", serverEx);
			
			throw serverEx;
		}		
		finally 
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}

		logger.exiting("server.database.BatchDAO", "getBatchesInProject");
		
		return result;			
		
	}
	
	/**
	 * Get a specific batch
	 * @param id Id of batch
	 * @return A <code>Batch</code>
	 * @throws DatabaseException 
	 */
	public Batch getBatch(int id) throws DatabaseException
	{
		logger.entering("server.database.BatchDAO", "getBatch");
		
		Batch result = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			String query = "select * from batch where id = (?)";
			
			stmt = db.getConnection().prepareStatement(query);

			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(!rs.isBeforeFirst())
			{
				result = null;
			}
			else
			{
				result = new Batch(rs.getInt(1),rs.getString(2),rs.getInt(3));
			}
			
			
		}
		catch (SQLException e) 
		{
			DatabaseException serverEx = new DatabaseException(e.getMessage(), e);
			
			logger.throwing("server.database.BatchDAO", "getBatchesInProject", serverEx);
			
			throw serverEx;
		}		
		finally 
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}

		logger.exiting("server.database.BatchDAO", "getBatchesInProject");
		
		return result;			
	}
	
	
}
