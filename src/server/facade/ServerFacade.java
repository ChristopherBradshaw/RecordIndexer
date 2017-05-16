package server.facade;

import java.util.ArrayList;
import java.util.List;

import server.ServerException;
import server.database.Database;
import server.database.DatabaseException;
import shared.model.Batch;
import shared.model.Field;
import shared.model.IndexedData;
import shared.model.Project;
import shared.model.User;

public class ServerFacade
{
	public static void initialize() throws ServerException 
	{
		try
		{
			Database.initialize();
		}
		catch(DatabaseException e)
		{
			throw new ServerException(e.getMessage(),e);
		}
	}
	
	/**
	 * Validate a user
	 * @param username Entered username
	 * @param password Entered password
	 * @return <code>User</code> if valid pair, <code>null</code> otherwise
	 * @throws ServerException If could not access database
	 */
	public static User isValidUser(String username, String password) throws ServerException
	{
		Database d = new Database();
		try
		{
			d.startTransaction();
			User valid = d.getUserDAO().isValidUser(username, password);
			d.endTransaction(true);
			return valid;
		}
		catch(DatabaseException e)
		{
			d.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	/**
	 * Get all projects
	 * @return A <code>List</code> of all <code>Projects</code> in this database
	 * @throws ServerException If could not access database
	 */
	public static List<Project> getProjects() throws ServerException
	{
		Database d = new Database();
		try
		{
			d.startTransaction();
			List<Project> projects = d.getProjectDAO().getAll();
			d.endTransaction(true);
			return projects;
		}
		catch(DatabaseException e)
		{
			d.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	/**
	 * Get a sample <code>Batch</code> from a <code>Project</code>
	 * @param projectId Id denoting <code>Project</code> to get sample from
	 * @return A Path to sample <code>Batch</code> if success, "FAILED" otherwise
	 * @throws ServerException If could not access database
	 */
	public static String getSampleBatch(int projectId) throws ServerException
	{
		Database d = new Database();
		try
		{
			d.startTransaction();
			Batch sample = d.getBatchDAO().getSampleBatch(projectId);
			String output = null;
			if(sample != null)
			{	
				output = sample.getFilePath();
			}
			d.endTransaction(true);
			return output;
		}
		catch(DatabaseException e)
		{
			d.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	/**
	 * Get a downloaded <code>Batch</code> from a <code>Project</code>
	 * @param projectId Id denoting <code>Project</code> to get <code>Batch</code> from
	 * @return A <code>Batch</code> to download
	 * @throws ServerException If could not access database
	 */
	public static Batch downloadBatch(int projectId) throws ServerException
	{
		Database d = new Database();
		try
		{
			d.startTransaction();
			Batch output = d.getBatchDAO().downloadBatch(projectId);
			d.endTransaction(true);
			return output;
		}
		catch(DatabaseException e)
		{
			d.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	/**
	 * Submit indexed values
	 * @param data Data to be added
	 * @param batchId Id of batch
	 * @return <code>true</code> if added successfully
	 * @throws ServerException If could not access database
	 */
	public static boolean submitBatch(List<ArrayList<String>> data, int batchId) throws ServerException
	{
		Database d = new Database();
		try
		{
			d.startTransaction();
			Batch tmpBatch = d.getBatchDAO().getBatch(batchId);
			
			//Parse input to make sure valid----
			Project tmpProject = d.getProjectDAO().getProject(tmpBatch.getForeignProjectId());
			if(tmpProject.getRecordsPerImage() != data.size())
			{
				d.endTransaction(false);
				throw new ServerException("Invalid text value input");
			}
			
			for(ArrayList<String> tmpArr : data)
			{
				if(d.getFieldDAO().getFieldsInProject(tmpProject.getId()).size() != tmpArr.size())
				{
					d.endTransaction(false);
					throw new ServerException("Invalid text value input");
				}
			}
			
			//----------------------------------
			
			for(int i = 0; i < data.size(); ++i)
			{
				for(int j = 0; j < data.get(i).size(); ++j)
				{
					String txt = data.get(i).get(j);
					String fPath = tmpBatch.getFilePath();
					int fieldId = d.getFieldDAO().getFieldsInProject(tmpBatch.getForeignProjectId()).get(j).getId();
			
					IndexedData tmp = new IndexedData(-1,txt,fPath,fieldId,batchId,i);
					d.getIndexedDataDAO().add(tmp);
				}
			}
			
			d.endTransaction(true);
			return true;
		}
		catch(DatabaseException e)
		{
			d.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	

	/**
	 * Get all the <code>Fields</code> in a project
	 * @param projectId Id of a <code>Project</code> to get <code>Fields</code> from
	 * @return <code>List</code> of <code>Fields</code>
	 * @throws ServerException If could not access database
	 */
	public static List<Field> getFields(int projectId) throws ServerException
	{
		Database d = new Database();
		try
		{
			d.startTransaction();
			List<Field> fields = d.getFieldDAO().getFieldsInProject(projectId);
			d.endTransaction(true);
			
			return fields;
		}
		catch(DatabaseException e)
		{
			d.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	/**
	 * Search existing data
	 * @param fields Field ids to search for
	 * @param keywords Case-insensitive strings to search for
	 * @return A <code>List</code> of <code>IndexedData</code> 
	 * @throws ServerException If could not access database
	 */
	public static List<IndexedData> search(List<Integer> fields, List<String> keywords) throws ServerException
	{
		Database d = new Database();
		try
		{
			d.startTransaction();
			List<IndexedData> data = d.getIndexedDataDAO().search(fields, keywords);
			d.endTransaction(true);
			
			if(data.size() == 0 || fields.size() == 0 || keywords.size() == 0)
				return null;

			return data;
		}
		catch(DatabaseException e)
		{
			d.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	/**
	 * Update a user
	 * @param userParams <code>User</code> to update
	 * @throws ServerException 
	 */
	public static void updateUser(User userParams) throws ServerException
	{
		Database d = new Database();
		try
		{
			d.startTransaction();
			d.getUserDAO().update(userParams);
			d.endTransaction(true);
		}
		catch(DatabaseException e)
		{
			d.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
}
