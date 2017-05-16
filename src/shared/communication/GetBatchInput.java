package shared.communication;

public class GetBatchInput
{
	private ValidateUserInput validateUser;
	private int projectId;
	
	/**
	 * @param validateUser Holds info for username/password combination
	 * @param projectId Foreign key to a Project
	 */
	public GetBatchInput(ValidateUserInput validateUser, int projectId)
	{
		this.validateUser = validateUser;
		this.projectId = projectId;
	}

	/**
	 * @return the validateUser
	 */
	public ValidateUserInput getValidateUser()
	{
		return validateUser;
	}

	/**
	 * @param validateUser the validateUser to set
	 */
	public void setValidateUser(ValidateUserInput validateUser)
	{
		this.validateUser = validateUser;
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
	
	/**
	 * @return String representation of output
	 */
	public String toString()
	{
		return null;
	}
	
}
