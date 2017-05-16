package shared.communication;

import java.util.List;

public class SearchInput
{
	private ValidateUserInput validateUser;
	private List<Integer> fieldIds;
	private List<String> keywords;

	/**
	 * @param fieldIds Foreign Field object ids
	 * @param keywords Words to search for
	 */
	public SearchInput(ValidateUserInput validateUser, List<Integer> fieldIds, List<String> keywords)
	{
		this.validateUser = validateUser;
		this.fieldIds = fieldIds;
		this.keywords = keywords;
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
	 * @return the fieldIds
	 */
	public List<Integer> getFieldIds()
	{
		return fieldIds;
	}

	/**
	 * @param fieldId
	 *            the fieldId to set
	 */
	public void setFieldIds(List<Integer> fieldId)
	{
		this.fieldIds = fieldId;
	}

	/**
	 * @return the keywords
	 */
	public List<String> getKeywords()
	{
		return keywords;
	}

	/**
	 * @param keywords
	 *            the keywords to set
	 */
	public void setKeywords(List<String> keywords)
	{
		this.keywords = keywords;
	}

}
