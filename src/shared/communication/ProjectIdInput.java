package shared.communication;

public class ProjectIdInput
{
	private int projectId;
	
	/**
	 * 
	 * @param projectId Foreign key for a Project object
	 */
	public ProjectIdInput(int projectId)
	{
		this.projectId = projectId;
	}

	/**
	 * @return the projectId
	 */
	public int getProjectId()
	{
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(int projectId)
	{
		this.projectId = projectId;
	}
	
}
