package shared.communication;


public class GetFieldsInput
{
	private GetBatchInput batchInput;
	private ValidateUserInput validateUser;
	
	/**
	 * Create a new instance with a specific project specified
	 * @param batchInput username/password/projectId information
	 */
	public GetFieldsInput(GetBatchInput batchInput)
	{
		this.batchInput = batchInput;
		this.validateUser = null;
	}
	
	/**
	 * Create a new instance without a project specified
	 * @param validateUser username/password information
	 */
	public GetFieldsInput(ValidateUserInput validateUser)
	{
		this.batchInput = null;
		this.validateUser = validateUser;
	}

	/**
	 * @return the batchInput
	 */
	public GetBatchInput getBatchInput()
	{
		return batchInput;
	}

	/**
	 * @param batchInput the batchInput to set
	 */
	public void setBatchInput(GetBatchInput batchInput)
	{
		this.batchInput = batchInput;
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
	
	
}
