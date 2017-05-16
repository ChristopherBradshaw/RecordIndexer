package server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import shared.model.Batch;
import shared.model.Project;

public class ProjectDAO
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
	public ProjectDAO(Database db)
	{
		this.db = db;
	}
	
	/**
	 * Add a new Project to the database
	 * @param projectParams The Project to be added
	 * @throws DatabaseException
	 */
	public void add(Project projectParams) throws DatabaseException
	{
		PreparedStatement stmt = null;
		ResultSet keyRS = null;
		try
		{
			String query = "insert into project (title, recordsPerImage, firstYCoord, recordHeight)"
					+ " values (?,?,?,?)";
			
			stmt = db.getConnection().prepareStatement(query);
			stmt.setString(1, projectParams.getTitle());
			stmt.setInt(2, projectParams.getRecordsPerImage());
			stmt.setInt(3, projectParams.getFirstYCoord());
			stmt.setInt(4, projectParams.getRecordHeight());
			
			if(stmt.executeUpdate() == 1)
			{
				Statement keyStmt = db.getConnection().createStatement();
				keyRS = keyStmt.executeQuery("select last_insert_rowid()");
				keyRS.next();
				int id = keyRS.getInt(1);
				projectParams.setId(id);
			}
			else
			{
				throw new DatabaseException("Could not insert project");
			}
		}
		catch(SQLException e)
		{
			throw new DatabaseException("Could not insert project",e);
		}
		finally 
		{
			Database.safeClose(stmt);
			Database.safeClose(keyRS);
		}
	}
	
	/**
	 * 
	 * @return A list of all Projects in this database
	 * @throws DatabaseException 
	 */
	public List<Project> getAll() throws DatabaseException
	{
		logger.entering("server.database.ProjectDAO", "getAll");
		
		ArrayList<Project> result = new ArrayList<Project>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			String query = "select * from project";
			
			stmt = db.getConnection().prepareStatement(query);
			
			rs = stmt.executeQuery();
			while (rs.next()) 
			{
				int tmpId = rs.getInt(1);
				String tmpTitle = rs.getString(2);
				int tmpRecordsPerImage = rs.getInt(3);
				int tmpFirstYCoord = rs.getInt(4);
				int tmpRecordHeight = rs.getInt(5);
				
				result.add(new Project(tmpId, tmpTitle, tmpRecordsPerImage, tmpFirstYCoord, tmpRecordHeight));
			}
		}
		catch (SQLException e) 
		{
			DatabaseException serverEx = new DatabaseException(e.getMessage(), e);
			
			logger.throwing("server.database.ProjectDAO", "getAll", serverEx);
			
			throw serverEx;
		}		
		finally 
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}

		logger.exiting("server.database.ProjectDAO", "getAll");
		
		return result;		
		
	}
	
	/**
	 * Get a <code>Project</code> from the database
	 * @param id ProjectId to get
	 * @return A <code>Project</code> object
	 * @throws DatabaseException 
	 */
	public Project getProject(int id) throws DatabaseException
	{
		logger.entering("server.database.ProjectDAO", "getProject");
		

		Project result = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			String query = "select * from project where id = (?)";
			
			stmt = db.getConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(!rs.isBeforeFirst())
			{
				result = null;
			}
			else
			{
				result = new Project(rs.getInt(1),rs.getString(2),rs.getInt(3),rs.getInt(4),rs.getInt(5));
			}
		
		}
		catch (SQLException e) 
		{
			DatabaseException serverEx = new DatabaseException(e.getMessage(), e);
			
			logger.throwing("server.database.ProjectDAO", "getProject", serverEx);
			
			throw serverEx;
		}		
		finally 
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}

		logger.exiting("server.database.ProjectDAO", "getProject");
		
		return result;	
	}
}
