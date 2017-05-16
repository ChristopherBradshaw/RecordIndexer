package server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import shared.model.Field;

public class FieldDAO
{
	
	private static Logger logger;
	
	static {
		logger = Logger.getLogger("recordindexer");
	}
	
	private Database db;

	/**
	 * @param db This DAO's database object
	 */
	public FieldDAO(Database db)
	{
		this.db = db;
	}
	
	/**
	 * Add a new Field to the database
	 * @param fieldParams The Field to be added
	 */
	public void add(Field fieldParams) throws DatabaseException
	{
		PreparedStatement stmt = null;
		ResultSet keyRS = null;
		try
		{
			String query = "insert into field "
					+ "(title, xCoord, width, helpHtml, knownData, foreignProjectId) "
					+ "values (?,?,?,?,?,?)";
			
			stmt = db.getConnection().prepareStatement(query);
			stmt.setString(1, fieldParams.getTitle());
			stmt.setInt(2,fieldParams.getxCoord());
			stmt.setInt(3, fieldParams.getWidth());
			stmt.setString(4, fieldParams.getHelpHtml());
			stmt.setString(5, fieldParams.getKnownData());
			stmt.setInt(6,fieldParams.getForeignProjectId());
			
			if(stmt.executeUpdate() == 1)
			{
				Statement keyStmt = db.getConnection().createStatement();
				keyRS = keyStmt.executeQuery("select last_insert_rowid()");
				keyRS.next();
				int id = keyRS.getInt(1);
				fieldParams.setId(id);
			}
			else
			{
				throw new DatabaseException("Could not insert field");
			}
		}
		catch(SQLException e)
		{
			throw new DatabaseException("Could not insert field",e);
		}
		finally 
		{
			Database.safeClose(stmt);
			Database.safeClose(keyRS);
		}
	}

	/**
	 * 
	 * @return A list of all Fields in this database
	 * @throws DatabaseException 
	 */
	public List<Field> getAll() throws DatabaseException
	{
		//-1 signifies all projects
		return getFieldsInProject(-1);	
	}
	
	/**
	 * Connect to the server and get a <code>List</code> of <code>Fields</code> from the <code>Project</code> that has the id <code>projectId</code>
	 * @param projectId The ID of a <code>Project</code> to get <code>Fields</code> from. -1 to represent every project
	 * @return A <code>List</code> of <code>Fields</code>
	 * @throws DatabaseException 
	 */
	public List<Field> getFieldsInProject(int projectId) throws DatabaseException
	{
		logger.entering("server.database.FieldDAO", "getFieldsInProject");
		
		ArrayList<Field> result = new ArrayList<Field>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			String query = "select * from field";
			if(projectId != -1)
				query += " where foreignProjectId = (?)";
			
			stmt = db.getConnection().prepareStatement(query);

			if(projectId != -1)
				stmt.setInt(1, projectId);
			
			rs = stmt.executeQuery();
			while (rs.next()) 
			{
				int tmpId = rs.getInt(1);
				String tmpTitle = rs.getString(2);
				int tmpXCoord = rs.getInt(3);
				int tmpWidth = rs.getInt(4);
				String tmpHelpHtml = rs.getString(5);
				String tmpKnownData = rs.getString(6);
				int tmpForeignProjectId = rs.getInt(7);
				result.add(new Field(tmpId, tmpTitle, tmpXCoord, tmpWidth, tmpHelpHtml, tmpKnownData, tmpForeignProjectId));
			}
		}
		catch (SQLException e) 
		{
			DatabaseException serverEx = new DatabaseException(e.getMessage(), e);
			
			logger.throwing("server.database.FieldDAO", "getFieldsInProject", serverEx);
			
			throw serverEx;
		}		
		finally 
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}

		logger.exiting("server.database.FieldDAO", "getFieldsInProject");
		
		return result;		
		
	}
	
}
