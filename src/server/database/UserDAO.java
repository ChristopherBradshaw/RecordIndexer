package server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import shared.model.User;

public class UserDAO
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
	public UserDAO(Database db)
	{
		this.db = db;
	}
	
	/**
	 * Add a new User to the database
	 * @param userParams The User to be added
	 * @throws DatabaseException 
	 */
	public void add(User userParams) throws DatabaseException
	{
		PreparedStatement stmt = null;
		ResultSet keyRS = null;
		try
		{
			String query = "insert into user (username, password, fName, lName, email, completedBatches, currentBatch) values"
					+ "(?,?,?,?,?,?,?)";
			stmt = db.getConnection().prepareStatement(query);
			stmt.setString(1, userParams.getUsername());
			stmt.setString(2, userParams.getPassword());
			stmt.setString(3, userParams.getFirstName());
			stmt.setString(4, userParams.getLastName());
			stmt.setString(5, userParams.getEmail());
			stmt.setInt(6, userParams.getCompletedBatches());
			stmt.setInt(7, -1);
			
			if(stmt.executeUpdate() == 1)
			{
				Statement keyStmt = db.getConnection().createStatement();
				keyRS = keyStmt.executeQuery("select last_insert_rowid()");
				keyRS.next();
				int id = keyRS.getInt(1);
				userParams.setId(id);
			}
			else
			{
				throw new DatabaseException("Could not insert user");
			}
		}
		catch(SQLException e)
		{
			throw new DatabaseException("Could not insert user",e);
		}
		finally
		{
			Database.safeClose(stmt);
			Database.safeClose(keyRS);
		}
	}
	
	/**
	 * Update an existing User's completedBatches and/or currentBatch in the database
	 * @param userParams The User to be updated
	 * @throws DatabaseException 
	 */
	public void update(User userParams) throws DatabaseException
	{
		PreparedStatement stmt = null;
		ResultSet keyRS = null;
		try
		{
			String query = "update user set completedBatches = (?), currentBatch = (?) where id = (?)";
			stmt = db.getConnection().prepareStatement(query);
			stmt.setInt(1, userParams.getCompletedBatches());
			stmt.setInt(2, userParams.getCurrentBatchID());
			stmt.setInt(3, userParams.getId());
			
			if(stmt.executeUpdate() != 1)
			{
				throw new DatabaseException("Could not update user");
			}
		}
		catch(SQLException e)
		{
			throw new DatabaseException("Could not update user",e);
		}
		finally
		{
			Database.safeClose(stmt);
			Database.safeClose(keyRS);
		}
	}	

	/**
	 * Search database and check if <code>userName</code> and <code>password</code> are a valid pair
	 * @param userName Entered user name
	 * @param password Entered password
	 * @return <code>User</code> object if valid, <code>null</code> otherwise
	 * @throws DatabaseException 
	 */
	public User isValidUser(String userName, String password) throws DatabaseException
	{
		logger.entering("server.database.UserDAO", "isValidUser");
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			String query = "select * from user where username = (?)";
			stmt = db.getConnection().prepareStatement(query);
			stmt.setString(1, userName);
			
			rs = stmt.executeQuery();
			
			if(!rs.isBeforeFirst())
			{
				return null;		//Username not found
			}
			
			
			if(password.equals(rs.getString(3)))
			{
				User tmpUser = new User(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getInt(7));
				tmpUser.setCurrentBatchID(rs.getInt(8));
				return tmpUser;
			}
		}
		catch(SQLException e)
		{
			DatabaseException ex = new DatabaseException();
			logger.throwing("server.database.UserDAO", "isValidUser", ex);
			throw ex;
		}
		finally
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}
		
		logger.exiting("server.database.UserDAO", "isValidUser");
		
		return null;
	}
	
	/**
	 * Check if any user in the database is assigned to this batch
	 * @param batchId Foreign batch id to check
	 * @return <code>true</code> if exists user who has this id, <code>false</code> otherwise.
	 */
	public boolean isAssignedToUser(int batchId) throws DatabaseException
	{
		logger.entering("server.database.UserDAO", "isAssignedToUser");
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			String query = "select currentBatch from user where currentBatch = (?)";
			stmt = db.getConnection().prepareStatement(query);
			
			stmt.setInt(1, batchId);
			rs = stmt.executeQuery();
			
			logger.exiting("server.database.UserDAO", "isAssignedToUser");

			if(!rs.isBeforeFirst())	//Empty result set
			{
				
				return false;
			}

			return true;
		}
		catch(SQLException e)
		{
			DatabaseException ex = new DatabaseException();
			logger.throwing("server.database.UserDAO", "isAssignedToUser", ex);
			throw ex;
		}
		finally
		{
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}
	}
}
