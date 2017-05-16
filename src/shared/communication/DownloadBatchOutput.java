package shared.communication;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import shared.model.Batch;
import shared.model.Field;
import shared.model.Project;
import client.communication.ClientCommunicator;

@SuppressWarnings("serial")
public class DownloadBatchOutput implements Serializable
{
	private Batch batch;
	private Project project;
	private List<Field> fields;
	/**
	 * @param b Batch to download
	 */
	public DownloadBatchOutput(Batch batch, Project project, List<Field> fields)
	{
		this.batch = batch;
		this.project = project;
		this.fields = fields;
	}

	
	/**
	 * @return the batch
	 */
	public Batch getBatch()
	{
		return batch;
	}


	/**
	 * @param batch the batch to set
	 */
	public void setBatch(Batch batch)
	{
		this.batch = batch;
	}


	/**
	 * @return the project
	 */
	public Project getProject()
	{
		return project;
	}


	/**
	 * @param project the project to set
	 */
	public void setProject(Project project)
	{
		this.project = project;
	}


	/**
	 * @return the fields
	 */
	public List<Field> getFields()
	{
		return fields;
	}


	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<Field> fields)
	{
		this.fields = fields;
	}


	/**
	 * @return String representation of output
	 */
	public String toString()
	{
		StringBuilder ss = new StringBuilder();
		ss.append(batch.getId() + "\n");
		ss.append(project.getId() + "\n");
		ss.append(ClientCommunicator.getURL() + File.separator + batch.getFilePath() + "\n");
		ss.append(project.getFirstYCoord() + "\n");
		ss.append(project.getRecordHeight() + "\n");
		ss.append(project.getRecordsPerImage() + "\n");
		ss.append(fields.size() + "\n");
		
		for(int i = 0; i < fields.size(); ++i)
		{
			Field f = fields.get(i);
			ss.append(f.getId() + "\n");
			ss.append((i+1) + "\n");
			ss.append(f.getTitle() + "\n");
			ss.append(ClientCommunicator.getURL() + File.separator + f.getHelpHtml() + "\n");
			ss.append(f.getxCoord() + "\n");
			ss.append(f.getWidth() + "\n");
			
			if(!f.getKnownData().equals(""))
			{
				ss.append(ClientCommunicator.getURL() + File.separator + f.getKnownData() + "\n");
			}
		}

		return ss.toString();
	}	
}
