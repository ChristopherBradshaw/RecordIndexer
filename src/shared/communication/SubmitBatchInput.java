package shared.communication;

import java.util.ArrayList;
import java.util.List;

public class SubmitBatchInput
{
	private ValidateUserInput validateUser;
	private int batch;
	private List<ArrayList<String>> values;
	/**
	 * @param validateUser username/password information
	 * @param batchId Batch being submitted
	 * @param values Indexed values in the Batch
	 */
	public SubmitBatchInput(ValidateUserInput validateUser, int batchId,
			List<ArrayList<String>> values)
	{
		this.validateUser = validateUser;
		this.batch = batchId;
		this.values = values;
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
	 * @return the batch
	 */
	public int getBatch()
	{
		return batch;
	}
	/**
	 * @param batch the batch to set
	 */
	public void setBatch(int batch)
	{
		this.batch = batch;
	}
	/**
	 * @return the values
	 */
	public List<ArrayList<String>> getValues()
	{
		return values;
	}
	/**
	 * @param values the values to set
	 */
	public void setValues(List<ArrayList<String>> values)
	{
		this.values = values;
	}
	
	
	
}
